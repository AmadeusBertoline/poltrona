package poltrona.dto.poltrona;

public record PoltronaStatusDTO(
        Long poltronaId,
        String numero,
        boolean ocupada) {
}
