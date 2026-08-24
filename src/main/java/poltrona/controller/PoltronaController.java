package poltrona.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poltrona.dto.poltrona.PoltronaResponseDTO;
import poltrona.dto.poltrona.TipoPoltronaRequestDTO;
import poltrona.service.PoltronaService;

@RestController
@RequestMapping("/poltronas")
public class PoltronaController {

    private final PoltronaService poltronaService;

    public PoltronaController(PoltronaService poltronaService) {
        this.poltronaService = poltronaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PoltronaResponseDTO> buscarPorId(@PathVariable Long id) {

        PoltronaResponseDTO poltronas = poltronaService.buscarPorId(id);

        return ResponseEntity.status(HttpStatus.OK).body(poltronas);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<PoltronaResponseDTO> atualizarTipo(@PathVariable Long id,
            @RequestBody TipoPoltronaRequestDTO tipo) {

        PoltronaResponseDTO poltrona = poltronaService.atualizarTipo(id, tipo);

        return ResponseEntity.status(HttpStatus.OK).body(poltrona);

    }

}
