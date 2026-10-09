package poltrona.controller.doc;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import poltrona.dto.page.RespostaPaginadaDTO;
import poltrona.dto.poltrona.MapaPoltronasResponseDTO;
import poltrona.dto.sessao.AtualizaSessaoRequestDTO;
import poltrona.dto.sessao.GradeSessaoRequestDTO;
import poltrona.dto.sessao.SessaoFiltroDTO;
import poltrona.dto.sessao.SessaoRequestDTO;
import poltrona.dto.sessao.SessaoResponseDTO;

@Tag(name = "Sessões", description = "Gerenciamento e consulta de sessões de exibição nos cinemas")
public interface SessaoControllerDoc {

    @Operation(summary = "Cadastrar sessão", description = "Cadastra uma nova sessão individual para exibição de um filme em uma sala específica")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sessão cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário não possui permissão para cadastrar sessões"),
            @ApiResponse(responseCode = "404", description = "Filme, sala ou cinema não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito de horário ou sessão já cadastrada")
    })
    ResponseEntity<SessaoResponseDTO> cadastrar(
            @Valid SessaoRequestDTO dto);

    @Operation(summary = "Cadastrar grade de sessões", description = "Gera e cadastra múltiplas sessões para um intervalo de dias e horários definido na grade")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Grade de sessões cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da grade inválidos ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário não possui permissão para cadastrar sessões"),
            @ApiResponse(responseCode = "404", description = "Filme, sala ou cinema não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito de horário ou impossibilidade de gerar uma ou mais sessões")
    })
    ResponseEntity<List<SessaoResponseDTO>> cadastrarGrade(
            @Valid GradeSessaoRequestDTO dto);

    @Operation(summary = "Listar sessões", description = "Lista as sessões cadastradas de forma paginada com suporte a filtros dinâmicos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de sessões retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de consulta inválidos")
    })
    ResponseEntity<RespostaPaginadaDTO<SessaoResponseDTO>> listar(
            @ParameterObject SessaoFiltroDTO filtro,
            @ParameterObject Pageable pageable);

    @Operation(summary = "Buscar sessão por ID", description = "Busca as informações detalhadas de uma sessão específica pelo seu identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessão encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada para o ID informado")
    })
    ResponseEntity<SessaoResponseDTO> buscarPorId(
            @Parameter(description = "ID da sessão") Long id);

    @Operation(summary = "Obter mapa de poltronas", description = "Retorna o mapa de ocupação das poltronas da sala vinculada à sessão, indicando as poltronas disponíveis e ocupadas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mapa de poltronas retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada para o ID informado")
    })
    ResponseEntity<MapaPoltronasResponseDTO> obterMapaPoltronas(
            @Parameter(description = "ID da sessão") Long id);

    @Operation(summary = "Atualizar sessão", description = "Atualiza os dados de uma sessão existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessão atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário não possui permissão para atualizar a sessão"),
            @ApiResponse(responseCode = "404", description = "Sessão, filme ou sala não encontrada"),
            @ApiResponse(responseCode = "409", description = "Conflito com outra sessão ou horário existente")
    })
    ResponseEntity<SessaoResponseDTO> atualizar(
            @Parameter(description = "ID da sessão") Long id,

            @Valid AtualizaSessaoRequestDTO dto);

    @Operation(summary = "Deletar sessão", description = "Remove uma sessão do sistema pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sessão removida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Não é possível remover a sessão devido a uma regra de negócio"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário não possui permissão para remover a sessão"),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada para o ID informado")
    })
    ResponseEntity<Void> deletar(
            @Parameter(description = "ID da sessão") Long id);
}