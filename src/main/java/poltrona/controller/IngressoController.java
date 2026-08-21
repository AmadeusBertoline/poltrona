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
import poltrona.dto.ingresso.IngressoRequestDTO;
import poltrona.dto.ingresso.IngressoResponseDTO;
import poltrona.service.IngressoService;

@RestController
@RequestMapping("/ingressos")
public class IngressoController {

    private final IngressoService ingressoService;

    public IngressoController(IngressoService ingressoService) {
        this.ingressoService = ingressoService;
    }

    @PostMapping
    public ResponseEntity<IngressoResponseDTO> cadastrar(@RequestBody IngressoRequestDTO dto) {

        IngressoResponseDTO ingresso = ingressoService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(ingresso);

    }

    @GetMapping
    public ResponseEntity<Page<IngressoResponseDTO>> listarTodos(
            @PageableDefault(page = 0, size = 10, sort = "preco", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<IngressoResponseDTO> ingressos = ingressoService.listarTodos(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ingressos);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        ingressoService.cancelar(id);
        return ResponseEntity.noContent().build();
    }

}
