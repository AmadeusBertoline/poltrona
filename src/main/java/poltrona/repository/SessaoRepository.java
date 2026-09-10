package poltrona.repository;

import java.time.LocalDate;
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
                            WHERE s.sala.id = :salaId
                              AND s.dataHoraInicio < :novaFim
                              AND s.dataHoraFim > :novaInicio
                        """)
        boolean existeConflitoDeHorario(
                        @Param("salaId") Long salaId,
                        @Param("novaInicio") LocalDateTime novaInicio,
                        @Param("novaFim") LocalDateTime novaFim);

        boolean existsById(Long id);

        boolean existsByFilmeIdAndDataHoraFimAfterAndAtivoTrue(Long id, LocalDateTime agora);

        boolean existsByFilmeId(Long id);

        @Query("SELECT s FROM Sessao s " +
                        "WHERE (:cinemaId IS NULL OR s.sala.cinema.id = :cinemaId) " +
                        "AND (:filmeId IS NULL OR s.filme.id = :filmeId) " +
                        "AND (:inicioDia IS NULL OR (s.dataHoraInicio >= :inicioDia AND s.dataHoraInicio < :fimDia)) " +
                        "AND s.ativo = true")
        Page<Sessao> findAllByFiltro(
                        @Param("cinemaId") Long cinemaId,
                        @Param("inicioDia") LocalDateTime inicioDia,
                        @Param("fimDia") LocalDateTime fimDia,
                        @Param("filmeId") Long filmeId,
                        Pageable pageable);

}
