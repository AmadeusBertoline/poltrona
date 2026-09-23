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
import poltrona.dto.gerente.AtualizaGerenteRequestDTO;
import poltrona.dto.gerente.GerenteRequestDTO;
import poltrona.dto.gerente.GerenteResponseDTO;
import poltrona.dto.usuario.AtualizaSenhaRequestDTO;
import poltrona.service.GerenteService;

@RestController
@RequestMapping("/gerentes")
public class GerenteController {

    private final GerenteService gerenteService;

    public GerenteController(GerenteService gerenteService) {
        this.gerenteService = gerenteService;
    }

    @PostMapping
    public ResponseEntity<GerenteResponseDTO> cadastrar(@Valid @RequestBody GerenteRequestDTO dto) {

        GerenteResponseDTO gerente = gerenteService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(gerente);

    }

    @GetMapping
    public ResponseEntity<Page<GerenteResponseDTO>> listarTodos(
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<GerenteResponseDTO> gerentes = gerenteService.listarTodos(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(gerentes);

    }

    @GetMapping("/me")
    public ResponseEntity<GerenteResponseDTO> me() {

        GerenteResponseDTO gerente = gerenteService.me();

        return ResponseEntity.status(HttpStatus.OK).body(gerente);

    }

    @PatchMapping
    public ResponseEntity<GerenteResponseDTO> atualizar(@Valid @RequestBody AtualizaGerenteRequestDTO dto) {

        GerenteResponseDTO gerente = gerenteService.atualizar(dto);

        return ResponseEntity.status(HttpStatus.OK).body(gerente);

    }

    @PatchMapping("/senha")
    public ResponseEntity<Void> atualizarSenha(@Valid @RequestBody AtualizaSenhaRequestDTO dto) {

        gerenteService.atualizarSenha(dto);

        return ResponseEntity.noContent().build();

    }

    @DeleteMapping
    public ResponseEntity<Void> encerrar() {

        gerenteService.encerrar();

        return ResponseEntity.noContent().build();

    }

    @GetMapping("/{id}")
    public ResponseEntity<GerenteResponseDTO> buscarPorId(@PathVariable Long id) {

        GerenteResponseDTO gerente = gerenteService.buscarPorId(id);

        return ResponseEntity.status(HttpStatus.OK).body(gerente);

    }

}