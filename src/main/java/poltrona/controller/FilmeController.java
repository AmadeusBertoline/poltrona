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

import jakarta.validation.Valid;
import poltrona.controller.doc.FilmeControllerDoc;
import poltrona.dto.filme.FilmeFiltroDTO;
import poltrona.dto.filme.FilmeRequestDTO;
import poltrona.dto.filme.FilmeResponseDTO;
import poltrona.service.FilmeService;

@RestController
@RequestMapping("/filmes")
public class FilmeController implements FilmeControllerDoc {

    private final FilmeService filmeService;

    public FilmeController(FilmeService filmeService) {
        this.filmeService = filmeService;
    }

    @Override
    @PostMapping
    public ResponseEntity<FilmeResponseDTO> cadastrar(@RequestBody @Valid FilmeRequestDTO dto) {
        FilmeResponseDTO filme = filmeService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(filme);
    }

    @Override
    @PostMapping("/lote")
    public ResponseEntity<List<FilmeResponseDTO>> cadastrarEmLote(@RequestBody List<@Valid FilmeRequestDTO> dtos) {
        List<FilmeResponseDTO> salvos = filmeService.cadastrarEmLote(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvos);
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<FilmeResponseDTO>> listar(
            FilmeFiltroDTO filtro,
            @PageableDefault(page = 0, size = 10, sort = "titulo", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<FilmeResponseDTO> lista = filmeService.listarParaClientes(filtro, pageable);
        return ResponseEntity.ok(lista);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<FilmeResponseDTO> buscarPorId(@PathVariable Long id) {
        FilmeResponseDTO filme = filmeService.buscarPorId(id);
        return ResponseEntity.status(HttpStatus.OK).body(filme);
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<FilmeResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody FilmeRequestDTO dto) {
        FilmeResponseDTO filme = filmeService.atualizar(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(filme);
    }

    @Override
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        filmeService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        filmeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}