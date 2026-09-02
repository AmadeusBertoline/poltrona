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
import poltrona.dto.preco.AtualizaPrecoRequestDTO;
import poltrona.dto.preco.PrecoRequestDTO;
import poltrona.dto.preco.PrecoResponseDTO;
import poltrona.service.PrecoService;

@RestController
@RequestMapping("/precos")
public class PrecoController {

    private final PrecoService precoService;

    public PrecoController(PrecoService precoService) {
        this.precoService = precoService;
    }

    @PostMapping
    public ResponseEntity<PrecoResponseDTO> cadastrar(@RequestBody @Valid PrecoRequestDTO dto) {

        PrecoResponseDTO preco = precoService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(preco);

    }

    @GetMapping
    public ResponseEntity<Page<PrecoResponseDTO>> listarTodos(
            @PageableDefault(page = 0, size = 10, sort = "precoBase", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<PrecoResponseDTO> precos = precoService.listarTodos(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(precos);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<PrecoResponseDTO> atualizar(@PathVariable Long id, @RequestBody AtualizaPrecoRequestDTO dto) {

        PrecoResponseDTO preco = precoService.atualizar(id, dto);

        return ResponseEntity.status(HttpStatus.OK).body(preco);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {

        precoService.desativar(id);

        return ResponseEntity.noContent().build();

    }

    @GetMapping("/{id}")
    public ResponseEntity<Page<PrecoResponseDTO>> buscarPorCinema(@PathVariable Long id,
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<PrecoResponseDTO> precos = precoService.buscarPorCinema(id, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(precos);

    }

}
