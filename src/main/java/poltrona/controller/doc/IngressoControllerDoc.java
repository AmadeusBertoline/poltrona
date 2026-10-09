package poltrona.controller.doc;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import poltrona.dto.ingresso.IngressoResponseDTO;

@Tag(name = "Ingressos", description = "Gerenciamento de ingressos")
public interface IngressoControllerDoc {

        @Operation(summary = "Listar todos os ingressos", description = "Lista todos os ingressos cadastrados no sistema de forma paginada (Acesso administrativo)")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Lista de ingressos retornada com sucesso"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "403", description = "Acesso proibido")
        })
        ResponseEntity<Page<IngressoResponseDTO>> listarTodos(
                        @ParameterObject Pageable pageable);

        @Operation(summary = "Listar meus ingressos", description = "Lista os ingressos do cliente autenticado de forma paginada, ordenados da data de criação mais recente para a mais antiga")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Lista dos ingressos do cliente retornada com sucesso"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado")
        })
        ResponseEntity<Page<IngressoResponseDTO>> meusIngressos(
                        @ParameterObject Pageable pageable);

        @Operation(summary = "Buscar ingresso por ID", description = "Busca as informações detalhadas de um ingresso pelo seu identificador")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Ingresso encontrado com sucesso"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "404", description = "Ingresso não encontrado para o ID informado")
        })
        ResponseEntity<IngressoResponseDTO> buscarPorId(Long id);

        @Operation(summary = "Download do ingresso em PDF", description = "Gera e faz o download do arquivo PDF do ingresso")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "PDF do ingresso gerado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Tentativa de baixar um ingresso que pertence a outro usuário"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "404", description = "Ingresso não encontrado para o ID informado")
        })
        ResponseEntity<byte[]> downloadPdf(Long id);

        @Operation(summary = "Cancelar ingresso", description = "Cancela um ingresso do cliente autenticado respeitando o tempo mínimo de antecedência configurado na política operacional do cinema")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Ingresso cancelado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Ingresso já cancelado ou fora do prazo limite de antecedência para cancelamento"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "403", description = "Acesso proibido: tentativa de cancelar ingresso de outro usuário"),
                        @ApiResponse(responseCode = "404", description = "Ingresso não encontrado para o ID informado")
        })
        ResponseEntity<Void> cancelar(Long id);
}