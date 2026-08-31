package poltrona.enums.filme;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GeneroFilme {
    ACAO("Ação"),
    AVENTURA("Aventura"),
    COMEDIA("Comédia"),
    DRAMA("Drama"),
    FICCAO_CIENTIFICA("Ficção Científica"),
    TERROR("Terror"),
    SUSPENSE("Suspense"),
    ROMANCE("Romance"),
    ANIMACAO("Animação"),
    FANTASIA("Fantasia"),
    DOCUMENTARIO("Documentário"),
    POLICIAL("Policial"),
    MUSICAL("Musical"),
    MISTERIO("Mistério");

    private final String descricao;

    GeneroFilme(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }
}
