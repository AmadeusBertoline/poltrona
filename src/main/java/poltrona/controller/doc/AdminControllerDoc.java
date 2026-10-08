package poltrona.controller.doc;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import poltrona.dto.admin.AdminRequestDTO;
import poltrona.dto.admin.AdminResponseDTO;
import poltrona.dto.admin.AtualizaAdminRequestDTO;
import poltrona.dto.usuario.AtualizaSenhaRequestDTO;

@Tag(name = "Admins", description = "Gestão de administradores")
public interface AdminControllerDoc {

    @Operation(summary = "Cadastrar um admin", description = "Cadastra um novo administrador no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Administrador cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflito: e-mail ou dados já cadastrados")
    })
    ResponseEntity<AdminResponseDTO> cadastrar(AdminRequestDTO dto);

    @Operation(summary = "Listar todos os admins", description = "Retorna uma lista paginada de administradores")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    ResponseEntity<Page<AdminResponseDTO>> listarTodos(@ParameterObject Pageable pageable);

    @Operation(summary = "Meus dados de admin", description = "Retorna informações do administrador atualmente autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados do administrador retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado / Token inválido"),
            @ApiResponse(responseCode = "404", description = "Administrador não encontrado")
    })
    ResponseEntity<AdminResponseDTO> me();

    @Operation(summary = "Atualizar admin", description = "Atualiza os dados cadastrais do admin autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados atualizados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados informados são inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<AdminResponseDTO> atualizar(AtualizaAdminRequestDTO dto);

    @Operation(summary = "Atualizar senha", description = "Atualiza a senha de acesso do admin autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou nova senha inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<Void> atualizarSenha(AtualizaSenhaRequestDTO dto);

    @Operation(summary = "Encerrar conta", description = "Encerra e desativa a conta do admin autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Conta encerrada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<Void> encerrar();

    @Operation(summary = "Buscar admin por id", description = "Busca os dados detalhados de um administrador pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Administrador encontrado"),
            @ApiResponse(responseCode = "404", description = "Administrador não encontrado para o ID informado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<AdminResponseDTO> buscarPorId(Long id);
}