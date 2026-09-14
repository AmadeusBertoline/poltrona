package poltrona.repository;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poltrona.entity.Sessao;

public interface SessaoRepository extends JpaRepository<Sessao, Long> {

    @Query("""
                SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
                FROM Sessao s
                JOIN s.sala sala
                JOIN sala.cinema cinema
                WHERE sala.id = :salaId
                  AND (:sessaoId IS NULL OR s.id <> :sessaoId)
                  AND s.ativo = true
                  AND s.dataHoraInicio < :novaFimComLimpeza
                  AND FUNCTION('ADDMINUTES', s.dataHoraFim, cinema.politicaOperacional.intervaloLimpezaMinutos) > :novaInicio
            """)
    boolean existeConflitoDeHorario(
            @Param("salaId") Long salaId,
            @Param("sessaoId") Long sessaoId,
            @Param("novaInicio") LocalDateTime novaInicio,
            @Param("novaFimComLimpeza") LocalDateTime novaFimComLimpeza);

    boolean existsById(Long id);

    boolean existsByFilmeIdAndDataHoraFimAfterAndAtivoTrue(Long id, LocalDateTime agora);

    boolean existsByFilmeId(Long id);

    @Query("SELECT s FROM Sessao s " +
            "WHERE (:cinemaId IS NULL OR s.sala.cinema.id = :cinemaId) " +
            "AND (:filmeId IS NULL OR s.filme.id = :filmeId) " +
            "AND (:inicioDia IS NULL OR (s.dataHoraInicio >= :inicioDia AND s.dataHoraInicio < :fimDia)) " +
            "AND (:apenasDisponiveis = false OR s.dataHoraFim > :agora) " +
            "AND s.ativo = true")
    Page<Sessao> findAllByFiltro(
            @Param("cinemaId") Long cinemaId,
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("fimDia") LocalDateTime fimDia,
            @Param("filmeId") Long filmeId,
            @Param("apenasDisponiveis") Boolean apenasDisponiveis,
            @Param("agora") LocalDateTime agora,
            Pageable pageable);

}
