package poltrona.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import poltrona.dto.poltrona.PoltronaResponseDTO;
import poltrona.dto.poltrona.TipoPoltronaRequestDTO;
import poltrona.service.PoltronaService;

@Tag(name = "Poltronas", description = "Gerenciamento e consulta de poltronas das salas")
@RestController
@RequestMapping("/poltronas")
public class PoltronaController {

    private final PoltronaService poltronaService;

    public PoltronaController(PoltronaService poltronaService) {
        this.poltronaService = poltronaService;
    }

    @Operation(summary = "Buscar poltrona por ID", description = "Busca as informações detalhadas de uma poltrona específica pelo identificador")
    @GetMapping("/{id}")
    public ResponseEntity<PoltronaResponseDTO> buscarPorId(@PathVariable Long id) {

        PoltronaResponseDTO poltrona = poltronaService.buscarPorId(id);

        return ResponseEntity.status(HttpStatus.OK).body(poltrona);

    }

    @Operation(summary = "Listar poltronas por sala", description = "Retorna a lista de todas as poltronas pertencentes a uma determinada sala")
    @GetMapping("/sala/{salaId}")
    public ResponseEntity<List<PoltronaResponseDTO>> listarPorSala(@PathVariable Long salaId) {

        List<PoltronaResponseDTO> poltronas = poltronaService.listarPorSala(salaId);

        return ResponseEntity.status(HttpStatus.OK).body(poltronas);

    }

    @Operation(summary = "Atualizar tipo da poltrona", description = "Atualiza a categoria ou tipo de uma poltrona (ex: VIP, Convencional, Acessível)")
    @PatchMapping("/{id}")
    public ResponseEntity<PoltronaResponseDTO> atualizarTipo(@PathVariable Long id,
            @Valid @RequestBody TipoPoltronaRequestDTO tipo) {

        PoltronaResponseDTO poltrona = poltronaService.atualizarTipo(id, tipo);

        return ResponseEntity.status(HttpStatus.OK).body(poltrona);

    }

    @Operation(summary = "Alterar status da poltrona", description = "Ativa ou desativa o status de disponibilidade de uma poltrona via query parameter")
    @PatchMapping("/{id}/status")
    public ResponseEntity<PoltronaResponseDTO> alterarStatus(@PathVariable Long id,
            @RequestParam Boolean ativa) {

        PoltronaResponseDTO poltrona = poltronaService.alterarStatus(id, ativa);

        return ResponseEntity.status(HttpStatus.OK).body(poltrona);

    }

}