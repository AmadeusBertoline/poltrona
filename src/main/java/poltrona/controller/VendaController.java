package poltrona.controller;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import poltrona.dto.venda.VendaRequestDTO;
import poltrona.dto.venda.VendaResponseDTO;
import poltrona.service.VendaService;

@Tag(name = "Vendas", description = "Gerenciamento e processamento de vendas de ingressos e produtos da bomboniere")
@RestController
@RequestMapping("/vendas")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @Operation(summary = "Realizar venda", description = "Registra uma nova compra no sistema contendo ingressos e/ou itens de bomboniere")
    @PostMapping
    public ResponseEntity<VendaResponseDTO> cadastrar(@Valid @RequestBody VendaRequestDTO dto) {

        VendaResponseDTO venda = vendaService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(venda);

    }

    @Operation(summary = "Listar todas as vendas", description = "Lista o histórico de vendas cadastradas de forma paginada com suporte a filtro por cliente (acesso administrativo)")
    @GetMapping
    public ResponseEntity<Page<VendaResponseDTO>> listarTodas(
            @RequestParam(required = false) Long clienteId,
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<VendaResponseDTO> vendas = vendaService.listarTodas(clienteId, pageable);

        return ResponseEntity.ok(vendas);

    }

    @Operation(summary = "Minhas compras", description = "Retorna o histórico de compras paginado do cliente atualmente autenticado")
    @GetMapping("/me")
    public ResponseEntity<Page<VendaResponseDTO>> me(
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<VendaResponseDTO> compras = vendaService.me(pageable);

        return ResponseEntity.ok(compras);

    }

    @Operation(summary = "Buscar venda por ID", description = "Busca os detalhes completos de uma transação de venda pelo seu identificador")
    @GetMapping("/{id}")
    public ResponseEntity<VendaResponseDTO> buscarPorId(@PathVariable Long id) {

        VendaResponseDTO venda = vendaService.buscarPorId(id);

        return ResponseEntity.ok(venda);

    }

    @Operation(summary = "Cancelar venda", description = "Cancela uma venda existente, liberando os ingressos/poltronas associados e estornando os itens")
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