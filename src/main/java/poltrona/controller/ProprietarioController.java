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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import poltrona.dto.proprietario.AtualizaProprietarioRequestDTO;
import poltrona.dto.proprietario.ProprietarioRequestDTO;
import poltrona.dto.proprietario.ProprietarioResponseDTO;
import poltrona.dto.usuario.AtualizaSenhaRequestDTO;
import poltrona.service.ProprietarioService;

@Tag(name = "Proprietários", description = "Gerenciamento e consulta de perfis de proprietários de cinemas")
@RestController
@RequestMapping("/proprietarios")
public class ProprietarioController {

    private final ProprietarioService proprietarioService;

    public ProprietarioController(ProprietarioService proprietarioService) {
        this.proprietarioService = proprietarioService;
    }

    @Operation(summary = "Cadastrar proprietário", description = "Cadastra um novo proprietário de cinema no sistema")
    @PostMapping
    public ResponseEntity<ProprietarioResponseDTO> cadastrar(@Valid @RequestBody ProprietarioRequestDTO dto) {

        ProprietarioResponseDTO proprietario = proprietarioService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(proprietario);

    }

    @Operation(summary = "Listar todos os proprietários", description = "Lista todos os proprietários cadastrados de forma paginada")
    @GetMapping
    public ResponseEntity<Page<ProprietarioResponseDTO>> listarTodos(
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<ProprietarioResponseDTO> proprietarios = proprietarioService.listarTodos(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(proprietarios);

    }

    @Operation(summary = "Meus dados de proprietário", description = "Retorna os dados do proprietário autenticado")
    @GetMapping("/me")
    public ResponseEntity<ProprietarioResponseDTO> me() {

        ProprietarioResponseDTO proprietario = proprietarioService.me();

        return ResponseEntity.status(HttpStatus.OK).body(proprietario);

    }

    @Operation(summary = "Buscar proprietário por ID", description = "Busca os detalhes de um proprietário específico pelo seu identificador")
    @GetMapping("/{id}")
    public ResponseEntity<ProprietarioResponseDTO> buscarPorId(@PathVariable Long id) {

        ProprietarioResponseDTO proprietario = proprietarioService.buscarPorId(id);

        return ResponseEntity.status(HttpStatus.OK).body(proprietario);

    }

    @Operation(summary = "Atualizar proprietário", description = "Atualiza os dados cadastrais do proprietário autenticado")
    @PatchMapping
    public ResponseEntity<ProprietarioResponseDTO> atualizar(@Valid @RequestBody AtualizaProprietarioRequestDTO dto) {

        ProprietarioResponseDTO response = proprietarioService.atualizar(dto);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @Operation(summary = "Atualizar senha", description = "Atualiza a senha do proprietário autenticado")
    @PatchMapping("/senha")
    public ResponseEntity<Void> atualizarSenha(@Valid @RequestBody AtualizaSenhaRequestDTO dto) {

        proprietarioService.atualizarSenha(dto);

        return ResponseEntity.noContent().build();

    }

    @Operation(summary = "Encerrar conta", description = "O proprietário autenticado encerra sua própria conta no sistema")
    @DeleteMapping
    public ResponseEntity<Void> encerrar() {

        proprietarioService.encerrar();

        return ResponseEntity.noContent().build();

    }

}