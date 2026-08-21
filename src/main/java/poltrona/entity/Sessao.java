package poltrona.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import poltrona.enums.StatusSessao;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSessao status;

    @Embedded
    private PoliticaVenda politicaVenda = new PoliticaVenda();

    public Sessao(LocalDateTime dataHoraInicio, Filme filme, Sala sala, Preco preco, PoliticaVenda politicaVenda) {
        this.dataHoraInicio = dataHoraInicio;
        this.filme = filme;
        this.sala = sala;
        this.preco = preco;
        this.status = StatusSessao.AGENDADA;
        if (politicaVenda != null) {
            this.politicaVenda = politicaVenda;
        }
        calcularDataHoraFim();
    }

    public void validarPermiteVenda(LocalDateTime momento) {
        if (this.status == StatusSessao.CANCELADA) {
            throw new RegraNegocioException("Não é possível comprar ingressos para uma sessão cancelada.");
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

    public void alterarStatus(StatusSessao novoStatus) {
        if (novoStatus != null) {
            this.status = novoStatus;
        }
    }

    private void calcularDataHoraFim() {
        if (this.filme != null && this.dataHoraInicio != null) {
            this.dataHoraFim = this.dataHoraInicio.plusMinutes(this.filme.getDuracao());
        }
    }
}