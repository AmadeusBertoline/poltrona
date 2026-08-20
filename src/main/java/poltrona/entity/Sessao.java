package poltrona.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import poltrona.enums.StatusSessao;

@Entity
@Table(name = "sessoes")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Sessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_hora_inicio", nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(name = "data_hora_fim", nullable = false)
    private LocalDateTime dataHoraFim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filme_id", nullable = false)
    private Filme filme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "preco_id", nullable = false)
    private Preco preco;

    @Column(nullable = false)
    private StatusSessao status;

    public void calcularDataHoraFim() {
        if (this.filme != null && this.dataHoraInicio != null) {
            this.dataHoraFim = this.dataHoraInicio.plusMinutes(this.filme.getDuracao());
        }
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
        this.calcularDataHoraFim();
    }

    public void setFilme(Filme filme) {
        this.filme = filme;
        this.calcularDataHoraFim();
    }

}
