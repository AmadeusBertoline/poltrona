package poltrona.mapper;

import org.springframework.stereotype.Component;
import poltrona.dto.venda.ItemVendaResponseDTO;
import poltrona.dto.venda.VendaRequestDTO;
import poltrona.dto.venda.VendaResponseDTO;
import poltrona.entity.Usuario;
import poltrona.entity.Venda;
import java.util.List;
import java.util.UUID;

@Component
public class VendaMapper {

    private final ItemVendaMapper itemVendaMapper;

    public VendaMapper(ItemVendaMapper itemVendaMapper) {
        this.itemVendaMapper = itemVendaMapper;
    }

    public Venda toEntity(VendaRequestDTO dto, Usuario cliente) {
        if (dto == null) {
            return null;
        }

        return new Venda(cliente, dto.formaPagamento());
    }

    public VendaResponseDTO toDTO(Venda venda) {
        if (venda == null) {
            return null;
        }

        List<ItemVendaResponseDTO> itensDTO = venda.getItens().stream()
                .map(itemVendaMapper::toDTO)
                .toList();

        return new VendaResponseDTO(
                venda.getId(),
                UUID.fromString(venda.getCodigoComprovante()),
                venda.getDataHora(),
                venda.getValorTotal(),
                venda.getStatus(),
                venda.getFormaPagamento(),
                itensDTO);
    }
}