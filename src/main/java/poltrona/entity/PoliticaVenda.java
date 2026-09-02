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

    @Column(name = "antecedencia_minutos_cancelamento")
    private Integer antecedenciaMinutosCancelamento = 30;

    public PoliticaVenda(Integer toleranciaMinutosCompra, Integer antecedenciaMinutosCancelamento) {
        if (toleranciaMinutosCompra != null) {
            this.toleranciaMinutosCompra = toleranciaMinutosCompra;
        }
        if (antecedenciaMinutosCancelamento != null) {
            this.antecedenciaMinutosCancelamento = antecedenciaMinutosCancelamento;
        }
    }

    public boolean isVendaPermitida(LocalDateTime inicioSessao, LocalDateTime momento) {
        LocalDateTime limite = inicioSessao.plusMinutes(toleranciaMinutosCompra);
        return !momento.isAfter(limite);
    }

    public boolean isCancelamentoPermitido(LocalDateTime inicioSessao, LocalDateTime momento) {
        LocalDateTime limite = inicioSessao.minusMinutes(antecedenciaMinutosCancelamento);
        return !momento.isAfter(limite);
    }
}