package poltrona.controller;

import org.springframework.data.domain.Page;
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
import jakarta.validation.Valid;
import poltrona.dto.poltrona.MapaPoltronasResponseDTO;
import poltrona.dto.sessao.SessaoRequestDTO;
import poltrona.dto.sessao.SessaoResponseDTO;
import poltrona.service.SessaoService;

@RestController
@RequestMapping("/sessoes")
public class SessaoController {

    private final SessaoService sessaoService;

    public SessaoController(SessaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    @PostMapping
    public ResponseEntity<SessaoResponseDTO> cadastrar(@RequestBody @Valid SessaoRequestDTO dto) {

        SessaoResponseDTO sessao = sessaoService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(sessao);

    }

    @GetMapping
    public ResponseEntity<Page<SessaoResponseDTO>> listarTodas(
            @PageableDefault(page = 0, size = 10, sort = "dataHoraInicio", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<SessaoResponseDTO> pagina = sessaoService.listarTodas(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(pagina);

    }

    @GetMapping("/{id}")
    public ResponseEntity<SessaoResponseDTO> buscarPorId(@PathVariable Long id) {

        SessaoResponseDTO sessao = sessaoService.buscarPorId(id);

        return ResponseEntity.status(HttpStatus.OK).body(sessao);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        sessaoService.deletar(id);

        return ResponseEntity.noContent().build();

    }

    @GetMapping("/{id}/mapa-poltronas")
    public ResponseEntity<MapaPoltronasResponseDTO> obterMapaPoltronas(@PathVariable Long id) {

        MapaPoltronasResponseDTO response = sessaoService.obterMapaPoltronas(id);

        return ResponseEntity.ok(response);

    }

}
