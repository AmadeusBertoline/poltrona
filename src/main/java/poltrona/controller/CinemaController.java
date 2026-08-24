package poltrona.controller;

import java.util.List;
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
    public ResponseEntity<List<CinemaResponseDTO>> listarTodos() {

        List<CinemaResponseDTO> lista = cinemaService.listarTodos();

        return ResponseEntity.status(HttpStatus.OK).body(lista);

    }

}
