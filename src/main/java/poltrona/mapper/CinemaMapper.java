package poltrona.mapper;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import poltrona.dto.cinema.CinemaRequestDTO;
import poltrona.dto.cinema.CinemaResponseDTO;
import poltrona.dto.sala.SalaResponseDTO;
import poltrona.entity.Cinema;

@Component
public class CinemaMapper {

    private final EnderecoMapper enderecoMapper;
    private final SalaMapper salaMapper;

    public CinemaMapper(EnderecoMapper enderecoMapper, SalaMapper salaMapper) {
        this.enderecoMapper = enderecoMapper;
        this.salaMapper = salaMapper;
    }

    public Cinema toEntity(CinemaRequestDTO dto) {
        if (dto == null)
            return null;

        Cinema entidade = new Cinema();
        entidade.setNomeFantasia(dto.nomeFantasia());
        entidade.setRazaoSocial(dto.razaoSocial());
        entidade.setCnpj(dto.cnpj());
        entidade.setTelefone(dto.telefone());

        if (dto.endereco() != null) {
            entidade.setEndereco(enderecoMapper.toEntity(dto.endereco()));
        }

        return entidade;
    }

    public CinemaResponseDTO toDTO(Cinema entidade) {
        if (entidade == null)
            return null;

        List<SalaResponseDTO> salasDTO = entidade.getSalas() != null
                ? entidade.getSalas().stream().map(salaMapper::toDTO).toList()
                : Collections.emptyList();

        return new CinemaResponseDTO(
                entidade.getId(),
                entidade.getNomeFantasia(),
                entidade.getRazaoSocial(),
                entidade.getCnpj(),
                entidade.getTelefone(),
                enderecoMapper.toDTO(entidade.getEndereco()),
                salasDTO);
    }
}