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

import jakarta.validation.Valid;
import poltrona.controller.doc.CinemaControllerDoc;
import poltrona.dto.cinema.AtualizaCinemaRequestDTO;
import poltrona.dto.cinema.CinemaFiltroDTO;
import poltrona.dto.cinema.CinemaRequestDTO;
import poltrona.dto.cinema.CinemaResponseDTO;
import poltrona.service.CinemaService;

@RestController
@RequestMapping("/cinemas")
public class CinemaController implements CinemaControllerDoc {

    private final CinemaService cinemaService;

    public CinemaController(CinemaService cinemaService) {
        this.cinemaService = cinemaService;
    }

    @Override
    @PostMapping
    public ResponseEntity<CinemaResponseDTO> cadastrar(@Valid @RequestBody CinemaRequestDTO dto) {
        CinemaResponseDTO cinema = cinemaService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cinema);
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<CinemaResponseDTO>> listarTodos(
            CinemaFiltroDTO filtro,
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao") Pageable pageable) {
        Page<CinemaResponseDTO> lista = cinemaService.listarTodos(filtro, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<Page<CinemaResponseDTO>> me(
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<CinemaResponseDTO> cinema = cinemaService.me(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(cinema);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CinemaResponseDTO> buscarPorId(@PathVariable Long id) {
        CinemaResponseDTO cinema = cinemaService.buscarPorId(id);
        return ResponseEntity.status(HttpStatus.OK).body(cinema);
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<CinemaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizaCinemaRequestDTO dto) {
        CinemaResponseDTO cinema = cinemaService.atualizar(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(cinema);
    }

    @Override
    @PatchMapping("/{id}/encerrar")
    public ResponseEntity<Void> encerrar(@PathVariable Long id) {
        cinemaService.encerrar(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cinemaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}