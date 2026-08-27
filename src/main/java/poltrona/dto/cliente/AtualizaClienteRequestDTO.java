package poltrona.dto.cliente;

import java.time.LocalDate;

public record AtualizaClienteRequestDTO(

        String nome,
        String email,
        LocalDate dataNascimento

) {
}
