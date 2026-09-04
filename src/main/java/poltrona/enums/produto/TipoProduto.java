package poltrona.enums.produto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoProduto {

    BEBIDAS("Bebida"),
    PIPOCAS("Pipoca"),
    DOCE("Doce"),
    COMBO("Combo");

    private final String descricao;

    private TipoProduto(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return this.descricao;
    }

}
