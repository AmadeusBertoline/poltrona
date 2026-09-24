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
import poltrona.dto.gerente.AtualizaGerenteRequestDTO;
import poltrona.dto.gerente.GerenteRequestDTO;
import poltrona.dto.gerente.GerenteResponseDTO;
import poltrona.dto.usuario.AtualizaSenhaRequestDTO;
import poltrona.service.GerenteService;

@Tag(name = "Gerentes", description = "Gerenciamento de gerentes")
@RestController
@RequestMapping("/gerentes")
public class GerenteController {

    private final GerenteService gerenteService;

    public GerenteController(GerenteService gerenteService) {
        this.gerenteService = gerenteService;
    }

    @Operation(summary = "Cadastrar gerente", description = "Cadastra um novo gerente no sistema")
    @PostMapping
    public ResponseEntity<GerenteResponseDTO> cadastrar(@Valid @RequestBody GerenteRequestDTO dto) {

        GerenteResponseDTO gerente = gerenteService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(gerente);

    }

    @Operation(summary = "Listar todos os gerentes", description = "Lista todos os gerentes cadastrados de forma paginada")
    @GetMapping
    public ResponseEntity<Page<GerenteResponseDTO>> listarTodos(
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<GerenteResponseDTO> gerentes = gerenteService.listarTodos(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(gerentes);

    }

    @Operation(summary = "Meus dados de gerente", description = "Retorna os dados do gerente autenticado")
    @GetMapping("/me")
    public ResponseEntity<GerenteResponseDTO> me() {

        GerenteResponseDTO gerente = gerenteService.me();

        return ResponseEntity.status(HttpStatus.OK).body(gerente);

    }

    @Operation(summary = "Atualizar gerente", description = "Atualiza os dados cadastrais do gerente autenticado")
    @PatchMapping
    public ResponseEntity<GerenteResponseDTO> atualizar(@Valid @RequestBody AtualizaGerenteRequestDTO dto) {

        GerenteResponseDTO gerente = gerenteService.atualizar(dto);

        return ResponseEntity.status(HttpStatus.OK).body(gerente);

    }

    @Operation(summary = "Atualizar senha", description = "Atualiza a senha do gerente autenticado")
    @PatchMapping("/senha")
    public ResponseEntity<Void> atualizarSenha(@Valid @RequestBody AtualizaSenhaRequestDTO dto) {

        gerenteService.atualizarSenha(dto);

        return ResponseEntity.noContent().build();

    }

    @Operation(summary = "Encerrar conta", description = "O gerente autenticado encerra sua própria conta no sistema")
    @DeleteMapping
    public ResponseEntity<Void> encerrar() {

        gerenteService.encerrar();

        return ResponseEntity.noContent().build();

    }

    @Operation(summary = "Buscar gerente por ID", description = "Busca os dados de um gerente específico pelo identificador")
    @GetMapping("/{id}")
    public ResponseEntity<GerenteResponseDTO> buscarPorId(@PathVariable Long id) {

        GerenteResponseDTO gerente = gerenteService.buscarPorId(id);

        return ResponseEntity.status(HttpStatus.OK).body(gerente);

    }

}