package poltrona.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poltrona.dto.venda.VendaRequestDTO;
import poltrona.dto.venda.VendaResponseDTO;
import poltrona.service.VendaService;

@RestController
@RequestMapping("/vendas")
public class VendaController {

    private VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @PostMapping
    public ResponseEntity<VendaResponseDTO> cadastrar(@RequestBody VendaRequestDTO dto) {

        VendaResponseDTO venda = vendaService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(venda);

    }

    @GetMapping
    public ResponseEntity<Page<VendaResponseDTO>> listarTodos(
            @PageableDefault(page = 0, size = 10, sort = "dataCriacao", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<VendaResponseDTO> vendas = vendaService.listarTodas(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(vendas);

    }

}
