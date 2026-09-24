package poltrona.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import poltrona.dto.page.RespostaPaginadaDTO;
import poltrona.dto.sessao.GradeSessaoRequestDTO;
import poltrona.dto.sessao.SessaoFiltroDTO;
import poltrona.dto.sessao.SessaoRequestDTO;
import poltrona.dto.sessao.SessaoResponseDTO;
import poltrona.service.SessaoService;

@Tag(name = "Sessões", description = "Gerenciamento e consulta de sessões de exibição nos cinemas")
@RestController
@RequestMapping("/sessoes")
public class SessaoController {

    private final SessaoService sessaoService;

    public SessaoController(SessaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    @Operation(summary = "Cadastrar sessão", description = "Cadastra uma nova sessão individual para exibição de um filme em uma sala específica")
    @PostMapping
    public ResponseEntity<SessaoResponseDTO> cadastrar(@Valid @RequestBody SessaoRequestDTO dto) {

        SessaoResponseDTO sessao = sessaoService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(sessao);

    }

    @Operation(summary = "Cadastrar grade de sessões", description = "Gera e cadastra múltiplas sessões em lote para um intervalo de dias e horários")
    @PostMapping("/grade")
    public ResponseEntity<List<SessaoResponseDTO>> cadastrarGrade(@Valid @RequestBody GradeSessaoRequestDTO dto) {

        List<SessaoResponseDTO> sessoes = sessaoService.cadastrarGrade(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(sessoes);

    }

    @Operation(summary = "Listar sessões", description = "Lista as sessões cadastradas de forma paginada com suporte a filtros dinâmicos (ex: por filme, sala, cinema ou data)")
    @GetMapping
    public ResponseEntity<RespostaPaginadaDTO<SessaoResponseDTO>> listar(
            SessaoFiltroDTO filtro,
            @PageableDefault(page = 0, size = 10, sort = "dataHoraInicio", direction = Sort.Direction.ASC) Pageable pageable) {

        RespostaPaginadaDTO<SessaoResponseDTO> pagina = sessaoService.listar(filtro, pageable);

        return ResponseEntity.ok(pagina);
    }

    @Operation(summary = "Buscar sessão por ID", description = "Busca as informações detalhadas de uma sessão específica pelo seu identificador")
    @GetMapping("/{id}")
    public ResponseEntity<SessaoResponseDTO> buscarPorId(@PathVariable Long id) {

        SessaoResponseDTO sessao = sessaoService.buscarPorId(id);

        return ResponseEntity.ok(sessao);

    }

    @Operation(summary = "Deletar sessão", description = "Remove uma sessão do sistema pelo seu ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        sessaoService.deletar(id);

        return ResponseEntity.noContent().build();

    }

}