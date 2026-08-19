package poltrona.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import poltrona.dto.filme.FilmeRequestDTO;
import poltrona.dto.filme.FilmeResponseDTO;
import poltrona.service.FilmeService;

@Tag(name = "Filmes", description = "Gerenciamento de filmes")
@RestController
@RequestMapping("/filmes")
public class FilmeController {

    private final FilmeService filmeService;

    public FilmeController(FilmeService filmeService) {

        this.filmeService = filmeService;

    }

    @PostMapping
    public ResponseEntity<FilmeResponseDTO> cadastrar(@RequestBody @Valid FilmeRequestDTO dto) {

        FilmeResponseDTO filme = filmeService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(filme);

    }

    @GetMapping
    public ResponseEntity<Page<FilmeResponseDTO>> listarTodos(
            @PageableDefault(page = 0, size = 10, sort = "titulo", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<FilmeResponseDTO> lista = filmeService.listarTodos(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(lista);

    }

    @PatchMapping("/{id}/atualizar")
    public ResponseEntity<FilmeResponseDTO> atualizar(Long id, FilmeRequestDTO dto) {

        FilmeResponseDTO filme = filmeService.atualizar(id, dto);

        return ResponseEntity.status(HttpStatus.OK).body(filme);

    }

}
