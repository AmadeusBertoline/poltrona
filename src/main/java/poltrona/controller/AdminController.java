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
import poltrona.dto.admin.AdminRequestDTO;
import poltrona.dto.admin.AdminResponseDTO;
import poltrona.dto.admin.AtualizaAdminRequestDTO;
import poltrona.service.AdminService;

@RestController
@RequestMapping("/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping
    public ResponseEntity<AdminResponseDTO> cadastrar(@RequestBody AdminRequestDTO dto) {

        AdminResponseDTO admin = adminService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(admin);

    }

    @GetMapping
    public ResponseEntity<Page<AdminResponseDTO>> listarTodos(
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<AdminResponseDTO> admins = adminService.listarTodos(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(admins);

    }

    @GetMapping("/me")
    public ResponseEntity<AdminResponseDTO> me() {

        AdminResponseDTO admin = adminService.me();

        return ResponseEntity.status(HttpStatus.OK).body(admin);

    }

    @PatchMapping("/me")
    public ResponseEntity<AdminResponseDTO> atualizar(@RequestBody AtualizaAdminRequestDTO dto) {

        AdminResponseDTO admin = adminService.atualizar(dto);

        return ResponseEntity.status(HttpStatus.OK).body(admin);

    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> encerrarConta() {

        adminService.encerrarConta();

        return ResponseEntity.noContent().build();

    }

}
