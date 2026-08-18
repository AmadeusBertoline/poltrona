package poltrona.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poltrona.dto.login.LoginRequestDTO;
import poltrona.dto.login.LoginResponseDTO;
import poltrona.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> logar(@RequestBody LoginRequestDTO dto) {

        LoginResponseDTO login = authService.logar(dto);

        return ResponseEntity.status(HttpStatus.OK).body(login);

    }

}
