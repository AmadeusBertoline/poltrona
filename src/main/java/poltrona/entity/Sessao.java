package poltrona.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import poltrona.exception.RegraNegocioException;

@Entity
@Table(name = "sessoes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private Boolean ativo;

    @Embedded
    private PoliticaVenda politicaVenda = new PoliticaVenda();

    public Sessao(LocalDateTime dataHoraInicio, Filme filme, Sala sala, Preco preco, PoliticaVenda politicaVenda) {
        this.dataHoraInicio = dataHoraInicio;
        this.filme = filme;
        this.sala = sala;
        this.preco = preco;
        this.ativo = true;
        if (politicaVenda != null) {
            this.politicaVenda = politicaVenda;
        }
        calcularDataHoraFim();
    }

    public void validarPermiteVenda(LocalDateTime momento) {
        if (this.ativo == false) {
            throw new RegraNegocioException("Não é possível comprar ingressos para uma sessão inativa.");
        }

        if (this.dataHoraFim.isBefore(momento)) {
            throw new RegraNegocioException("Não é possível comprar ingressos para sessões já encerradas.");
        }

        int tolerancia = this.politicaVenda.getToleranciaMinutosCompra();
        if (this.dataHoraInicio.plusMinutes(tolerancia).isBefore(momento)) {
            throw new RegraNegocioException(
                    "Tempo limite para compra ultrapassado. Tolerância: " + tolerancia + " minutos após o início.");
        }
    }

    public void alterarHorario(LocalDateTime novoHorario) {
        if (novoHorario != null) {
            this.dataHoraInicio = novoHorario;
            calcularDataHoraFim();
        }
    }

    public void alterarFilme(Filme novoFilme) {
        if (novoFilme != null) {
            this.filme = novoFilme;
            calcularDataHoraFim();
        }
    }

    public void alterarStatus(Boolean novoStatus) {
        if (novoStatus != null) {
            this.ativo = novoStatus;
        }
    }

    private void calcularDataHoraFim() {
        if (this.filme != null && this.dataHoraInicio != null) {
            this.dataHoraFim = this.dataHoraInicio.plusMinutes(this.filme.getDuracao());
        }
    }
}