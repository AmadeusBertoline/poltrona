package poltrona.enums.venda;

import lombok.Getter;

@Getter
public enum StatusVenda {
    
    PENDENTE("Aguardando confirmação do pagamento"),
    PAGA("Pagamento confirmado e venda efetuada"),
    CANCELADA("Venda cancelada"),
    ESTORNADA("Pagamento estornado/devolvido ao cliente");

    private final String descricao;

    StatusVenda(String descricao) {
        this.descricao = descricao;
    }
}