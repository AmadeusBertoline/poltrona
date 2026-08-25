package poltrona.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poltrona.dto.cinema.CinemaRequestDTO;
import poltrona.dto.cinema.CinemaResponseDTO;
import poltrona.service.CinemaService;

@RestController
@RequestMapping("/cinemas")
public class CinemaController {

    private final CinemaService cinemaService;

    public CinemaController(CinemaService cinemaService) {
        this.cinemaService = cinemaService;
    }

    @PostMapping
    public ResponseEntity<CinemaResponseDTO> cadastrar(@RequestBody CinemaRequestDTO dto) {

        CinemaResponseDTO cinema = cinemaService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(cinema);

    }

    @GetMapping
    public ResponseEntity<Page<CinemaResponseDTO>> listarTodos(
            @PageableDefault(page = 0, size = 10, sort = "") Pageable pageable) {

        Page<CinemaResponseDTO> lista = cinemaService.listarTodos(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(lista);

    }

    // @GetMapping("/me")
    // public ResponseEntity<CinemaResponseDTO> me() {

    //     CinemaResponseDTO cinema = cinemaService.me();

    //     ResponseEntity.status(HttpStatus.OK).body(cinema);

    // }

}
