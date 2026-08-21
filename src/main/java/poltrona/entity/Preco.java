package poltrona.entity;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "precos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Preco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private BigDecimal precoBase;

    @Column(nullable = false)
    private Boolean ativo;

    public Preco(String nome, BigDecimal precoBase) {
        this.nome = nome;
        this.precoBase = precoBase;
        this.ativo = (ativo != null) ? ativo : true;
    }

    public void atualizarPrecoBase(BigDecimal novoPreco) {
        if (novoPreco != null) {
            this.precoBase = novoPreco;
        }
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }
}