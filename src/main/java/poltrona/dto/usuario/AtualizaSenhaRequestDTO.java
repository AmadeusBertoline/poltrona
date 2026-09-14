package poltrona.dto.usuario;

import poltrona.validation.senhaValida.SenhaValida;

public record AtualizaSenhaRequestDTO(

        @SenhaValida 
        String senhaAtual,

        @SenhaValida 
        String novaSenha,

        @SenhaValida 
        String confirmarSenha

) {
}
