package poltrona.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusSessao {

    AGENDADA("Agendada"),
    INICIADA("Iniciada"),
    FINALIZADA("Finalizada"),
    CANCELADA("Cancelada");

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
