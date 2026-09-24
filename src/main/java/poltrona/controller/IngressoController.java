package poltrona.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import poltrona.dto.ingresso.IngressoRequestDTO;
import poltrona.dto.ingresso.IngressoResponseDTO;
import poltrona.entity.Ingresso;
import poltrona.mapper.IngressoMapper;
import poltrona.service.IngressoService;

@RestController
@RequestMapping("/ingressos")
public class IngressoController {

    private final IngressoService ingressoService;
    private final IngressoMapper ingressoMapper;

    public IngressoController(IngressoService ingressoService, IngressoMapper ingressoMapper) {
        this.ingressoService = ingressoService;
        this.ingressoMapper = ingressoMapper;
    }

    @PostMapping
    public ResponseEntity<IngressoResponseDTO> cadastrar(@RequestBody @Valid IngressoRequestDTO dto) {
        Ingresso ingresso = ingressoService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ingressoMapper.toDTO(ingresso));
    }

    @GetMapping
    public ResponseEntity<Page<IngressoResponseDTO>> listarTodos(Pageable pageable) {
        return ResponseEntity.ok(ingressoService.listarTodos(pageable));
    }

    @GetMapping("/meus")
    public ResponseEntity<Page<IngressoResponseDTO>> meusIngressos(Pageable pageable) {
        return ResponseEntity.ok(ingressoService.meusIngressos(pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        ingressoService.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Download do ingresso em PDF", description = "Gera e realiza o download do arquivo PDF contendo o ingresso digital e seu respectivo QR Code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingresso em PDF gerado com sucesso", content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE, schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "Ingresso não encontrado para o ID informado", content = @Content)
    })
    @GetMapping(value = "/{id}/download", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {

        byte[] pdfBytes = ingressoService.gerarPdfIngresso(id);

        String filename = "ingresso-" + id + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}