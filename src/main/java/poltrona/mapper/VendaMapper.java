package poltrona.mapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import poltrona.dto.venda.ItemVendaResponseDTO;
import poltrona.dto.venda.VendaRequestDTO;
import poltrona.dto.venda.VendaResponseDTO;
import poltrona.entity.Cliente;
import poltrona.entity.Venda;

@Component
public class VendaMapper {

    private final ItemVendaMapper itemVendaMapper;

    public VendaMapper(ItemVendaMapper itemVendaMapper) {
        this.itemVendaMapper = itemVendaMapper;
    }

    public Venda toEntity(Cliente cliente, VendaRequestDTO dto) {

        return new Venda(cliente, dto.formaPagamento());

    }

    public VendaResponseDTO toDTO(Venda venda) {

        List<ItemVendaResponseDTO> itensDaVenda = venda.getItens().stream().map(itemVendaMapper::toDTO)
                .collect(Collectors.toList());

        return new VendaResponseDTO(
                venda.getId(),
                UUID.fromString(venda.getCodigoComprovante()),
                venda.getDataHora(),
                venda.getValorTotal(),
                venda.getStatus(),
                venda.getFormaPagamento(),
                itensDaVenda);

    }

}
