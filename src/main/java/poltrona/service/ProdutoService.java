package poltrona.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.page.RespostaPaginadaDTO;
import poltrona.dto.produto.AtualizaProdutoRequestDTO;
import poltrona.dto.produto.CadastroProdutoRequestDTO;
import poltrona.dto.produto.ProdutoResponseDTO;
import poltrona.entity.Cinema;
import poltrona.entity.Gerente;
import poltrona.entity.Produto;
import poltrona.entity.Proprietario;
import poltrona.entity.Usuario;
import poltrona.enums.produto.TipoProduto;
import poltrona.exception.RegraNegocioException;
import poltrona.exception.ResourceAlreadyExistsException;
import poltrona.exception.ResourceNotFoundException;
import poltrona.mapper.ProdutoMapper;
import poltrona.repository.CinemaRepository;
import poltrona.repository.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;
    private final UsuarioService usuarioService;
    private final CinemaRepository cinemaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, ProdutoMapper produtoMapper,
            UsuarioService usuarioService, CinemaRepository cinemaRepository) {
        this.produtoRepository = produtoRepository;
        this.produtoMapper = produtoMapper;
        this.usuarioService = usuarioService;
        this.cinemaRepository = cinemaRepository;
    }

    @CacheEvict(value = "produtos", allEntries = true)
    @Transactional
    public ProdutoResponseDTO cadastrar(CadastroProdutoRequestDTO dto) {

        final Cinema cinema;
        Usuario usuario = usuarioService.usuarioLogado();

        if (usuario instanceof Proprietario proprietario) {

            cinema = cinemaRepository.findByIdAndProprietarioId(dto.cinemaId(), proprietario.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Cinema não encontrado ou não pertence a este proprietário: " + dto.cinemaId()));

        } else if (usuario instanceof Gerente gerente) {

            if (!dto.cinemaId().equals(gerente.getCinema().getId())) {
                throw new RegraNegocioException(
                        "Você não pode cadastrar um produto em um cinema que não opera");
            }

            cinema = gerente.getCinema();

        } else {
            throw new RegraNegocioException("Usuário sem permissão para realizar esta operação.");
        }

        if (produtoRepository.existsByNomeIgnoreCaseAndCinemaId(dto.nome(), cinema.getId())) {
            throw new ResourceAlreadyExistsException("Produto já existente com o nome: " + dto.nome());
        }

        Produto produto = produtoMapper.toEntity(dto, cinema);
        Produto salvo = produtoRepository.save(produto);

        return produtoMapper.toDTO(salvo);
    }

    @Cacheable(value = "produtos", key = "{ #ativo, #nome, #tipoProduto, #pageable.pageNumber, #pageable.pageSize, #pageable.sort.toString() }")
    @Transactional(readOnly = true)
    public RespostaPaginadaDTO<ProdutoResponseDTO> listarTodos(
            Boolean ativo,
            String nome,
            TipoProduto tipoProduto,
            Pageable pageable) {

        Page<ProdutoResponseDTO> paginaResultados = produtoRepository
                .findAllByFiltro(ativo, nome, tipoProduto, pageable)
                .map(produtoMapper::toDTO);

        return RespostaPaginadaDTO.de(paginaResultados);
    }

    @Cacheable(value = "produtoPorId", key = "#id")
    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = buscarProdutoEValidarAcesso(id);
        return produtoMapper.toDTO(produto);
    }

    @Caching(evict = {
            @CacheEvict(value = "produtos", allEntries = true),
            @CacheEvict(value = "produtoPorId", key = "#id")
    })
    @Transactional
    public ProdutoResponseDTO atualizar(Long id, AtualizaProdutoRequestDTO dto) {

        Produto produto = buscarProdutoEValidarAcesso(id);

        if (!produto.getNome().equalsIgnoreCase(dto.nome()) &&
                produtoRepository.existsByNomeIgnoreCaseAndCinemaId(dto.nome(), produto.getCinema().getId())) {
            throw new ResourceAlreadyExistsException("Produto já existente com o nome: " + dto.nome());
        }

        produto.atualizar(dto.nome(), dto.descricao(), dto.preco());

        if (dto.quantidadeEstoque() != null) {
            int diferenca = dto.quantidadeEstoque() - produto.getQuantidadeEstoque();
            if (diferenca > 0) {
                produto.adicionarEstoque(diferenca);
            } else if (diferenca < 0) {
                produto.debitarEstoque(Math.abs(diferenca));
            }
        }

        Produto atualizado = produtoRepository.save(produto);

        return produtoMapper.toDTO(atualizado);
    }

    @Caching(evict = {
            @CacheEvict(value = "produtos", allEntries = true),
            @CacheEvict(value = "produtoPorId", key = "#id")
    })
    @Transactional
    public ProdutoResponseDTO alterarStatus(Long id, Boolean status) {

        Produto produto = buscarProdutoEValidarAcesso(id);

        produto.alterarStatus(status);

        Produto alterado = produtoRepository.save(produto);

        return produtoMapper.toDTO(alterado);
    }

    @Caching(evict = {
            @CacheEvict(value = "produtos", allEntries = true),
            @CacheEvict(value = "produtoPorId", key = "#id")
    })
    @Transactional
    public void deletar(Long id) {

        Produto produto = buscarProdutoEValidarAcesso(id);

        produtoRepository.delete(produto);
    }

    private Produto buscarProdutoEValidarAcesso(Long id) {

        Usuario usuario = usuarioService.usuarioLogado();

        if (usuario instanceof Proprietario proprietario) {

            return produtoRepository.findByIdAndCinemaProprietarioId(id, proprietario.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado de id: " + id));

        } else if (usuario instanceof Gerente gerente) {

            Produto produto = produtoRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado de id: " + id));

            if (!produto.getCinema().getId().equals(gerente.getCinema().getId())) {
                throw new RegraNegocioException(
                        "Você não tem permissão para acessar ou alterar produtos de outro cinema.");
            }

            return produto;

        }

        throw new RegraNegocioException("Usuário sem permissão para realizar esta operação.");
    }
}