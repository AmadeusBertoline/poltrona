package poltrona.mapper;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import poltrona.dto.cinema.CinemaRequestDTO;
import poltrona.dto.cinema.CinemaResponseDTO;
import poltrona.dto.sala.SalaResponseDTO;
import poltrona.entity.Cinema;

@Component
@RequiredArgsConstructor
public class CinemaMapper {

    private final EnderecoMapper enderecoMapper;
    private final SalaMapper salaMapper;

    public Cinema toEntity(CinemaRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Cinema.builder()
                .nomeFantasia(dto.nomeFantasia())
                .razaoSocial(dto.razaoSocial())
                .cnpj(dto.cnpj())
                .telefone(dto.telefone())
                .endereco(dto.endereco() != null ? enderecoMapper.toEntity(dto.endereco()) : null)
                .build();
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
                salasDTO
        );
    }
}