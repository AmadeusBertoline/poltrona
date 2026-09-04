package poltrona.repository;

import java.time.LocalDateTime;

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

        boolean existsByPrecoIdAndDataHoraInicioAfterAndAtivoTrue(
                        Long precoId,
                        LocalDateTime dataHora);

}
