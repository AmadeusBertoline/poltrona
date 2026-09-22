package poltrona.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import poltrona.enums.filme.FormatoFilme;
import poltrona.enums.sessao.StatusSessao;
import poltrona.exception.RegraNegocioException;

@Entity
@Table(name = "sessoes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "data_hora_inicio", nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(name = "data_hora_fim", nullable = false)
    private LocalDateTime dataHoraFim;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "filme_id", nullable = false)
    private Filme filme;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormatoFilme formato;

    @Column(nullable = false)
    private Boolean ativo;

    public Sessao(LocalDateTime dataHoraInicio, Filme filme, Sala sala, FormatoFilme formato, Preco preco,
            PoliticaOperacional politicaOperacional) {
        if (dataHoraInicio == null) {
            throw new IllegalArgumentException("A data/hora de início é obrigatória.");
        }
        if (filme == null) {
            throw new IllegalArgumentException("O filme é obrigatório para a sessão.");
        }
        if (sala == null) {
            throw new IllegalArgumentException("A sala é obrigatória para a sessão.");
        }

        this.dataHoraInicio = dataHoraInicio;
        this.filme = filme;
        this.sala = sala;
        this.formato = formato;
        this.preco = preco != null ? preco.getValor() : BigDecimal.ZERO;
        this.ativo = true;
        calcularDataHoraFim();
    }

    public void validarPermiteVenda(LocalDateTime momento) {
        if (!this.ativo) {
            throw new RegraNegocioException("Não é possível comprar ingressos para uma sessão inativa.");
        }

        if (this.dataHoraFim.isBefore(momento)) {
            throw new RegraNegocioException("Não é possível comprar ingressos para sessões já encerradas.");
        }

        int tolerancia = sala.getCinema().getPoliticaOperacional().getToleranciaMinutosCompra();
        if (this.dataHoraInicio.plusMinutes(tolerancia).isBefore(momento)) {
            throw new RegraNegocioException(
                    "Tempo limite para compra ultrapassado. Tolerância: " + tolerancia + " minutos após o início.");
        }
    }

    public void validarPermiteAlteracao(long ingressosVendidos) {
        if (ingressosVendidos > 0) {
            throw new RegraNegocioException(
                    "Não é possível alterar a sessão pois já existem " + ingressosVendidos
                            + " ingresso(s) vendido(s).");
        }

        if (this.dataHoraInicio.isBefore(LocalDateTime.now())) {
            throw new RegraNegocioException("Não é possível alterar uma sessão que já iniciou ou finalizou.");
        }

        if (!this.ativo) {
            throw new RegraNegocioException("Não é possível alterar uma sessão inativa/cancelada.");
        }
    }

    public void permiteExclusao(long ingressosVendidos) {
        if (ingressosVendidos > 0) {
            throw new RegraNegocioException(
                    "Não é possível excluir a sessão pois já existem " + ingressosVendidos
                            + " ingresso(s) vendido(s).");
        }
    }

    public void alterarPreco(BigDecimal novoPreco) {
        if (novoPreco != null) {
            if (novoPreco.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RegraNegocioException("O preço da sessão deve ser maior que zero.");
            }
            this.preco = novoPreco;
        }
    }

    public void alterarSala(Sala novaSala) {
        if (novaSala != null) {
            this.sala = novaSala;
        }
    }

    public void alterarFormato(FormatoFilme novoFormato) {
        if (novoFormato != null) {
            this.formato = novoFormato;
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
            this.dataHoraFim = this.dataHoraInicio.plusMinutes(this.filme.getDuracaoMinutos());
        }
    }

    public StatusSessao getStatus() {
        LocalDateTime agora = LocalDateTime.now();

        if (!this.ativo) {
            return StatusSessao.CANCELADA;
        }

        if (agora.isBefore(this.dataHoraInicio)) {
            return StatusSessao.AGENDADA;
        } else if (agora.isAfter(this.dataHoraInicio) && agora.isBefore(this.dataHoraFim)) {
            return StatusSessao.EM_ANDAMENTO;
        } else {
            return StatusSessao.FINALIZADA;
        }
    }
}