package poltrona.dto.proprietario;

import java.time.LocalDate;

public record AtualizaProprietarioRequestDTO(

        String nome,
        String email,
        LocalDate dataNascimento

) {}
