package poltrona.controller.doc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import poltrona.dto.proprietario.AtualizaProprietarioRequestDTO;
import poltrona.dto.proprietario.ProprietarioRequestDTO;
import poltrona.dto.proprietario.ProprietarioResponseDTO;
import poltrona.dto.usuario.AtualizaSenhaRequestDTO;

@Tag(name = "Proprietários", description = "Gerenciamento e consulta de perfis de proprietários de cinemas")
public interface ProprietarioControllerDoc {

    @Operation(summary = "Cadastrar proprietário", description = "Cadastra um novo proprietário de cinema no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Proprietário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflito: e-mail ou CPF informado já está em uso por outro usuário")
    })
    ResponseEntity<ProprietarioResponseDTO> cadastrar(ProprietarioRequestDTO dto);

    @Operation(summary = "Listar todos os proprietários", description = "Lista todos os proprietários cadastrados de forma paginada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada de proprietários retornada com sucesso")
    })
    ResponseEntity<Page<ProprietarioResponseDTO>> listarTodos(Pageable pageable);

    @Operation(summary = "Meus dados de proprietário", description = "Retorna os dados do proprietário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados do proprietário retornados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuário autenticado não possui o perfil de proprietário"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<ProprietarioResponseDTO> me();

    @Operation(summary = "Buscar proprietário por ID", description = "Busca os detalhes de um proprietário específico pelo seu identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Proprietário encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Proprietário não encontrado para o ID informado")
    })
    ResponseEntity<ProprietarioResponseDTO> buscarPorId(Long id);

    @Operation(summary = "Atualizar proprietário", description = "Atualiza os dados cadastrais do proprietário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Proprietário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "409", description = "Conflito: o e-mail informado já está em uso por outro usuário")
    })
    ResponseEntity<ProprietarioResponseDTO> atualizar(AtualizaProprietarioRequestDTO dto);

    @Operation(summary = "Atualizar senha", description = "Atualiza a senha do proprietário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta, confirmação de senha divergente ou dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<Void> atualizarSenha(AtualizaSenhaRequestDTO dto);

    @Operation(summary = "Encerrar conta", description = "O proprietário autenticado encerra sua própria conta no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Conta encerrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Regra de negócio violada: não é possível encerrar a conta com sessões futuras que possuem ingressos vendidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<Void> encerrar();
}