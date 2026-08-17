package poltrona.dto.login;

public record LoginResponseDTO(

        String token,
        String tipo,
        Long id,
        String email,
        String role

) {
}
