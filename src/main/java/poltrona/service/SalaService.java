package poltrona.service;

import org.springframework.stereotype.Service;

import poltrona.dto.sala.SalaRequestDTO;
import poltrona.dto.sala.SalaResponseDTO;
import poltrona.repository.SalaRepository;

@Service
public class SalaService {

    private final SalaRepository salaRepository;

    public SalaService(SalaRepository salaRepository){
        this.salaRepository = salaRepository;
    }

    // public SalaResponseDTO cadastrar(SalaRequestDTO dto){

        


    // }
    
}
