package poltrona.controller;

import jakarta.validation.Valid;
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
import poltrona.dto.venda.VendaRequestDTO;
import poltrona.dto.venda.VendaResponseDTO;
import poltrona.service.VendaService;

@RestController
@RequestMapping("/vendas")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @PostMapping
    public ResponseEntity<VendaResponseDTO> cadastrar(@Valid @RequestBody VendaRequestDTO dto) {

        VendaResponseDTO venda = vendaService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(venda);

    }

    @GetMapping
    public ResponseEntity<Page<VendaResponseDTO>> listarTodas(
            @RequestParam(required = false) Long clienteId,
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<VendaResponseDTO> vendas = vendaService.listarTodas(clienteId, pageable);

        return ResponseEntity.ok(vendas);

    }

    @GetMapping("/me")
    public ResponseEntity<Page<VendaResponseDTO>> me(
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<VendaResponseDTO> compras = vendaService.me(pageable);

        return ResponseEntity.ok(compras);

    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaResponseDTO> buscarPorId(@PathVariable Long id) {

        VendaResponseDTO venda = vendaService.buscarPorId(id);

        return ResponseEntity.ok(venda);

    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<VendaResponseDTO> cancelar(@PathVariable Long id) {

        VendaResponseDTO venda = vendaService.cancelar(id);

        return ResponseEntity.ok(venda);

    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {

        byte[] pdfBytes = vendaService.gerarPdfComprovanteVenda(id);

        String filename = "compra-" + id + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

}