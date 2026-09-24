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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import poltrona.dto.sala.AtualizaSalaRequestDTO;
import poltrona.dto.sala.SalaRequestDTO;
import poltrona.dto.sala.SalaResponseDTO;
import poltrona.service.SalaService;

@Tag(name = "Salas", description = "Gerenciamento e consulta de salas de exibição dos cinemas")
@RestController
@RequestMapping("/salas")
public class SalaController {

    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @Operation(summary = "Cadastrar sala", description = "Cadastra uma nova sala de exibição vinculada a um cinema")
    @PostMapping
    public ResponseEntity<SalaResponseDTO> cadastrar(@Valid @RequestBody SalaRequestDTO dto) {

        SalaResponseDTO sala = salaService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(sala);

    }

    @Operation(summary = "Listar salas", description = "Lista as salas cadastradas com suporte a filtros por cinema e status de atividade de forma paginada")
    @GetMapping
    public ResponseEntity<Page<SalaResponseDTO>> listar(
            @RequestParam(required = false) Long cinemaId,
            @RequestParam(required = false) Boolean ativo,
            @PageableDefault(page = 0, size = 10, sort = "numero", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<SalaResponseDTO> salas = salaService.listar(cinemaId, ativo, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(salas);

    }

    @Operation(summary = "Buscar sala por ID", description = "Busca os detalhes de uma sala específica pelo seu identificador")
    @GetMapping("/{id}")
    public ResponseEntity<SalaResponseDTO> buscarPorId(@PathVariable Long id) {

        SalaResponseDTO sala = salaService.buscarPorId(id);

        return ResponseEntity.status(HttpStatus.OK).body(sala);

    }

    @Operation(summary = "Atualizar sala", description = "Atualiza os dados de uma sala existente (ex: nome, capacidade ou tipo de tela)")
    @PatchMapping("/{id}")
    public ResponseEntity<SalaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizaSalaRequestDTO dto) {

        SalaResponseDTO sala = salaService.atualizar(id, dto);

        return ResponseEntity.status(HttpStatus.OK).body(sala);

    }

    @Operation(summary = "Desativar sala", description = "Desativa uma sala de cinema (Soft Delete), impedindo o agendamento de novas sessões")
    @DeleteMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {

        salaService.desativar(id);

        return ResponseEntity.noContent().build();

    }

    @Operation(summary = "Deletar sala", description = "Remove fisicamente uma sala do sistema pelo seu ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        salaService.deletar(id);

        return ResponseEntity.noContent().build();

    }

}