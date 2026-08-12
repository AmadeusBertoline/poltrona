package poltrona.dto.filme;

import java.time.LocalDate;

import poltrona.enums.StatusFilme;
import poltrona.validation.CaminhoImagemValido.CaminhoImagemValido;
import poltrona.validation.DataLancamentoValida.DataLancamentoValida;
import poltrona.validation.DuracaoValida.DuracaoValida;
import poltrona.validation.NomeValido.NomeValido;
import poltrona.validation.SinopseValida.SinopseValida;
import poltrona.validation.StatusValido.StatusValido;
import poltrona.validation.TituloValido.TituloValido;

public record FilmeRequestDTO(

    @TituloValido
    String titulo,

    @SinopseValida
    String sinopse,

    @DuracaoValida
    Integer duracao,

    @NomeValido 
    String diretor,

    @NomeValido 
    String distribuidora,

    @DataLancamentoValida
    LocalDate dataLancamento,

    @CaminhoImagemValido
    String imagePath,

    @StatusValido
    StatusFilme status

) {
}
