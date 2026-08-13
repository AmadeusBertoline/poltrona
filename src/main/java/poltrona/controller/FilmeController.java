package poltrona.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import poltrona.dto.filme.FilmeRequestDTO;
import poltrona.dto.filme.FilmeResponseDTO;
import poltrona.service.FilmeService;

@Tag(name = "Filmes", description = "Gerenciamento de filmes")
@Controller
@RequestMapping("/filmes")
public class FilmeController {

    private final FilmeService filmeService;

    public FilmeController(FilmeService filmeService) {

        this.filmeService = filmeService;

    }

    @Operation(summary = "Cadastrar filme", description = "Cadastra um filme")
    @PostMapping("/cadastrar")
    public ResponseEntity<FilmeResponseDTO> cadastrar(@RequestBody @Valid FilmeRequestDTO dto) {

        FilmeResponseDTO filme = filmeService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(filme);

    }

    @Operation(summary = "Listar todos os filmes", description = "Lista todos os filmes")
    @GetMapping("/listar-todos")
    public ResponseEntity<List<FilmeResponseDTO>> listarTodos() {

        List<FilmeResponseDTO> lista = filmeService.listarTodos();

        return ResponseEntity.status(HttpStatus.OK).body(lista);

    }

}
