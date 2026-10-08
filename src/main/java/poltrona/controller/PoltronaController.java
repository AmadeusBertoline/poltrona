package poltrona.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import poltrona.controller.doc.PoltronaControllerDoc;
import poltrona.dto.poltrona.PoltronaResponseDTO;
import poltrona.dto.poltrona.TipoPoltronaRequestDTO;
import poltrona.service.PoltronaService;

@RestController
@RequestMapping("/poltronas")
public class PoltronaController implements PoltronaControllerDoc {

    private final PoltronaService poltronaService;

    public PoltronaController(PoltronaService poltronaService) {
        this.poltronaService = poltronaService;
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PoltronaResponseDTO> buscarPorId(@PathVariable Long id) {
        PoltronaResponseDTO poltrona = poltronaService.buscarPorId(id);
        return ResponseEntity.ok(poltrona);
    }

    @Override
    @GetMapping("/sala/{salaId}")
    public ResponseEntity<List<PoltronaResponseDTO>> listarPorSala(@PathVariable Long salaId) {
        List<PoltronaResponseDTO> poltronas = poltronaService.listarPorSala(salaId);
        return ResponseEntity.ok(poltronas);
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<PoltronaResponseDTO> atualizarTipo(
            @PathVariable Long id,
            @Valid @RequestBody TipoPoltronaRequestDTO tipo) {
        PoltronaResponseDTO poltrona = poltronaService.atualizarTipo(id, tipo);
        return ResponseEntity.ok(poltrona);
    }

    @Override
    @PatchMapping("/{id}/status")
    public ResponseEntity<PoltronaResponseDTO> alterarStatus(
            @PathVariable Long id,
            @RequestParam Boolean ativa) {
        PoltronaResponseDTO poltrona = poltronaService.alterarStatus(id, ativa);
        return ResponseEntity.ok(poltrona);
    }

    @Override
    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        poltronaService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}