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
import poltrona.dto.produto.AtualizaProdutoRequestDTO;
import poltrona.dto.produto.CadastroProdutoRequestDTO;
import poltrona.dto.produto.ProdutoResponseDTO;
import poltrona.enums.produto.TipoProduto;
import poltrona.service.ProdutoService;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> cadastrar(@Valid @RequestBody CadastroProdutoRequestDTO dto) {

        ProdutoResponseDTO response = produtoService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> listarTodos(
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) TipoProduto tipoProduto,
            @PageableDefault(page = 0, size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<ProdutoResponseDTO> produtos = produtoService.listarTodos(ativo, nome, tipoProduto, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(produtos);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {

        ProdutoResponseDTO response = produtoService.buscarPorId(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizaProdutoRequestDTO dto) {

        ProdutoResponseDTO response = produtoService.atualizar(id, dto);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProdutoResponseDTO> alterarStatus(
            @PathVariable Long id,
            @RequestParam Boolean ativo) {

        ProdutoResponseDTO response = produtoService.alterarStatus(id, ativo);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        produtoService.deletar(id);

        return ResponseEntity.noContent().build();

    }

}