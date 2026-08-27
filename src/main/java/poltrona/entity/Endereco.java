package poltrona.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Endereco {

    @Column(nullable = false)
    private String logradouro;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(nullable = true)
    private String complemento;

    @Column(nullable = false)
    private String bairro;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String uf;

    @Column(nullable = false, length = 8)
    private String cep;

    public Endereco(String logradouro, String numero, String complemento, String bairro, String cidade, String uf,
            String cep) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.cep = cep;
    }

    public void atualizar(String logradouro, String numero, String complemento, String bairro, String cidade, String uf,
            String cep) {

        if (logradouro != null && !logradouro.isBlank()) {
            this.logradouro = logradouro;
        }
        if (numero != null && !numero.isBlank()) {
            this.numero = numero;
        }
        if (complemento != null) {

            this.complemento = complemento;
        }
        if (bairro != null && !bairro.isBlank()) {
            this.bairro = bairro;
        }
        if (cidade != null && !cidade.isBlank()) {
            this.cidade = cidade;
        }
        if (uf != null && !uf.isBlank()) {
            this.uf = uf;
        }
        if (cep != null && !cep.isBlank()) {
            this.cep = cep;
        }
    }

}