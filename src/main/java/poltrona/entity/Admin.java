package poltrona.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "administradores")
@Getter
@Setter
public class Admin extends Usuario {

    private Boolean superAdmin = true;

    @Column(length = 100)
    private String departamentoSuporte;
}
