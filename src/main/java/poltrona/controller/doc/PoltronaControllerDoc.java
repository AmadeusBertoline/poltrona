package poltrona.controller.doc;

import java.util.List;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import poltrona.dto.poltrona.PoltronaResponseDTO;
import poltrona.dto.poltrona.TipoPoltronaRequestDTO;

@Tag(name = "Poltronas", description = "Gerenciamento e consulta de poltronas das salas")
public interface PoltronaControllerDoc {

    @Operation(summary = "Buscar poltrona por ID", description = "Busca as informações detalhadas de uma poltrona específica pelo identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Poltrona encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Poltrona não encontrada para o ID informado")
    })
    ResponseEntity<PoltronaResponseDTO> buscarPorId(Long id);

    @Operation(summary = "Listar poltronas por sala", description = "Retorna a lista de todas as poltronas pertencentes a uma determinada sala de cinema")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de poltronas retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Sala não encontrada para o ID informado")
    })
    ResponseEntity<List<PoltronaResponseDTO>> listarPorSala(Long salaId);

    @Operation(summary = "Atualizar tipo da poltrona", description = "Atualiza a categoria ou tipo de uma poltrona (ex: CONVENCIONAL, VIP, RECLINAVEL, ACESSIVEL). Não é permitido se houver ingressos para sessões futuras.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo da poltrona atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "A poltrona já possui este tipo ou possui ingressos vendidos para sessões futuras"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido: usuário sem permissão para alterar poltronas deste cinema"),
            @ApiResponse(responseCode = "404", description = "Poltrona não encontrada para o ID informado")
    })
    ResponseEntity<PoltronaResponseDTO> atualizarTipo(Long id, TipoPoltronaRequestDTO tipo);

    @Operation(summary = "Alterar status da poltrona", description = "Ativa ou desativa a disponibilidade de uma poltrona. Não é permitido desativar se houver ingressos para sessões futuras.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status da poltrona alterado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Tentativa de desativar poltrona com ingressos vendidos para sessões futuras"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido: usuário sem permissão para alterar poltronas deste cinema"),
            @ApiResponse(responseCode = "404", description = "Poltrona não encontrada para o ID informado")
    })
    ResponseEntity<PoltronaResponseDTO> alterarStatus(Long id, Boolean ativa);

    @Operation(summary = "Desativar poltrona", description = "Desativa uma poltrona individualmente. Não é permitido se ela já estiver inativa ou possuir ingressos para sessões futuras.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Poltrona desativada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Poltrona já inativa ou possui ingressos vendidos para sessões futuras"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido: usuário sem permissão para alterar poltronas deste cinema"),
            @ApiResponse(responseCode = "404", description = "Poltrona não encontrada para o ID informado")
    })
    ResponseEntity<Void> desativar(Long id);
}