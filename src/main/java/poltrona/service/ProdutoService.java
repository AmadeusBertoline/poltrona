package poltrona.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import poltrona.dto.produto.AtualizaProdutoRequestDTO;
import poltrona.dto.produto.CadastroProdutoRequestDTO;
import poltrona.dto.produto.ProdutoResponseDTO;
import poltrona.entity.Cinema;
import poltrona.entity.Produto;
import poltrona.entity.Proprietario;
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

    @Transactional
    public Produto cadastrar(CadastroProdutoRequestDTO dto) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        Cinema cinema = cinemaRepository.findByIdAndProprietarioId(dto.cinemaId(), proprietario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cinema não encontrado de id: " + dto.cinemaId()));

        if (produtoRepository.existsByNomeIgnoreCaseAndCinemaId(dto.nome(), cinema.getId())) {
            throw new ResourceAlreadyExistsException("Produto já existente com o nome: " + dto.nome());
        }

        Produto produto = produtoMapper.toEntity(dto, cinema);

        Produto salvo = produtoRepository.save(produto);

        return salvo;

    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> listarTodos(Pageable pageable) {

        return produtoRepository.findAll(pageable).map(produtoMapper::toDTO);

    }

    @Transactional(readOnly = true)
    public Produto buscarPorId(Long id) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        Produto produto = produtoRepository.findByIdAndCinemaProprietarioId(id, proprietario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado de id: " + id));

        return produto;

    }

    @Transactional
    public Produto atualizar(Long id, AtualizaProdutoRequestDTO dto) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        Produto produto = produtoRepository.findByIdAndCinemaProprietarioId(id, proprietario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado de id: " + id));

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

        return atualizado;

    }

    @Transactional
    public Produto desativar(Long id) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        Produto produto = produtoRepository.findByIdAndCinemaProprietarioId(id, proprietario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado de id: " + id));

        if (!produto.getAtivo()) {
            throw new RegraNegocioException("O produto de id " + id + " já está desativado.");
        }

        produto.desativar();

        Produto desativado = produtoRepository.save(produto);

        return desativado;

    }

    @Transactional
    public void deletar(Long id) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        Produto produto = produtoRepository.findByIdAndCinemaProprietarioId(id, proprietario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado de id: " + id));

        produtoRepository.delete(produto);

    }

}