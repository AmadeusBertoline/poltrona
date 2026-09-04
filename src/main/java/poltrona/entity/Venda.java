package poltrona.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import poltrona.enums.venda.FormaPagamento;
import poltrona.enums.venda.StatusVenda;

@Entity
@Table(name = "vendas")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_comprovante", nullable = false, unique = true, updatable = false)
    private String codigoComprovante;

    @Column(name = "data_hora", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusVenda status;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false)
    private FormaPagamento formaPagamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVenda> itens = new ArrayList<>();

    public Venda(Usuario cliente, FormaPagamento formaPagamento) {
        if (cliente == null) {
            throw new IllegalArgumentException("O cliente é obrigatório para registrar a venda.");
        }
        if (formaPagamento == null) {
            throw new IllegalArgumentException("A forma de pagamento é obrigatória.");
        }

        this.codigoComprovante = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusVenda.PAGA;
        this.formaPagamento = formaPagamento;
        this.cliente = cliente;
        this.valorTotal = BigDecimal.ZERO;
    }

    public void adicionarItens(List<ItemVenda> novosItens) {
        if (novosItens == null || novosItens.isEmpty()) {
            throw new IllegalArgumentException("A venda precisa conter ao menos um item.");
        }

        for (ItemVenda item : novosItens) {
            item.vincularVenda(this);
            this.itens.add(item);
            this.valorTotal = this.valorTotal.add(item.getPrecoSubtotal());
        }
    }

    public void cancelar() {
        if (this.status == StatusVenda.CANCELADA) {
            throw new IllegalStateException("Esta venda já se encontra cancelada.");
        }
        this.status = StatusVenda.CANCELADA;
    }

    public List<ItemVenda> getItens() {
        return Collections.unmodifiableList(this.itens);
    }
}