package poltrona.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.ingresso.IngressoRequestDTO;
import poltrona.dto.produto.ProdutoRequestDTO;
import poltrona.dto.venda.VendaRequestDTO;
import poltrona.dto.venda.VendaResponseDTO;
import poltrona.entity.Ingresso;
import poltrona.entity.ItemVenda;
import poltrona.entity.Produto;
import poltrona.entity.Usuario;
import poltrona.entity.Venda;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.ItemVendaMapper;
import poltrona.mapper.VendaMapper;
import poltrona.repository.ProdutoRepository;
import poltrona.repository.VendaRepository;
import java.util.ArrayList;
import java.util.List;

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

        if (dto.ingressos() != null) {
            for (IngressoRequestDTO ingressoDto : dto.ingressos()) {
                Ingresso ingresso = ingressoService.cadastrar(ingressoDto);

                ItemVenda itemIngresso = itemVendaMapper.toEntityIngresso(
                        ingresso,
                        "Ingresso - " + ingresso.getTipo());
                itens.add(itemIngresso);
            }
        }

        if (dto.produtos() != null) {
            for (ProdutoRequestDTO produtoDto : dto.produtos()) {
                Produto produto = produtoRepository.findById(produtoDto.id())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Produto não encontrado ID: " + produtoDto.id()));

                ItemVenda itemProduto = itemVendaMapper.toEntityProduto(produto, produtoDto.quantidade());
                itens.add(itemProduto);
            }
        }

        venda.adicionarItens(itens);

        Venda salva = vendaRepository.save(venda);

        return vendaMapper.toDTO(salva);
    }

    @Transactional(readOnly = true)
    public Page<VendaResponseDTO> listarTodas(Pageable pageable) {
        return vendaRepository.findAll(pageable).map(vendaMapper::toDTO);
    }
}