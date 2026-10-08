package poltrona.controller.doc;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import poltrona.dto.gerente.AtualizaGerenteRequestDTO;
import poltrona.dto.gerente.GerenteRequestDTO;
import poltrona.dto.gerente.GerenteResponseDTO;
import poltrona.dto.usuario.AtualizaSenhaRequestDTO;

@Tag(name = "Gerentes", description = "Gerenciamento de gerentes")
public interface GerenteControllerDoc {

    @Operation(summary = "Cadastrar gerente", description = "Cadastra um novo gerente associado a um cinema do proprietário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Gerente cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou senha e confirmação de senha não coincidem"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido: o cinema informado não pertence ao proprietário autenticado"),
            @ApiResponse(responseCode = "409", description = "Conflito: já existe uma conta cadastrada com este CPF ou e-mail")
    })
    ResponseEntity<GerenteResponseDTO> cadastrar(GerenteRequestDTO dto);

    @Operation(summary = "Listar todos os gerentes", description = "Lista todos os gerentes cadastrados de forma paginada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de gerentes retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    ResponseEntity<Page<GerenteResponseDTO>> listarTodos(@ParameterObject Pageable pageable);

    @Operation(summary = "Meus dados de gerente", description = "Retorna os dados do gerente autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados do gerente autenticado retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<GerenteResponseDTO> me();

    @Operation(summary = "Atualizar gerente", description = "Atualiza os dados cadastrais (nome, e-mail, data de nascimento) do gerente autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados do gerente atualizados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou conta bloqueada/encerrada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "409", description = "Conflito: já existe outra conta cadastrada com o e-mail informado")
    })
    ResponseEntity<GerenteResponseDTO> atualizar(AtualizaGerenteRequestDTO dto);

    @Operation(summary = "Atualizar senha", description = "Atualiza a senha de acesso do gerente autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou a nova senha não coincide com a confirmação"),
            @ApiResponse(responseCode = "401", description = "Senha atual incorreta ou usuário não autenticado")
    })
    ResponseEntity<Void> atualizarSenha(AtualizaSenhaRequestDTO dto);

    @Operation(summary = "Encerrar conta", description = "O gerente autenticado encerra sua própria conta no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Conta encerrada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<Void> encerrar();

    @Operation(summary = "Buscar gerente por ID", description = "Busca os dados de um gerente específico pelo identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Gerente encontrado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido"),
            @ApiResponse(responseCode = "404", description = "Gerente não encontrado para o ID informado")
    })
    ResponseEntity<GerenteResponseDTO> buscarPorId(Long id);
}