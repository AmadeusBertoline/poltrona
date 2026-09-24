package poltrona.enums.ingresso;

import java.math.BigDecimal;

public enum TipoIngresso {
    INTEIRA(new BigDecimal("1.00")),
    MEIA(new BigDecimal("0.50"));

    private final BigDecimal multiplicador;

    TipoIngresso(BigDecimal multiplicador) {
        this.multiplicador = multiplicador;
    }

    public BigDecimal getMultiplicador() {
        return multiplicador;
    }

    public BigDecimal calcularPrecoFinal(BigDecimal precoBase) {
        return precoBase.multiply(this.multiplicador);
    }
}