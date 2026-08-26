package poltrona.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import poltrona.dto.proprietario.AtualizaProprietarioRequestDTO;
import poltrona.dto.proprietario.ProprietarioRequestDTO;
import poltrona.dto.proprietario.ProprietarioResponseDTO;
import poltrona.dto.usuario.AtualizaSenhaRequestDTO;
import poltrona.service.ProprietarioService;

@RestController
@RequestMapping("/proprietarios")
public class ProprietarioController {

    private ProprietarioService proprietarioService;

    public ProprietarioController(ProprietarioService proprietarioService) {
        this.proprietarioService = proprietarioService;
    }

    @PostMapping
    public ResponseEntity<ProprietarioResponseDTO> cadastrar(@RequestBody ProprietarioRequestDTO dto) {

        ProprietarioResponseDTO proprietario = proprietarioService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(proprietario);

    }

    @GetMapping
    public ResponseEntity<Page<ProprietarioResponseDTO>> listarTodos(
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<ProprietarioResponseDTO> proprietarios = proprietarioService.listarTodos(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(proprietarios);

    }

    @GetMapping("/me")
    public ResponseEntity<ProprietarioResponseDTO> me() {

        ProprietarioResponseDTO proprietario = proprietarioService.me();

        return ResponseEntity.status(HttpStatus.OK).body(proprietario);

    }

    @PatchMapping
    public ResponseEntity<ProprietarioResponseDTO> atualizar(@Valid @RequestBody AtualizaProprietarioRequestDTO dto) {
        ProprietarioResponseDTO response = proprietarioService.atualizar(dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> encerrar() {

        proprietarioService.encerrar();
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/senha")
    public ResponseEntity<Void> atualizarSenha(@RequestBody AtualizaSenhaRequestDTO dto) {

        proprietarioService.atualizarSenha(dto);

        return ResponseEntity.noContent().build();

    }

}
