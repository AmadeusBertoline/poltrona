package poltrona.dto.filme;

import java.time.LocalDate;
import java.util.Set;

import poltrona.enums.GeneroFilme;
import poltrona.validation.caminhoImagemValido.CaminhoImagemValido;
import poltrona.validation.dataLancamentoValida.DataLancamentoValida;
import poltrona.validation.duracaoValida.DuracaoValida;
import poltrona.validation.generosValidos.GenerosValidos;
import poltrona.validation.nomeValido.NomeValido;
import poltrona.validation.sinopseValida.SinopseValida;
import poltrona.validation.tituloValido.TituloValido;

public record FilmeRequestDTO(

    @TituloValido
    String titulo,

    @SinopseValida
    String sinopse,

    @GenerosValidos
    Set<GeneroFilme> generos,

    @DuracaoValida
    Integer duracao,

    @NomeValido 
    String diretor,

    @NomeValido 
    String distribuidora,

    @DataLancamentoValida
    LocalDate dataLancamento,

    @CaminhoImagemValido
    String imagePath

) {}
