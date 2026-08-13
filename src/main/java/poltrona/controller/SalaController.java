package poltrona.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;gita 
import poltrona.dto.sala.SalaRequestDTO;
import poltrona.dto.sala.SalaResponseDTO;
import poltrona.service.SalaService;

@RestController
@RequestMapping("/salas")
public class SalaController {

    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<SalaResponseDTO> cadastrar(@RequestBody SalaRequestDTO dto) {

        SalaResponseDTO sala = salaService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(sala);

    }

    @GetMapping("/listar-todas")
    public ResponseEntity<List<SalaResponseDTO>> listarTodas() {

        List<SalaResponseDTO> salas = salaService.listarTodas();

        return ResponseEntity.status(HttpStatus.OK).body(salas);

    }

}
