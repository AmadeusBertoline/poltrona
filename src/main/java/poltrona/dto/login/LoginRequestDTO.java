package poltrona.dto.login;

public record LoginRequestDTO (

    String emailOrCpf,
    String senha

){}
