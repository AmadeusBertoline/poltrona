package poltrona.controller;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import poltrona.dto.sala.AtualizaSalaRequestDTO;
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

    @PostMapping
    public ResponseEntity<SalaResponseDTO> cadastrar(@Valid @RequestBody SalaRequestDTO dto) {

        SalaResponseDTO sala = salaService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(sala);

    }

    @GetMapping
    public ResponseEntity<Page<SalaResponseDTO>> listar(
            @RequestParam(required = false) Long cinemaId,
            @RequestParam(required = false) Boolean ativo,
            @PageableDefault(page = 0, size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<SalaResponseDTO> salas = salaService.listar(cinemaId, ativo, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(salas);

    }

    @GetMapping("/{id}")
    public ResponseEntity<SalaResponseDTO> buscarPorId(@PathVariable Long id) {

        SalaResponseDTO sala = salaService.buscarPorId(id);

        return ResponseEntity.status(HttpStatus.OK).body(sala);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<SalaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizaSalaRequestDTO dto) {

        SalaResponseDTO sala = salaService.atualizar(id, dto);

        return ResponseEntity.status(HttpStatus.OK).body(sala);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {

        salaService.desativar(id);

        return ResponseEntity.noContent().build();

    }

}