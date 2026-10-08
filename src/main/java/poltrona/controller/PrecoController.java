package poltrona.controller;

import org.springdoc.core.annotations.ParameterObject;
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
import poltrona.controller.doc.PrecoControllerDoc;
import poltrona.dto.preco.AtualizaPrecoRequestDTO;
import poltrona.dto.preco.PrecoRequestDTO;
import poltrona.dto.preco.PrecoResponseDTO;
import poltrona.service.PrecoService;

@RestController
@RequestMapping("/precos")
public class PrecoController implements PrecoControllerDoc {

    private final PrecoService precoService;

    public PrecoController(PrecoService precoService) {
        this.precoService = precoService;
    }

    @Override
    @PostMapping
    public ResponseEntity<PrecoResponseDTO> cadastrar(@Valid @RequestBody PrecoRequestDTO dto) {
        PrecoResponseDTO preco = precoService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(preco);
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<PrecoResponseDTO>> listarTodos(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "valor", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<PrecoResponseDTO> precos = precoService.listarTodos(pageable);
        return ResponseEntity.ok(precos);
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<PrecoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizaPrecoRequestDTO dto) {
        PrecoResponseDTO preco = precoService.atualizar(id, dto);
        return ResponseEntity.ok(preco);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        precoService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PrecoResponseDTO> buscarPorId(@PathVariable Long id) {
        PrecoResponseDTO preco = precoService.buscarPorId(id);
        return ResponseEntity.ok(preco);
    }

    @Override
    @GetMapping("/cinema/{cinemaId}")
    public ResponseEntity<Page<PrecoResponseDTO>> buscarPorCinema(
            @PathVariable Long cinemaId,
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<PrecoResponseDTO> precos = precoService.buscarPorCinema(cinemaId, pageable);
        return ResponseEntity.ok(precos);
    }
}