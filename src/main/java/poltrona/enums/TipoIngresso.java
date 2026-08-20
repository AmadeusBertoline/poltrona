package poltrona.enums;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoIngresso {
    INTEIRA(new BigDecimal("1.00")),
    MEIA(new BigDecimal("0.50"));

    private final BigDecimal multiplicador;

    TipoIngresso(BigDecimal multiplicador) {
        this.multiplicador = multiplicador;
    }

    @JsonValue
    public BigDecimal calcularPrecoFinal(BigDecimal precoBase) {
        return precoBase.multiply(this.multiplicador);
    }
}
