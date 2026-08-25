package poltrona.mapper;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import poltrona.dto.cinema.CinemaRequestDTO;
import poltrona.dto.cinema.CinemaResponseDTO;
import poltrona.dto.sala.SalaResponseDTO;
import poltrona.entity.Cinema;
import poltrona.entity.Endereco;
import poltrona.entity.Proprietario;

@Component
public class CinemaMapper {

    private final EnderecoMapper enderecoMapper;
    private final SalaMapper salaMapper;

    public CinemaMapper(EnderecoMapper enderecoMapper, SalaMapper salaMapper) {
        this.enderecoMapper = enderecoMapper;
        this.salaMapper = salaMapper;
    }

    public Cinema toEntity(CinemaRequestDTO dto, Proprietario proprietario) {
        if (dto == null)
            return null;

        Endereco endereco = dto.endereco() != null ? enderecoMapper.toEntity(dto.endereco()) : null;

        return new Cinema(
                dto.nomeFantasia(),
                dto.razaoSocial(),
                dto.cnpj(),
                dto.telefone(),
                endereco,
                proprietario);
    }

    public CinemaResponseDTO toDTO(Cinema entidade) {
        if (entidade == null) {
            return null;
        }

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