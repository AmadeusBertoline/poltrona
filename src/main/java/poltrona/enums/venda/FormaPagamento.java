package poltrona.enums.venda;

import lombok.Getter;

@Getter
public enum FormaPagamento {

    PIX("Pix"),
    CARTAO_CREDITO("Cartão de Crédito"),
    CARTAO_DEBITO("Cartão de Débito"),
    DINHEIRO("Dinheiro em Espécie");

    private final String descricao;

    FormaPagamento(String descricao) {
        this.descricao = descricao;
    }
}