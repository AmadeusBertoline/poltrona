package poltrona.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import poltrona.dto.sessao.SessaoFiltroDTO;
import poltrona.entity.Sessao;

public interface SessaoRepository extends JpaRepository<Sessao, Long> {

  @Query("""
      SELECT COUNT(s) > 0
      FROM Sessao s
      WHERE s.sala.id = :salaId
        AND (:sessaoId IS NULL OR s.id <> :sessaoId)
        AND s.ativo = true
        AND s.dataHoraInicio < :novaFimComLimpeza
        AND s.dataHoraFim > :novaInicioMenosLimpeza
      """)
  boolean existeConflitoDeHorario(
      @Param("salaId") Long salaId,
      @Param("sessaoId") Long sessaoId,
      @Param("novaInicioMenosLimpeza") LocalDateTime novaInicioMenosLimpeza,
      @Param("novaFimComLimpeza") LocalDateTime novaFimComLimpeza);

  boolean existsByFilmeIdAndDataHoraFimAfterAndAtivoTrue(Long filmeId, LocalDateTime agora);

  boolean existsByFilmeId(Long filmeId);

  @Query("""
              SELECT s FROM Sessao s
              WHERE (:#{#filtro.cinemaId} IS NULL OR s.sala.cinema.id = :#{#filtro.cinemaId})
                AND (:#{#filtro.filmeId} IS NULL OR s.filme.id = :#{#filtro.filmeId})
                AND (:#{#filtro.data} IS NULL OR CAST(s.dataHoraInicio AS LocalDate) = :#{#filtro.data})
                AND (:#{#filtro.apenasDisponiveis} IS FALSE OR
                     (s.sala.capacidade - (SELECT COUNT(i) FROM Ingresso i WHERE i.sessao = s)) > 0)
      """)
  Page<Sessao> buscarComFiltros(@Param("filtro") SessaoFiltroDTO filtro, Pageable pageable);

}
