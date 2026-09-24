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
import poltrona.dto.cliente.AtualizaClienteRequestDTO;
import poltrona.dto.cliente.ClienteRequestDTO;
import poltrona.dto.cliente.ClienteResponseDTO;
import poltrona.dto.usuario.AtualizaSenhaRequestDTO;
import poltrona.service.ClienteService;

@Tag(name = "Clientes", description = "Gerenciamento de clientes")
@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Operation(summary = "Cadastrar cliente", description = "Cadastra um novo cliente no sistema")
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> cadastrar(@Valid @RequestBody ClienteRequestDTO dto) {

        ClienteResponseDTO cliente = clienteService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);

    }

    @Operation(summary = "Listar todos os clientes", description = "Lista todos os clientes cadastrados de forma paginada")
    @GetMapping
    public ResponseEntity<Page<ClienteResponseDTO>> listarTodos(
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<ClienteResponseDTO> clientes = clienteService.listarTodos(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(clientes);

    }

    @Operation(summary = "Meus dados de cliente", description = "Retorna os dados do cliente autenticado")
    @GetMapping("/me")
    public ResponseEntity<ClienteResponseDTO> me() {

        ClienteResponseDTO cliente = clienteService.me();

        return ResponseEntity.status(HttpStatus.OK).body(cliente);

    }

    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados cadastrais do cliente autenticado")
    @PatchMapping
    public ResponseEntity<ClienteResponseDTO> atualizar(@Valid @RequestBody AtualizaClienteRequestDTO dto) {

        ClienteResponseDTO cliente = clienteService.atualizar(dto);

        return ResponseEntity.status(HttpStatus.OK).body(cliente);

    }

    @Operation(summary = "Atualizar senha", description = "Atualiza a senha do cliente autenticado")
    @PatchMapping("/senha")
    public ResponseEntity<Void> atualizarSenha(@Valid @RequestBody AtualizaSenhaRequestDTO dto) {

        clienteService.atualizarSenha(dto);

        return ResponseEntity.noContent().build();

    }

    @Operation(summary = "Encerrar conta", description = "O cliente autenticado encerra sua própria conta no sistema")
    @DeleteMapping
    public ResponseEntity<Void> encerrar() {

        clienteService.encerrar();

        return ResponseEntity.noContent().build();

    }

    @Operation(summary = "Buscar cliente por ID", description = "Busca os dados de um cliente específico pelo identificador")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {

        ClienteResponseDTO cliente = clienteService.buscarPorId(id);

        return ResponseEntity.status(HttpStatus.OK).body(cliente);

    }

}