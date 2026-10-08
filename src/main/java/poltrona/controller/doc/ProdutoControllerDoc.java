package poltrona.controller.doc;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import poltrona.dto.page.RespostaPaginadaDTO;
import poltrona.dto.produto.AtualizaProdutoRequestDTO;
import poltrona.dto.produto.CadastroProdutoRequestDTO;
import poltrona.dto.produto.ProdutoResponseDTO;
import poltrona.enums.produto.TipoProduto;

@Tag(name = "Produtos", description = "Gerenciamento e consulta do catálogo de produtos (Bomboniere/Snack Bar)")
public interface ProdutoControllerDoc {

    @Operation(summary = "Cadastrar produto", description = "Cadastra um novo produto no catálogo (ex: pipoca, refrigerante, combo) para um cinema específico")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou tentativa de cadastrar produto em cinema que não gerencia"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Cinema não encontrado ou não pertence ao proprietário logado"),
            @ApiResponse(responseCode = "409", description = "Conflito: já existe um produto com este nome cadastrado para o cinema")
    })
    ResponseEntity<ProdutoResponseDTO> cadastrar(CadastroProdutoRequestDTO dto);

    @Operation(summary = "Listar produtos", description = "Lista os produtos cadastrados com suporte a filtros por status, nome e tipo, de forma paginada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada de produtos retornada com sucesso")
    })
    ResponseEntity<RespostaPaginadaDTO<ProdutoResponseDTO>> listarTodos(
            Boolean ativo,
            String nome,
            TipoProduto tipoProduto,
            Pageable pageable);

    @Operation(summary = "Buscar produto por ID", description = "Busca os detalhes de um produto específico pelo seu identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Gerente/usuário sem permissão para acessar produtos de outro cinema"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado para o ID informado")
    })
    ResponseEntity<ProdutoResponseDTO> buscarPorId(Long id);

    @Operation(summary = "Atualizar produto", description = "Atualiza parcialmente os dados de um produto existente (nome, descrição, preço ou quantidade em estoque)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou usuário sem permissão para alterar produtos deste cinema"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado para o ID informado"),
            @ApiResponse(responseCode = "409", description = "Conflito: o novo nome informado já pertence a outro produto deste cinema")
    })
    ResponseEntity<ProdutoResponseDTO> atualizar(Long id, AtualizaProdutoRequestDTO dto);

    @Operation(summary = "Alterar status do produto", description = "Ativa ou desativa a disponibilidade de um produto para vendas na bomboniere")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status do produto alterado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário sem permissão para alterar produtos deste cinema"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado para o ID informado")
    })
    ResponseEntity<ProdutoResponseDTO> alterarStatus(Long id, Boolean ativo);

    @Operation(summary = "Deletar produto", description = "Remove um produto do sistema permanentemente pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário sem permissão para remover produtos deste cinema"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado para o ID informado")
    })
    ResponseEntity<Void> deletar(Long id);
}