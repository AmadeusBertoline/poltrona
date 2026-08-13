package poltrona.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
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

    public ResponseEntity<CinemaResponseDTO> cadastrar(@RequestBody @Valid CinemaRequestDTO dto) {

        CinemaResponseDTO cinema = cinemaService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(cinema);

    }

}
