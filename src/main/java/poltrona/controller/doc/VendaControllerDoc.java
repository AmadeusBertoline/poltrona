package poltrona.controller.doc;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import poltrona.dto.venda.VendaRequestDTO;
import poltrona.dto.venda.VendaResponseDTO;

@Tag(name = "Vendas", description = "Gerenciamento e processamento de vendas de ingressos e produtos da bomboniere")
public interface VendaControllerDoc {

    @Operation(summary = "Realizar venda", description = "Registra uma nova compra no sistema contendo ingressos e/ou itens de bomboniere")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venda realizada com sucesso", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VendaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos ou regra de negócio violada", content = @Content),
            @ApiResponse(responseCode = "404", description = "Produto ou recurso não encontrado", content = @Content)
    })
    @PostMapping
    ResponseEntity<VendaResponseDTO> cadastrar(@Valid @RequestBody VendaRequestDTO dto);

    @Operation(summary = "Listar todas as vendas", description = "Lista o histórico de vendas cadastradas de forma paginada com suporte a filtro por cliente (acesso administrativo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de vendas retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @GetMapping
    ResponseEntity<Page<VendaResponseDTO>> listarTodas(
            @Parameter(description = "ID do cliente para filtragem opcional") @RequestParam(required = false) Long clienteId,
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable);

    @Operation(summary = "Minhas compras", description = "Retorna o histórico de compras paginado do cliente atualmente autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico de compras retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado", content = @Content)
    })
    @GetMapping("/me")
    ResponseEntity<Page<VendaResponseDTO>> me(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable);

    @Operation(summary = "Buscar venda por ID", description = "Busca os detalhes completos de uma transação de venda pelo seu identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venda encontrada com sucesso", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VendaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    ResponseEntity<VendaResponseDTO> buscarPorId(@Parameter(description = "ID da venda") @PathVariable Long id);

    @Operation(summary = "Cancelar venda", description = "Cancela uma venda existente, liberando os ingressos/poltronas associados e estornando os itens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venda cancelada com sucesso", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VendaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Venda já cancelada ou impossibilitada de cancelamento", content = @Content),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada", content = @Content)
    })
    @PatchMapping("/{id}/cancelar")
    ResponseEntity<VendaResponseDTO> cancelar(
            @Parameter(description = "ID da venda a ser cancelada") @PathVariable Long id);

    @Operation(summary = "Download do comprovante em PDF", description = "Gera e realiza o download do comprovante de venda no formato PDF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comprovante gerado com sucesso", content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE, schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "400", description = "Tentativa de acesso a comprovante de outro cliente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada para o ID informado", content = @Content)
    })
    @GetMapping(value = "/{id}/download", produces = MediaType.APPLICATION_PDF_VALUE)
    ResponseEntity<byte[]> downloadPdf(@Parameter(description = "ID da venda") @PathVariable Long id);
}