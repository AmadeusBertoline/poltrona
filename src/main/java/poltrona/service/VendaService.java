package poltrona.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poltrona.dto.venda.VendaRequestDTO;
import poltrona.dto.venda.VendaResponseDTO;
import poltrona.entity.Cliente;
import poltrona.mapper.VendaMapper;
import poltrona.repository.VendaRepository;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final UsuarioService usuarioService;
    private final VendaMapper vendaMapper;

    public VendaService(VendaRepository vendaRepository, UsuarioService usuarioService, VendaMapper vendaMapper) {
        this.vendaRepository = vendaRepository;
        this.usuarioService = usuarioService;
        this.vendaMapper = vendaMapper;
    }

    @Transactional
    public VendaResponseDTO cadastrar(VendaRequestDTO dto) {

        Cliente cliente = (Cliente) usuarioService.usuarioLogado();

    }

}
