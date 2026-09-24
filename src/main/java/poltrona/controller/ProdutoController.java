package poltrona.controller;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import poltrona.dto.page.RespostaPaginadaDTO;
import poltrona.dto.produto.AtualizaProdutoRequestDTO;
import poltrona.dto.produto.CadastroProdutoRequestDTO;
import poltrona.dto.produto.ProdutoResponseDTO;
import poltrona.enums.produto.TipoProduto;
import poltrona.service.ProdutoService;

@Tag(name = "Produtos", description = "Gerenciamento e consulta do catálogo de produtos (Bomboniere/Snack Bar)")
@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @Operation(summary = "Cadastrar produto", description = "Cadastra um novo produto no catálogo (ex: pipoca, refrigerante, combo)")
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> cadastrar(@Valid @RequestBody CadastroProdutoRequestDTO dto) {

        ProdutoResponseDTO response = produtoService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @Operation(summary = "Listar produtos", description = "Lista os produtos cadastrados com suporte a filtros por status, nome e tipo, de forma paginada")
    @GetMapping
    public ResponseEntity<RespostaPaginadaDTO<ProdutoResponseDTO>> listarTodos(
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) TipoProduto tipoProduto,
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(produtoService.listarTodos(ativo, nome, tipoProduto, pageable));
    }

    @Operation(summary = "Buscar produto por ID", description = "Busca os detalhes de um produto específico pelo seu identificador")
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {

        ProdutoResponseDTO response = produtoService.buscarPorId(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @Operation(summary = "Atualizar produto", description = "Atualiza parcialmente os dados de um produto existente pelo seu ID")
    @PatchMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizaProdutoRequestDTO dto) {

        ProdutoResponseDTO response = produtoService.atualizar(id, dto);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @Operation(summary = "Alterar status do produto", description = "Ativa ou desativa a disponibilidade de um produto para vendas")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProdutoResponseDTO> alterarStatus(
            @PathVariable Long id,
            @RequestParam Boolean ativo) {

        ProdutoResponseDTO response = produtoService.alterarStatus(id, ativo);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @Operation(summary = "Deletar produto", description = "Remove um produto do sistema pelo seu ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        produtoService.deletar(id);

        return ResponseEntity.noContent().build();

    }

}