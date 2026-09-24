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
import poltrona.dto.preco.AtualizaPrecoRequestDTO;
import poltrona.dto.preco.PrecoRequestDTO;
import poltrona.dto.preco.PrecoResponseDTO;
import poltrona.service.PrecoService;

@Tag(name = "Preços", description = "Gerenciamento e consulta da tabela de preços de ingressos")
@RestController
@RequestMapping("/precos")
public class PrecoController {

    private final PrecoService precoService;

    public PrecoController(PrecoService precoService) {
        this.precoService = precoService;
    }

    @Operation(summary = "Cadastrar preço", description = "Cadastra uma nova configuração de preço no sistema")
    @PostMapping
    public ResponseEntity<PrecoResponseDTO> cadastrar(@Valid @RequestBody PrecoRequestDTO dto) {

        PrecoResponseDTO preco = precoService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(preco);

    }

    @Operation(summary = "Listar todos os preços", description = "Lista todas as configurações de preços cadastradas de forma paginada")
    @GetMapping
    public ResponseEntity<Page<PrecoResponseDTO>> listarTodos(
            @PageableDefault(page = 0, size = 10, sort = "valor", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<PrecoResponseDTO> precos = precoService.listarTodos(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(precos);

    }

    @Operation(summary = "Atualizar preço", description = "Atualiza os valores ou regras de um preço existente pelo ID")
    @PatchMapping("/{id}")
    public ResponseEntity<PrecoResponseDTO> atualizar(@PathVariable Long id,
            @Valid @RequestBody AtualizaPrecoRequestDTO dto) {

        PrecoResponseDTO preco = precoService.atualizar(id, dto);

        return ResponseEntity.status(HttpStatus.OK).body(preco);

    }

    @Operation(summary = "Desativar preço", description = "Desativa uma configuração de preço pelo seu ID (Soft Delete), preservando o histórico de ingressos e sessões antigas")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {

        precoService.desativar(id);

        return ResponseEntity.noContent().build();

    }

    @Operation(summary = "Buscar preço por ID", description = "Busca os detalhes de uma configuração de preço específica pelo seu identificador")
    @GetMapping("/{id}")
    public ResponseEntity<PrecoResponseDTO> buscarPorId(@PathVariable Long id) {

        PrecoResponseDTO preco = precoService.buscarPorId(id);

        return ResponseEntity.status(HttpStatus.OK).body(preco);

    }

    @Operation(summary = "Buscar preços por cinema", description = "Lista as configurações de preços vinculadas a um cinema específico de forma paginada")
    @GetMapping("/cinema/{cinemaId}")
    public ResponseEntity<Page<PrecoResponseDTO>> buscarPorCinema(@PathVariable Long cinemaId,
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<PrecoResponseDTO> precos = precoService.buscarPorCinema(cinemaId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(precos);

    }

}