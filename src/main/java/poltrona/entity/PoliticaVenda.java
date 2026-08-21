package poltrona.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PoliticaVenda {

    @Column(name = "tolerancia_minutos_compra")
    private Integer toleranciaMinutosCompra = 15;

    public PoliticaVenda(Integer toleranciaMinutosCompra) {
        if (toleranciaMinutosCompra != null) {
            this.toleranciaMinutosCompra = toleranciaMinutosCompra;
        }
    }

    public boolean isVendaPermitida(LocalDateTime inicioSessao, LocalDateTime momento) {
        LocalDateTime limite = inicioSessao.plusMinutes(toleranciaMinutosCompra);
        return !momento.isAfter(limite);
    }
}