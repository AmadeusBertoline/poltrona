package poltrona.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.ingresso.IngressoRequestDTO;
import poltrona.dto.produto.ProdutoRequestDTO;
import poltrona.dto.venda.VendaRequestDTO;
import poltrona.dto.venda.VendaResponseDTO;
import poltrona.entity.Cliente;
import poltrona.entity.Ingresso;
import poltrona.entity.ItemVenda;
import poltrona.entity.Produto;
import poltrona.entity.Usuario;
import poltrona.entity.Venda;
import poltrona.enums.venda.StatusVenda;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.ItemVendaMapper;
import poltrona.mapper.VendaMapper;
import poltrona.repository.ProdutoRepository;
import poltrona.repository.VendaRepository;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final VendaMapper vendaMapper;
    private final ItemVendaMapper itemVendaMapper;
    private final UsuarioService usuarioService;
    private final IngressoService ingressoService;
    private final ProdutoRepository produtoRepository;

    public VendaService(
            VendaRepository vendaRepository,
            VendaMapper vendaMapper,
            ItemVendaMapper itemVendaMapper,
            UsuarioService usuarioService,
            IngressoService ingressoService,
            ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.vendaMapper = vendaMapper;
        this.itemVendaMapper = itemVendaMapper;
        this.usuarioService = usuarioService;
        this.ingressoService = ingressoService;
        this.produtoRepository = produtoRepository;

    }

    @Transactional
    public VendaResponseDTO cadastrar(VendaRequestDTO dto) {

        Usuario cliente = usuarioService.usuarioLogado();

        Venda venda = vendaMapper.toEntity(dto, cliente);

        List<ItemVenda> itens = new ArrayList<>();

        if (dto.ingressos() == null || dto.ingressos().isEmpty()) {
            throw new RegraNegocioException("Nenhum ingresso selecionado");
        }

        for (IngressoRequestDTO ingressoDto : dto.ingressos()) {
            Ingresso ingresso = ingressoService.cadastrar(ingressoDto);

            ItemVenda itemIngresso = itemVendaMapper.toEntityIngresso(
                    ingresso,
                    "Ingresso - " + ingresso.getTipo());
            itens.add(itemIngresso);
        }

        if (dto.produtos() != null && !dto.produtos().isEmpty()) {

            for (ProdutoRequestDTO produtoDto : dto.produtos()) {

                Produto produto = produtoRepository.findById(produtoDto.id())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Produto não encontrado ID: " + produtoDto.id()));

                int linhasAfetadas = produtoRepository.reduzirEstoque(
                        produtoDto.id(),
                        produtoDto.quantidade());

                if (linhasAfetadas == 0) {
                    throw new RegraNegocioException(
                            "Estoque do produto " + produto.getNome() + " insuficiente");
                }

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
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada de id " + id));
        return vendaMapper.toDTO(venda);
    }

    @Transactional(readOnly = true)
    public Page<VendaResponseDTO> listarTodas(Long clienteId, Pageable pageable) {
        return vendaRepository.buscarVendas(clienteId, pageable)
                .map(vendaMapper::toDTO);
    }

    @Transactional
    public VendaResponseDTO cancelar(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada de id " + id));

        if (venda.getStatus() == StatusVenda.CANCELADA) {
            throw new RegraNegocioException("Esta venda já se encontra cancelada.");
        }

        venda.setStatus(StatusVenda.CANCELADA);

        if (venda.getIngressos() != null) {
            venda.getIngressos().forEach(ingresso -> ingresso.cancelar());
        }

        return vendaMapper.toDTO(venda);
    }

    @Transactional(readOnly = true)
    public Page<VendaResponseDTO> me(Pageable pageable) {

        Cliente cliente = (Cliente) usuarioService.usuarioLogado();

        return vendaRepository.findByCliente(cliente, pageable).map(vendaMapper::toDTO);

    }
}