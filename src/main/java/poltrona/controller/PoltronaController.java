package poltrona.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import poltrona.dto.poltrona.PoltronaResponseDTO;
import poltrona.service.PoltronaService;

@RestController
@RequestMapping("/poltronas")
public class PoltronaController {

    private final PoltronaService poltronaService;

    public PoltronaController(PoltronaService poltronaService) {
        this.poltronaService = poltronaService;
    }

    @GetMapping("/listar-por-sala/{numero}")
    public ResponseEntity<List<PoltronaResponseDTO>> listarPorSala(@PathVariable Integer numero) {

        List<PoltronaResponseDTO> poltronas = poltronaService.listarPorSala(numero);

        return ResponseEntity.status(HttpStatus.OK).body(poltronas);

    }

}
