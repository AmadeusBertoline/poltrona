package poltrona.controller.doc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import poltrona.dto.sala.AtualizaSalaRequestDTO;
import poltrona.dto.sala.SalaRequestDTO;
import poltrona.dto.sala.SalaResponseDTO;

@Tag(name = "Salas", description = "Gerenciamento e consulta de salas de exibição dos cinemas")
public interface SalaControllerDoc {

    @Operation(summary = "Cadastrar sala", description = "Cadastra uma nova sala de exibição vinculada a um cinema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sala cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos, cinema inativo ou usuário sem permissão para operar no cinema informado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Cinema não encontrado ou não pertence ao proprietário logado"),
            @ApiResponse(responseCode = "409", description = "Conflito: o cinema já possui uma sala cadastrada com esse número")
    })
    ResponseEntity<SalaResponseDTO> cadastrar(SalaRequestDTO dto);

    @Operation(summary = "Listar salas", description = "Lista as salas cadastradas com suporte a filtros por cinema e status de atividade de forma paginada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada de salas retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cinema não encontrado para o ID informado no filtro")
    })
    ResponseEntity<Page<SalaResponseDTO>> listar(Long cinemaId, Boolean ativo, Pageable pageable);

    @Operation(summary = "Buscar sala por ID", description = "Busca os detalhes de uma sala específica pelo seu identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sala encontrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Gerente/usuário sem permissão para acessar salas de outro cinema"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Sala não encontrada para o ID informado")
    })
    ResponseEntity<SalaResponseDTO> buscarPorId(Long id);

    @Operation(summary = "Atualizar sala", description = "Atualiza os dados de uma sala existente (ex: número, capacidade de poltronas)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sala atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou usuário sem permissão para alterar salas deste cinema"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Sala não encontrada para o ID informado"),
            @ApiResponse(responseCode = "409", description = "Conflito: o novo número informado já pertence a outra sala deste cinema")
    })
    ResponseEntity<SalaResponseDTO> atualizar(Long id, AtualizaSalaRequestDTO dto);

    @Operation(summary = "Desativar sala", description = "Desativa uma sala de cinema (Soft Delete), impedindo o agendamento de novas sessões")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sala desativada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Regra de negócio violada: não é possível desativar sala com ingressos vendidos para sessões futuras ou usuário sem permissão"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Sala não encontrada para o ID informado")
    })
    ResponseEntity<Void> desativar(Long id);

    @Operation(summary = "Deletar sala", description = "Remove fisicamente uma sala do sistema pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sala removida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Regra de negócio violada: não é possível deletar sala vinculada a uma sessão ou usuário sem permissão"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Sala não encontrada para o ID informado")
    })
    ResponseEntity<Void> deletar(Long id);
}