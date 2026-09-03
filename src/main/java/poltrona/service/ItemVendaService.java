package poltrona.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import poltrona.dto.venda.ItemVendaResponseDTO;
import poltrona.repository.ItemVendaRepository;

@Service
public class ItemVendaService {

    private ItemVendaRepository itemVendaRepository;

    public ItemVendaService(ItemVendaRepository itemVendaRepository) {
        this.itemVendaRepository = itemVendaRepository;
    }

    @Transactional
    public ItemVendaResponseDTO cadastrarIngresso() {

    }
}