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
import poltrona.dto.admin.AdminRequestDTO;
import poltrona.dto.admin.AdminResponseDTO;
import poltrona.dto.admin.AtualizaAdminRequestDTO;
import poltrona.dto.usuario.AtualizaSenhaRequestDTO;
import poltrona.service.AdminService;

@Tag(name = "Admins", description = "Gestão de admins")
@RestController
@RequestMapping("/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "Cadastrar um admin", description = "Cadastra um Admin")
    @PostMapping
    public ResponseEntity<AdminResponseDTO> cadastrar(@Valid @RequestBody AdminRequestDTO dto) {

        AdminResponseDTO admin = adminService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(admin);

    }

    @Operation(summary = "Listar todos os admins", description = "Lista todos os admins")
    @GetMapping
    public ResponseEntity<Page<AdminResponseDTO>> listarTodos(
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<AdminResponseDTO> admins = adminService.listarTodos(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(admins);

    }

    @Operation(summary = "Meus dados de admins", description = "Lista informações do admin logado")
    @GetMapping("/me")
    public ResponseEntity<AdminResponseDTO> me() {

        AdminResponseDTO admin = adminService.me();

        return ResponseEntity.status(HttpStatus.OK).body(admin);

    }

    @Operation(summary = "Atualizar admin", description = "Atualiza dados do admin logado")
    @PatchMapping
    public ResponseEntity<AdminResponseDTO> atualizar(@Valid @RequestBody AtualizaAdminRequestDTO dto) {

        AdminResponseDTO admin = adminService.atualizar(dto);

        return ResponseEntity.status(HttpStatus.OK).body(admin);

    }

    @Operation(summary = "Atualizar senha", description = "Atualiza senha do admin logado")
    @PatchMapping("/senha")
    public ResponseEntity<Void> atualizarSenha(@Valid @RequestBody AtualizaSenhaRequestDTO dto) {

        adminService.atualizarSenha(dto);

        return ResponseEntity.noContent().build();

    }

    @Operation(summary = "Encerrar conta", description = "O admin logado encerra sua conta")
    @DeleteMapping
    public ResponseEntity<Void> encerrar() {

        adminService.encerrar();

        return ResponseEntity.noContent().build();

    }

    @Operation(summary = "Buscar admin por id", description = "Busca dados do admin pelo")
    @GetMapping("{id}")
    public ResponseEntity<AdminResponseDTO> buscarPorId(@PathVariable Long id){

        AdminResponseDTO admin = adminService.buscarPorId(id);

        return ResponseEntity.status(HttpStatus.OK).body(admin);

    }

}