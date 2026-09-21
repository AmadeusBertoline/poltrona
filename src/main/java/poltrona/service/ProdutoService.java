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
import poltrona.enums.produto.TipoProduto;
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
    public ProdutoResponseDTO cadastrar(CadastroProdutoRequestDTO dto) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        Cinema cinema = cinemaRepository.findByIdAndProprietarioId(dto.cinemaId(), proprietario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cinema não encontrado de id: " + dto.cinemaId()));

        if (produtoRepository.existsByNomeIgnoreCaseAndCinemaId(dto.nome(), cinema.getId())) {
            throw new ResourceAlreadyExistsException("Produto já existente com o nome: " + dto.nome());
        }

        Produto produto = produtoMapper.toEntity(dto, cinema);

        Produto salvo = produtoRepository.save(produto);

        return produtoMapper.toDTO(salvo);

    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> listarTodos(Boolean ativo, String nome, TipoProduto tipoProduto,
            Pageable pageable) {

        return produtoRepository.findAllByFiltro(ativo, nome, tipoProduto, pageable).map(produtoMapper::toDTO);

    }

    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(Long id) {

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado de id: " + id));

        return produtoMapper.toDTO(produto);

    }

    @Transactional
    public ProdutoResponseDTO atualizar(Long id, AtualizaProdutoRequestDTO dto) {

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
                produtoRepository.reduzirEstoque(id, diferenca);
            }
        }

        Produto atualizado = produtoRepository.save(produto);

        return produtoMapper.toDTO(atualizado);

    }

    @Transactional
    public ProdutoResponseDTO alterarStatus(Long id, Boolean status) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        Produto produto = produtoRepository.findByIdAndCinemaProprietarioId(id, proprietario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado de id: " + id));

        produto.alterarStatus(status);

        Produto alterado = produtoRepository.save(produto);

        return produtoMapper.toDTO(alterado);

    }

    @Transactional
    public void deletar(Long id) {

        Proprietario proprietario = (Proprietario) usuarioService.usuarioLogado();

        Produto produto = produtoRepository.findByIdAndCinemaProprietarioId(id, proprietario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado de id: " + id));

        produtoRepository.delete(produto);

    }

}