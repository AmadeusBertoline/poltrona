package poltrona.dto.usuario;

public record AtualizaSenhaRequestDTO(

        String senhaAtual,
        String novaSenha,
        String confirmarSenha

) {
}
