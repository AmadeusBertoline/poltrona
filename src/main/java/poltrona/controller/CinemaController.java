package poltrona.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import poltrona.dto.cinema.AtualizaCinemaRequestDTO;
import poltrona.dto.cinema.CinemaFiltroDTO;
import poltrona.dto.cinema.CinemaRequestDTO;
import poltrona.dto.cinema.CinemaResponseDTO;
import poltrona.service.CinemaService;

@Tag(name = "Cinemas", description = "Gerenciamento de cinemas")
@RestController
@RequestMapping("/cinemas")
public class CinemaController {

    private final CinemaService cinemaService;

    public CinemaController(CinemaService cinemaService) {
        this.cinemaService = cinemaService;
    }

    @Operation(summary = "Cadastrar um cinema", description = "Cadastra um novo cinema no sistema")
    @PostMapping
    public ResponseEntity<CinemaResponseDTO> cadastrar(@Valid @RequestBody CinemaRequestDTO dto) {

        CinemaResponseDTO cinema = cinemaService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(cinema);

    }

    @Operation(summary = "Listar todos os cinemas", description = "Lista todos os cinemas de forma paginada com suporte a filtros de busca")
    @GetMapping
    public ResponseEntity<Page<CinemaResponseDTO>> listarTodos(CinemaFiltroDTO filtro,
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao") Pageable pageable) {

        Page<CinemaResponseDTO> lista = cinemaService.listarTodos(filtro, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(lista);

    }

    @Operation(summary = "Meus cinemas", description = "Lista os cinemas vinculados ao usuário/admin autenticado de forma paginada")
    @GetMapping("/me")
    public ResponseEntity<Page<CinemaResponseDTO>> me(
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<CinemaResponseDTO> cinema = cinemaService.me(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(cinema);

    }

    @Operation(summary = "Buscar cinema por ID", description = "Busca as informações detalhadas de um cinema pelo seu identificador")
    @GetMapping("/{id}")
    public ResponseEntity<CinemaResponseDTO> buscarPorId(@PathVariable Long id) {

        CinemaResponseDTO cinema = cinemaService.buscarPorId(id);

        return ResponseEntity.status(HttpStatus.OK).body(cinema);

    }

    @Operation(summary = "Atualizar cinema", description = "Atualiza os dados cadastrais de um cinema existente pelo ID")
    @PatchMapping("/{id}")
    public ResponseEntity<CinemaResponseDTO> atualizar(@PathVariable Long id,
            @Valid @RequestBody AtualizaCinemaRequestDTO dto) {

        CinemaResponseDTO cinema = cinemaService.atualizar(id, dto);

        return ResponseEntity.status(HttpStatus.OK).body(cinema);

    }

    @Operation(summary = "Deletar cinema", description = "Remove um cinema do sistema pelo seu ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        cinemaService.deletar(id);

        return ResponseEntity.noContent().build();

    }

}