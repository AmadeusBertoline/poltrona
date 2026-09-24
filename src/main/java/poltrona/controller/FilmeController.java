package poltrona.controller;

import java.util.List;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import poltrona.dto.filme.FilmeFiltroDTO;
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

    @Operation(summary = "Cadastrar filme", description = "Cadastra um novo filme no sistema")
    @PostMapping
    public ResponseEntity<FilmeResponseDTO> cadastrar(@RequestBody @Valid FilmeRequestDTO dto) {

        FilmeResponseDTO filme = filmeService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(filme);

    }

    @Operation(summary = "Cadastrar filmes em lote", description = "Cadastra múltiplos filmes de uma só vez através de uma lista")
    @PostMapping("/lote")
    public ResponseEntity<List<FilmeResponseDTO>> cadastrarEmLote(@RequestBody List<@Valid FilmeRequestDTO> dtos) {

        List<FilmeResponseDTO> salvos = filmeService.cadastrarEmLote(dtos);

        return ResponseEntity.status(HttpStatus.CREATED).body(salvos);

    }

    @Operation(summary = "Listar filmes", description = "Lista todos os filmes de forma paginada com suporte a filtros de busca")
    @GetMapping
    public ResponseEntity<Page<FilmeResponseDTO>> listar(
            FilmeFiltroDTO filtro,
            @PageableDefault(page = 0, size = 10, sort = "titulo", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<FilmeResponseDTO> lista = filmeService.listarParaClientes(filtro, pageable);

        return ResponseEntity.ok(lista);

    }

    @Operation(summary = "Atualizar filme", description = "Atualiza os dados de um filme existente pelo seu ID")
    @PatchMapping("/{id}")
    public ResponseEntity<FilmeResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody FilmeRequestDTO dto) {

        FilmeResponseDTO filme = filmeService.atualizar(id, dto);

        return ResponseEntity.status(HttpStatus.OK).body(filme);

    }

    @Operation(summary = "Deletar filme", description = "Remove um filme do sistema pelo seu ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        filmeService.deletar(id);

        return ResponseEntity.noContent().build();

    }

    @Operation(summary = "Buscar filme por ID", description = "Busca as informações detalhadas de um filme pelo seu identificador")
    @GetMapping("/{id}")
    public ResponseEntity<FilmeResponseDTO> buscarPorId(@PathVariable Long id) {

        FilmeResponseDTO filme = filmeService.buscarPorId(id);

        return ResponseEntity.status(HttpStatus.OK).body(filme);

    }

}