package poltrona.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import poltrona.dto.ingresso.IngressoRequestDTO;
import poltrona.dto.produto.ProdutoRequestDTO;
import poltrona.dto.venda.VendaRequestDTO;
import poltrona.dto.venda.VendaResponseDTO;
import poltrona.entity.Cliente;
import poltrona.entity.Ingresso;
import poltrona.entity.ItemVenda;
import poltrona.entity.Usuario;
import poltrona.entity.Venda;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.ItemVendaMapper;
import poltrona.mapper.VendaMapper;
import poltrona.repository.VendaRepository;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final VendaMapper vendaMapper;
    private final ItemVendaMapper itemVendaMapper;
    private final UsuarioService usuarioService;
    private final IngressoService ingressoService;
    private final ProdutoService produtoService;
    private final AcessoCinemaService acessoCinemaService;
    private final PdfService pdfService;

    public VendaService(
            VendaRepository vendaRepository,
            VendaMapper vendaMapper,
            ItemVendaMapper itemVendaMapper,
            UsuarioService usuarioService,
            IngressoService ingressoService,
            ProdutoService produtoService,
            AcessoCinemaService acessoCinemaService,
            PdfService pdfService) {
        this.vendaRepository = vendaRepository;
        this.vendaMapper = vendaMapper;
        this.itemVendaMapper = itemVendaMapper;
        this.usuarioService = usuarioService;
        this.ingressoService = ingressoService;
        this.produtoService = produtoService;
        this.acessoCinemaService = acessoCinemaService;
        this.pdfService = pdfService;

    }

    @Transactional(readOnly = true)
    public byte[] gerarPdfComprovanteVenda(Long id) {

        Usuario usuario = usuarioService.usuarioLogado();

        Venda venda = vendaRepository.findById(id)
                .orElseThrow((() -> new ResourceNotFoundException("Venda não encontrada de id " + id)));

        acessoCinemaService.validarAcessoVenda(usuario, venda);

        VendaResponseDTO dto = vendaMapper.toDTO(venda);
        return pdfService.gerarComprovanteVenda(dto);
    }

    @Transactional
    public VendaResponseDTO cadastrar(VendaRequestDTO dto) {

        if (dto.ingressos() == null || dto.ingressos().isEmpty()) {
            throw new RegraNegocioException("Nenhum ingresso selecionado");
        }
        if (dto.produtos() != null) {
            Set<Long> produtos = new HashSet<>();
            if (dto.produtos().stream().anyMatch(produto -> !produtos.add(produto.id()))) {
                throw new RegraNegocioException("Cada produto deve aparecer uma única vez na venda.");
            }
        }

        Usuario usuario = usuarioService.usuarioLogado();
        if (!(usuario instanceof Cliente cliente)) {
            throw new AccessDeniedException("Somente clientes podem realizar vendas.");
        }
        Venda venda = vendaMapper.toEntity(dto, cliente);
        List<ItemVenda> itens = new ArrayList<>();
        Long cinemaIdVenda = null;

        // ordenar ingressos (o cadastro de ingressos bloqueia poltronas)
        List<IngressoRequestDTO> ingressosOrdenados = dto.ingressos().stream()
                .sorted(Comparator.comparing(IngressoRequestDTO::idPoltrona))
                .toList();

        for (IngressoRequestDTO ingressoDto : ingressosOrdenados) {
            Ingresso ingresso = ingressoService.cadastrar(ingressoDto);
            Long cinemaIdIngresso = ingresso.getSessao().getSala().getCinema().getId();
            if (cinemaIdVenda != null && !cinemaIdVenda.equals(cinemaIdIngresso)) {
                throw new RegraNegocioException("Todos os ingressos da venda devem pertencer ao mesmo cinema.");
            }
            cinemaIdVenda = cinemaIdIngresso;

            ItemVenda itemIngresso = itemVendaMapper.toEntityIngresso(
                    ingresso,
                    "Ingresso - " + ingresso.getTipo());
            itens.add(itemIngresso);
        }

        // ordenar produtos antes de dar update no banco
        if (dto.produtos() != null && !dto.produtos().isEmpty()) {

            List<ProdutoRequestDTO> produtosOrdenados = dto.produtos().stream()
                    .sorted(Comparator.comparing(ProdutoRequestDTO::id))
                    .toList();

            for (ProdutoRequestDTO produtoDto : produtosOrdenados) {

                poltrona.entity.Produto produto = produtoService.reduzirEstoqueParaVenda(
                        produtoDto.id(),
                        cinemaIdVenda,
                        produtoDto.quantidade());

                ItemVenda itemProduto = itemVendaMapper.toEntityProduto(
                        produto,
                        produtoDto.quantidade());

                itens.add(itemProduto);
            }
        }

        venda.adicionarItens(itens);
        Venda salva = vendaRepository.save(venda);

        return vendaMapper.toDTO(salva);
    }

    @Transactional(readOnly = true)
    public VendaResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioService.usuarioLogado();
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada de id " + id));
        acessoCinemaService.validarAcessoVenda(usuario, venda);
        return vendaMapper.toDTO(venda);
    }

    @Transactional(readOnly = true)
    public Page<VendaResponseDTO> listarTodas(Long clienteId, Pageable pageable) {
        Usuario usuario = usuarioService.usuarioLogado();
        if (usuario instanceof poltrona.entity.Admin) {
            return vendaRepository.buscarVendas(clienteId, null, pageable).map(vendaMapper::toDTO);
        }
        if (usuario instanceof poltrona.entity.Gerente gerente) {
            return vendaRepository.buscarVendas(clienteId, gerente.getCinema().getId(), pageable)
                    .map(vendaMapper::toDTO);
        }
        if (usuario instanceof poltrona.entity.Proprietario proprietario) {
            return vendaRepository.findAllByProprietarioId(proprietario.getId(), clienteId, pageable)
                    .map(vendaMapper::toDTO);
        }
        throw new AccessDeniedException("Você não tem permissão para listar vendas.");
    }

    @Transactional
    public VendaResponseDTO cancelar(Long id) {
        Usuario usuario = usuarioService.usuarioLogado();
        if (!(usuario instanceof Cliente cliente)) {
            throw new AccessDeniedException("Somente clientes podem cancelar vendas.");
        }
        Venda venda = vendaRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada de id " + id));

        if (!venda.getCliente().getId().equals(cliente.getId())) {
            throw new AccessDeniedException("Você só pode cancelar suas próprias vendas.");
        }

        venda.cancelar();
        for (Ingresso ingresso : venda.getIngressos()) {
            ingressoService.cancelarIngresso(ingresso);
        }
        for (ItemVenda item : venda.getItens()) {
            if (item.getProduto() != null) {
                produtoService.devolverEstoque(item.getProduto().getId(), item.getQuantidade());
            }
        }

        return vendaMapper.toDTO(venda);
    }

    @Transactional(readOnly = true)
    public Page<VendaResponseDTO> me(Pageable pageable) {

        Cliente cliente = (Cliente) usuarioService.usuarioLogado();

        return vendaRepository.findByCliente(cliente, pageable).map(vendaMapper::toDTO);

    }
}