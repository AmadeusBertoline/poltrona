package poltrona.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusSessao {

    EM_BREVE("Em breve"),
    INICIADA("Iniciada"),
    FINALIZADA("Finalizada");

    private final String status;

    private StatusSessao(String status) {
        {
            this.status = status;
        }
    }

    @JsonValue
    public String getStatus() {
        return status;
    }

}
