package poltrona.mapper;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import poltrona.dto.cinema.CinemaResponseDTO;
import poltrona.dto.proprietario.ProprietarioRequestDTO;
import poltrona.dto.proprietario.ProprietarioResponseDTO;
import poltrona.dto.usuario.UsuarioResponseDTO;
import poltrona.entity.Proprietario;

@Component
public class ProprietarioMapper {

    private final CinemaMapper cinemaMapper;

    public ProprietarioMapper(CinemaMapper cinemaMapper) {
        this.cinemaMapper = cinemaMapper;
    }

    public Proprietario toEntity(ProprietarioRequestDTO dto, String senha) {
        if (dto == null || dto.usuario() == null) {
            return null;
        }

        return new Proprietario(
                dto.usuario().nome(),
                dto.usuario().email(),
                senha,
                dto.usuario().cpf(),
                dto.usuario().dataNascimento());
    }

    public ProprietarioResponseDTO toDTO(Proprietario proprietario) {
        if (proprietario == null) {
            return null;
        }

        UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO(
                proprietario.getId(),
                proprietario.getNome(),
                proprietario.getEmail(),
                proprietario.getCpf(),
                proprietario.getDataNascimento(),
                proprietario.getAtivo(),
                proprietario.getDataCriacao());

        List<CinemaResponseDTO> cinemas = (proprietario.getCinemas() == null)
                ? Collections.emptyList()
                : proprietario.getCinemas()
                        .stream()
                        .map(cinemaMapper::toDTO)
                        .toList();

        return new ProprietarioResponseDTO(
                usuarioDTO,
                cinemas);
    }
}