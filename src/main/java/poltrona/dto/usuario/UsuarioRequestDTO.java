package poltrona.dto.usuario;

public record UsuarioRequestDTO (

    String nome,
    String email,
    String cpf,
    String senha,
    String confirmarSenha

){}
