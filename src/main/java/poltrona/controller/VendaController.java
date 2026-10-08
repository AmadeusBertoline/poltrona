package poltrona.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<VendaResponseDTO> cadastrar(VendaRequestDTO dto) {
        VendaResponseDTO venda = vendaService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(venda);
    }

    @Override
    public ResponseEntity<Page<VendaResponseDTO>> listarTodas(Long clienteId, Pageable pageable) {
        Page<VendaResponseDTO> vendas = vendaService.listarTodas(clienteId, pageable);
        return ResponseEntity.ok(vendas);
    }

    @Override
    public ResponseEntity<Page<VendaResponseDTO>> me(Pageable pageable) {
        Page<VendaResponseDTO> compras = vendaService.me(pageable);
        return ResponseEntity.ok(compras);
    }

    @Override
    public ResponseEntity<VendaResponseDTO> buscarPorId(Long id) {
        VendaResponseDTO venda = vendaService.buscarPorId(id);
        return ResponseEntity.ok(venda);
    }

    @Override
    public ResponseEntity<VendaResponseDTO> cancelar(Long id) {
        VendaResponseDTO venda = vendaService.cancelar(id);
        return ResponseEntity.ok(venda);
    }

    @Override
    public ResponseEntity<byte[]> downloadPdf(Long id) {
        byte[] pdfBytes = vendaService.gerarPdfComprovanteVenda(id);
        String filename = "compra-" + id + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}