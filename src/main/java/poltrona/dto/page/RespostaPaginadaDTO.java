package poltrona.dto.page;

import java.util.List;
import org.springframework.data.domain.Page;

public record RespostaPaginadaDTO<T>(
        List<T> conteudo,
        int pagina,
        int tamanhoPagina,
        long totalElementos,
        int totalPaginas) {
    public static <T> RespostaPaginadaDTO<T> de(Page<T> page) {
        return new RespostaPaginadaDTO<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
