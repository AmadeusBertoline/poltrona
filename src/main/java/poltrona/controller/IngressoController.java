package poltrona.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import poltrona.dto.ingresso.IngressoResponseDTO;
import poltrona.service.IngressoService;

@Tag(name = "Ingressos", description = "Gerenciamento de Ingressos")
@RestController
@RequestMapping("/ingressos")
public class IngressoController {

    private final IngressoService ingressoService;

    public IngressoController(IngressoService ingressoService) {
        this.ingressoService = ingressoService;
    }

    @GetMapping
    public ResponseEntity<Page<IngressoResponseDTO>> listarTodos(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ingressoService.listarTodos(pageable));
    }

    @GetMapping("/meus")
    public ResponseEntity<Page<IngressoResponseDTO>> meusIngressos(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ingressoService.meusIngressos(pageable));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        byte[] pdfBytes = ingressoService.gerarPdfIngresso(id);

        String filename = "ingresso-" + id + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}