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
public class PoliticaOperacional {

    @Column(name = "tolerancia_minutos_compra")
    private Integer toleranciaMinutosCompra;

    @Column(name = "antecedencia_minutos_cancelamento")
    private Integer antecedenciaMinutosCancelamento;

    @Column(name = "intervalo_limpeza_minutos")
    private Integer intervaloLimpezaMinutos;

    public PoliticaOperacional(Integer toleranciaMinutosCompra,
            Integer antecedenciaMinutosCancelamento,
            Integer intervaloLimpezaMinutos) {
        if (toleranciaMinutosCompra != null)
            this.toleranciaMinutosCompra = toleranciaMinutosCompra;
        if (antecedenciaMinutosCancelamento != null)
            this.antecedenciaMinutosCancelamento = antecedenciaMinutosCancelamento;
        if (intervaloLimpezaMinutos != null)
            this.intervaloLimpezaMinutos = intervaloLimpezaMinutos;
    }

    public boolean isVendaPermitida(LocalDateTime inicioSessao) {
        LocalDateTime limite = inicioSessao.plusMinutes(toleranciaMinutosCompra);
        return !LocalDateTime.now().isAfter(limite);
    }

    public boolean isCancelamentoPermitido(LocalDateTime inicioSessao) {
        LocalDateTime limite = inicioSessao.minusMinutes(antecedenciaMinutosCancelamento);
        return !LocalDateTime.now().isAfter(limite);
    }

    public void atualizar(Integer toleranciaMinutosCompra,
            Integer antecedenciaMinutosCancelamento,
            Integer intervaloLimpezaMinutos) {
        if (toleranciaMinutosCompra != null)
            this.toleranciaMinutosCompra = toleranciaMinutosCompra;
        if (antecedenciaMinutosCancelamento != null)
            this.antecedenciaMinutosCancelamento = antecedenciaMinutosCancelamento;
        if (intervaloLimpezaMinutos != null)
            this.intervaloLimpezaMinutos = intervaloLimpezaMinutos;
    }
}