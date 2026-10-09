package poltrona.controller.doc;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Venda realizada com sucesso", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VendaResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos ou regra de negócio violada"),
                        @ApiResponse(responseCode = "404", description = "Produto ou recurso não encontrado")
        })
        ResponseEntity<VendaResponseDTO> cadastrar(
                        @Valid VendaRequestDTO dto);

        @Operation(summary = "Listar todas as vendas", description = "Lista o histórico de vendas cadastradas de forma paginada com suporte a filtro por cliente")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Lista de vendas retornada com sucesso"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado")
        })
        ResponseEntity<Page<VendaResponseDTO>> listarTodas(
                        @Parameter(description = "ID do cliente para filtragem opcional") Long clienteId,

                        @ParameterObject Pageable pageable);

        @Operation(summary = "Minhas compras", description = "Retorna o histórico de compras paginado do cliente atualmente autenticado")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Histórico de compras retornado com sucesso"),
                        @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
        })
        ResponseEntity<Page<VendaResponseDTO>> me(
                        @ParameterObject Pageable pageable);

        @Operation(summary = "Buscar venda por ID", description = "Busca os detalhes completos de uma transação de venda pelo seu identificador")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Venda encontrada com sucesso", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VendaResponseDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Venda não encontrada")
        })
        ResponseEntity<VendaResponseDTO> buscarPorId(
                        @Parameter(description = "ID da venda") Long id);

        @Operation(summary = "Cancelar venda", description = "Cancela uma venda existente, liberando os ingressos/poltronas associados")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Venda cancelada com sucesso", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = VendaResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Venda já cancelada ou impossibilitada de cancelamento"),
                        @ApiResponse(responseCode = "404", description = "Venda não encontrada")
        })
        ResponseEntity<VendaResponseDTO> cancelar(
                        @Parameter(description = "ID da venda a ser cancelada") Long id);

        @Operation(summary = "Download do comprovante da venda", description = "Gera o comprovante de uma venda em formato PDF")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Comprovante gerado com sucesso"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "404", description = "Venda não encontrada para o ID informado")
        })
        ResponseEntity<byte[]> downloadPdf(Long id);
}