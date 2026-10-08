package poltrona.controller.doc;

import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import poltrona.dto.login.LoginRequestDTO;
import poltrona.dto.login.LoginResponseDTO;

@Tag(name = "Autenticação", description = "Endpoints de login e autenticação")
public interface AuthControllerDoc {

    @Operation(summary = "Realizar Login", description = "Autentica um usuário ativo via e-mail/CPF e senha, retornando um token Bearer JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campos da requisição inválidos (ex: e-mail em formato incorreto ou senha em branco)"),
            @ApiResponse(responseCode = "401", description = "Usuário ou senha inválidos")
    })
    ResponseEntity<LoginResponseDTO> logar(@Valid LoginRequestDTO dto);
}