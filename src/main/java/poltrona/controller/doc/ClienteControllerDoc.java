package poltrona.controller.doc;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import poltrona.dto.cliente.AtualizaClienteRequestDTO;
import poltrona.dto.cliente.ClienteRequestDTO;
import poltrona.dto.cliente.ClienteResponseDTO;
import poltrona.dto.usuario.AtualizaSenhaRequestDTO;

@Tag(name = "Clientes", description = "Gerenciamento de clientes")
public interface ClienteControllerDoc {

    @Operation(summary = "Cadastrar cliente", description = "Cadastra um novo cliente no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou senha/confirmação incorretas"),
            @ApiResponse(responseCode = "409", description = "Conflito: CPF ou e-mail já cadastrado no sistema")
    })
    ResponseEntity<ClienteResponseDTO> cadastrar(ClienteRequestDTO dto);

    @Operation(summary = "Listar todos os clientes", description = "Lista todos os clientes cadastrados de forma paginada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido")
    })
    ResponseEntity<Page<ClienteResponseDTO>> listarTodos(@ParameterObject Pageable pageable);

    @Operation(summary = "Meus dados de cliente", description = "Retorna os dados do cliente autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados do cliente retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<ClienteResponseDTO> me();

    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados cadastrais do cliente autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados do cliente atualizados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou conta bloqueada/encerrada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "409", description = "Conflito: e-mail informado já está em uso por outra conta")
    })
    ResponseEntity<ClienteResponseDTO> atualizar(AtualizaClienteRequestDTO dto);

    @Operation(summary = "Atualizar senha", description = "Atualiza a senha do cliente autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou nova senha e confirmação não coincidem"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<Void> atualizarSenha(AtualizaSenhaRequestDTO dto);

    @Operation(summary = "Encerrar conta", description = "O cliente autenticado encerra sua própria conta no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Conta encerrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Não é possível inativar a conta pois existem ingressos para sessões futuras"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<Void> encerrar();

    @Operation(summary = "Buscar cliente por ID", description = "Busca os dados de um cliente específico pelo identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado para o ID informado")
    })
    ResponseEntity<ClienteResponseDTO> buscarPorId(Long id);
}