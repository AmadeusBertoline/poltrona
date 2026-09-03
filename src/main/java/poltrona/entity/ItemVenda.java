package poltrona.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import poltrona.enums.venda.TipoItemVenda;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_venda")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao;

    @Column(name = "preco_unitario", nullable = false)
    private BigDecimal precoUnitario;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_subtotal", nullable = false)
    private BigDecimal precoSubtotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item", nullable = false)
    private TipoItemVenda tipoItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingresso_id")
    private Ingresso ingresso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id")
    private Produto produto;

    public ItemVenda(String descricao, BigDecimal precoUnitario, Integer quantidade,
            TipoItemVenda tipoItem, Ingresso ingresso, Produto produto) {
        this.descricao = descricao;
        this.precoUnitario = precoUnitario;
        this.quantidade = quantidade;
        this.precoSubtotal = (precoUnitario != null && quantidade != null)
                ? precoUnitario.multiply(BigDecimal.valueOf(quantidade))
                : BigDecimal.ZERO;
        this.tipoItem = tipoItem;
        this.ingresso = ingresso;
        this.produto = produto;
    }

    public void vincularVenda(Venda venda) {
        if (venda == null) {
            throw new IllegalArgumentException("A venda não pode ser nula ao vincular o item.");
        }
        this.venda = venda;
    }

}