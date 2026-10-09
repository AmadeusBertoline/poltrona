package poltrona.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import poltrona.controller.doc.VendaControllerDoc;
import poltrona.dto.venda.VendaRequestDTO;
import poltrona.dto.venda.VendaResponseDTO;
import poltrona.service.VendaService;

@RestController
@RequestMapping("/vendas")
public class VendaController implements VendaControllerDoc {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @Override
    @PostMapping
    public ResponseEntity<VendaResponseDTO> cadastrar(
            @RequestBody @Valid VendaRequestDTO dto) {

        VendaResponseDTO venda = vendaService.cadastrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(venda);
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<VendaResponseDTO>> listarTodas(
            @RequestParam(required = false) Long clienteId,

            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<VendaResponseDTO> vendas = vendaService.listarTodas(clienteId, pageable);

        return ResponseEntity.ok(vendas);
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<Page<VendaResponseDTO>> me(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<VendaResponseDTO> compras = vendaService.me(pageable);

        return ResponseEntity.ok(compras);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<VendaResponseDTO> buscarPorId(
            @PathVariable Long id) {

        VendaResponseDTO venda = vendaService.buscarPorId(id);

        return ResponseEntity.ok(venda);
    }

    @Override
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<VendaResponseDTO> cancelar(
            @PathVariable Long id) {

        VendaResponseDTO venda = vendaService.cancelar(id);

        return ResponseEntity.ok(venda);
    }

    @Override
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable Long id) {

        byte[] pdfBytes = vendaService.gerarPdfComprovanteVenda(id);

        String filename = "compra-" + id + ".pdf";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}