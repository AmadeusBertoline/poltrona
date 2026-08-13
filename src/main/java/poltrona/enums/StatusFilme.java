package poltrona.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusFilme {

    PRE_ESTREIA("Pré Estréia"),
    ESTREIA("Estreia"),
    EM_BREVE("Em breve"),
    EM_CARTAZ("Cartaz");

    private final String descricao;

    private StatusFilme(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

}
