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
    @JoinColumn(name = "ingresso_id", nullable = true)
    private Ingresso ingresso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = true)
    private Produto produto;

    public ItemVenda(String descricao, BigDecimal precoUnitario, Integer quantidade, TipoItemVenda tipoItem,
            Ingresso ingresso, Produto produto) {
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

    public static ItemVenda criarItemIngresso(Ingresso ingresso, BigDecimal precoCalculado) {
        ItemVenda item = new ItemVenda();
        item.descricao = String.format("Ingresso %s - %s (Poltrona %s)",
                ingresso.getTipo(),
                ingresso.getSessao().getFilme().getTitulo(),
                ingresso.getPoltrona().getNumero());
        item.precoUnitario = precoCalculado;
        item.quantidade = 1;
        item.precoSubtotal = precoCalculado;
        item.tipoItem = TipoItemVenda.INGRESSO;
        item.ingresso = ingresso;
        return item;
    }

    public static ItemVenda criarItemProduto(Produto produto, Integer quantidade) {
        ItemVenda item = new ItemVenda();
        item.descricao = produto.getNome();
        item.precoUnitario = produto.getPreco();
        item.quantidade = quantidade;
        item.precoSubtotal = produto.getPreco().multiply(BigDecimal.valueOf(quantidade));
        item.tipoItem = TipoItemVenda.PRODUTO_CONVENIENCIA;
        item.produto = produto;
        return item;
    }

    public void vincularVenda(Venda venda) {
        this.venda = venda;
    }
}