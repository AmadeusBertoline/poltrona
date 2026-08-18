package poltrona.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poltrona.dto.admin.AdminRequestDTO;
import poltrona.dto.admin.AdminResponseDTO;
import poltrona.service.AdminService;

@RestController
@RequestMapping("/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<AdminResponseDTO> cadastrar(@RequestBody AdminRequestDTO dto) {

        AdminResponseDTO admin = adminService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(admin);

    }

    @GetMapping("/listar-todos")
    public ResponseEntity<List<AdminResponseDTO>> listarTodos(){

        List<AdminResponseDTO> admins = adminService.listarTodos();

        return ResponseEntity.status(HttpStatus.OK).body(admins);

    }

}
