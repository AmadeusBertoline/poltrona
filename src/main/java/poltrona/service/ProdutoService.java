package poltrona.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.produto.CadastroProdutoRequestDTO;
import poltrona.dto.produto.ProdutoResponseDTO;
import poltrona.entity.Cinema;
import poltrona.entity.Produto;
import poltrona.entity.Proprietario;
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

}
