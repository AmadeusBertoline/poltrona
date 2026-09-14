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
import poltrona.dto.produto.AtualizaProdutoRequestDTO;
import poltrona.dto.produto.CadastroProdutoRequestDTO;
import poltrona.dto.produto.ProdutoResponseDTO;
import poltrona.entity.Produto;
import poltrona.mapper.ProdutoMapper;
import poltrona.service.ProdutoService;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final ProdutoMapper produtoMapper;

    public ProdutoController(ProdutoService produtoService, ProdutoMapper produtoMapper) {
        this.produtoService = produtoService;
        this.produtoMapper = produtoMapper;
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> cadastrar(@RequestBody CadastroProdutoRequestDTO dto) {

        Produto produto = produtoService.cadastrar(dto);

        ProdutoResponseDTO response = produtoMapper.toDTO(produto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> listarTodos(
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<ProdutoResponseDTO> produtos = produtoService.listarTodos(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(produtos);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {

        Produto produto = produtoService.buscarPorId(id);

        ProdutoResponseDTO response = produtoMapper.toDTO(produto);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody AtualizaProdutoRequestDTO dto) {

        Produto produto = produtoService.atualizar(id, dto);

        ProdutoResponseDTO response = produtoMapper.toDTO(produto);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<ProdutoResponseDTO> desativar(@PathVariable Long id) {

        Produto produto = produtoService.desativar(id);

        ProdutoResponseDTO response = produtoMapper.toDTO(produto);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        produtoService.deletar(id);

        return ResponseEntity.noContent().build();

    }

}