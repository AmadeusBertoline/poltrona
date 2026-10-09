package poltrona.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import poltrona.controller.doc.SessaoControllerDoc;
import poltrona.dto.page.RespostaPaginadaDTO;
import poltrona.dto.poltrona.MapaPoltronasResponseDTO;
import poltrona.dto.sessao.AtualizaSessaoRequestDTO;
import poltrona.dto.sessao.GradeSessaoRequestDTO;
import poltrona.dto.sessao.SessaoFiltroDTO;
import poltrona.dto.sessao.SessaoRequestDTO;
import poltrona.dto.sessao.SessaoResponseDTO;
import poltrona.service.SessaoService;

@RestController
@RequestMapping("/sessoes")
public class SessaoController implements SessaoControllerDoc {

    private final SessaoService sessaoService;

    public SessaoController(SessaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    @Override
    @PostMapping
    public ResponseEntity<SessaoResponseDTO> cadastrar(
            @Valid @RequestBody SessaoRequestDTO dto) {

        SessaoResponseDTO sessao = sessaoService.cadastrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(sessao);
    }

    @Override
    @PostMapping("/grade")
    public ResponseEntity<List<SessaoResponseDTO>> cadastrarGrade(
            @Valid @RequestBody GradeSessaoRequestDTO dto) {

        List<SessaoResponseDTO> sessoes = sessaoService.cadastrarGrade(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(sessoes);
    }

    @Override
    @GetMapping
    public ResponseEntity<RespostaPaginadaDTO<SessaoResponseDTO>> listar(
            @ParameterObject SessaoFiltroDTO filtro,
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "dataHoraInicio", direction = Sort.Direction.ASC) Pageable pageable) {

        RespostaPaginadaDTO<SessaoResponseDTO> pagina = sessaoService.listar(filtro, pageable);

        return ResponseEntity.ok(pagina);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<SessaoResponseDTO> buscarPorId(
            @PathVariable Long id) {

        SessaoResponseDTO sessao = sessaoService.buscarPorId(id);

        return ResponseEntity.ok(sessao);
    }

    @Override
    @GetMapping("/{id}/mapa-poltronas")
    public ResponseEntity<MapaPoltronasResponseDTO> obterMapaPoltronas(
            @PathVariable Long id) {

        MapaPoltronasResponseDTO mapa = sessaoService.obterMapaPoltronas(id);

        return ResponseEntity.ok(mapa);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<SessaoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizaSessaoRequestDTO dto) {

        SessaoResponseDTO sessao = sessaoService.atualizar(id, dto);

        return ResponseEntity.ok(sessao);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id) {

        sessaoService.deletar(id);

        return ResponseEntity.noContent().build();
    }
}