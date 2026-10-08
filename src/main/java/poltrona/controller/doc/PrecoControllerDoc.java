package poltrona.controller.doc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import poltrona.dto.preco.AtualizaPrecoRequestDTO;
import poltrona.dto.preco.PrecoRequestDTO;
import poltrona.dto.preco.PrecoResponseDTO;

@Tag(name = "Preços", description = "Gerenciamento e consulta da tabela de preços de ingressos")
public interface PrecoControllerDoc {

    @Operation(summary = "Cadastrar preço", description = "Cadastra uma nova configuração de preço para um determinado cinema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Preço cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou tentativa de cadastrar preço para cinema inativo"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido: o cinema não pertence ao proprietário logado"),
            @ApiResponse(responseCode = "409", description = "Conflito: já existe um preço cadastrado com este formato para este cinema")
    })
    ResponseEntity<PrecoResponseDTO> cadastrar(PrecoRequestDTO dto);

    @Operation(summary = "Listar todos os preços", description = "Lista todas as configurações de preços cadastradas de forma paginada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada de preços retornada com sucesso")
    })
    ResponseEntity<Page<PrecoResponseDTO>> listarTodos(Pageable pageable);

    @Operation(summary = "Atualizar preço", description = "Atualiza o valor base de uma configuração de preço existente pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preço atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido: o preço não pertence a nenhum cinema do proprietário logado")
    })
    ResponseEntity<PrecoResponseDTO> atualizar(Long id, AtualizaPrecoRequestDTO dto);

    @Operation(summary = "Desativar preço", description = "Desativa uma configuração de preço pelo ID (Soft Delete), preservando o histórico de ingressos e sessões")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Preço desativado com sucesso"),
            @ApiResponse(responseCode = "400", description = "O preço já se encontra inativo"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido: o preço não pertence a nenhum cinema do proprietário logado")
    })
    ResponseEntity<Void> desativar(Long id);

    @Operation(summary = "Buscar preço por ID", description = "Busca os detalhes de uma configuração de preço específica pelo seu identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preço encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Preço não encontrado para o ID informado")
    })
    ResponseEntity<PrecoResponseDTO> buscarPorId(Long id);

    @Operation(summary = "Buscar preços por cinema", description = "Lista as configurações de preços vinculadas a um cinema específico de forma paginada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada de preços do cinema retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cinema não encontrado para o ID informado")
    })
    ResponseEntity<Page<PrecoResponseDTO>> buscarPorCinema(Long cinemaId, Pageable pageable);
}