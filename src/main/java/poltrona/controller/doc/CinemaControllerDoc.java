package poltrona.controller.doc;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import poltrona.dto.cinema.AtualizaCinemaRequestDTO;
import poltrona.dto.cinema.CinemaFiltroDTO;
import poltrona.dto.cinema.CinemaRequestDTO;
import poltrona.dto.cinema.CinemaResponseDTO;

@Tag(name = "Cinemas", description = "Gerenciamento de cinemas")
public interface CinemaControllerDoc {

    @Operation(summary = "Cadastrar um cinema", description = "Cadastra um novo cinema no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cinema cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou proprietário bloqueado/encerrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido"),
            @ApiResponse(responseCode = "409", description = "Conflito: CNPJ ou nome de cinema já cadastrado para este proprietário")
    })
    ResponseEntity<CinemaResponseDTO> cadastrar(CinemaRequestDTO dto);

    @Operation(summary = "Listar todos os cinemas", description = "Lista todos os cinemas de forma paginada com suporte a filtros de busca")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de cinemas retornada com sucesso")
    })
    ResponseEntity<Page<CinemaResponseDTO>> listarTodos(
            @ParameterObject CinemaFiltroDTO filtro,
            @ParameterObject Pageable pageable);

    @Operation(summary = "Meus cinemas", description = "Lista os cinemas vinculados ao usuário/admin autenticado de forma paginada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista dos seus cinemas retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<Page<CinemaResponseDTO>> me(@ParameterObject Pageable pageable);

    @Operation(summary = "Buscar cinema por ID", description = "Busca as informações detalhadas de um cinema pelo seu identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cinema encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cinema não encontrado para o ID informado")
    })
    ResponseEntity<CinemaResponseDTO> buscarPorId(Long id);

    @Operation(summary = "Atualizar cinema", description = "Atualiza os dados cadastrais de um cinema existente pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cinema atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados informados são inválidos ou cinema inativo"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Cinema não encontrado para o ID informado"),
            @ApiResponse(responseCode = "409", description = "Conflito: outro cinema seu já utiliza este nome")
    })
    ResponseEntity<CinemaResponseDTO> atualizar(Long id, AtualizaCinemaRequestDTO dto);

    @Operation(summary = "Encerrar cinema", description = "Encerra as atividades de um cinema. Não é permitido se houverem sessões futuras com ingressos vendidos.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cinema encerrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Não é possível encerrar o cinema pois existem sessões futuras com ingressos vendidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: você não tem permissão para encerrar este cinema"),
            @ApiResponse(responseCode = "404", description = "Cinema não encontrado para o ID informado")
    })
    ResponseEntity<Void> encerrar(Long id);

    @Operation(summary = "Deletar cinema", description = "Remove um cinema do sistema pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cinema removido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Não é possível deletar um cinema com sessões que possuem ingressos vendidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado: você não tem permissão para excluir este cinema"),
            @ApiResponse(responseCode = "404", description = "Cinema não encontrado para o ID informado")
    })
    ResponseEntity<Void> deletar(Long id);
}