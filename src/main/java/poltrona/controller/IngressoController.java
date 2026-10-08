package poltrona.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import poltrona.controller.doc.IngressoControllerDoc;
import poltrona.dto.ingresso.IngressoResponseDTO;
import poltrona.service.IngressoService;

@RestController
@RequestMapping("/ingressos")
public class IngressoController implements IngressoControllerDoc {

    private final IngressoService ingressoService;

    public IngressoController(IngressoService ingressoService) {
        this.ingressoService = ingressoService;
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<IngressoResponseDTO>> listarTodos(
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ingressoService.listarTodos(pageable));
    }

    @Override
    @GetMapping("/meus")
    public ResponseEntity<Page<IngressoResponseDTO>> meusIngressos(
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ingressoService.meusIngressos(pageable));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<IngressoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ingressoService.buscarPorId(id));
    }

    @Override
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        byte[] pdfBytes = ingressoService.gerarPdfIngresso(id);
        String filename = "ingresso-" + id + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @Override
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        ingressoService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}