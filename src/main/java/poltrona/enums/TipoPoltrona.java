package poltrona.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoPoltrona {

    COMUM("Comum"),
    PREFERENCIAL("Preferencial"),
    NAMORADEIRA("Namoradeira"),
    D_BOX("D-Box");

    private final String descricao;

    private TipoPoltrona(String descricao){
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao(){
        return descricao;
    }
    
}
