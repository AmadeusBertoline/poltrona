package poltrona.repository;

import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poltrona.entity.Ingresso;
import poltrona.enums.ingresso.StatusIngresso;

public interface IngressoRepository extends JpaRepository<Ingresso, Long> {

        boolean existsBySessaoIdAndPoltronaIdAndStatus(Long sessaoId, Long poltronaId, StatusIngresso status);

        @Query("SELECT i.poltrona.id FROM Ingresso i WHERE i.sessao.id = :sessaoId")
        Set<Long> findPoltronaIdsBySessaoId(@Param("sessaoId") Long sessaoId);

        boolean existsBySessaoSalaCinemaProprietarioIdAndSessaoDataHoraFimAfter(
                        Long proprietarioId,
                        LocalDateTime agora);

        boolean existsBySessaoSalaCinemaIdAndSessaoDataHoraFimAfter(
                        Long proprietarioId,
                        LocalDateTime agora);

        boolean existsByStatusAndUsuarioIdAndSessaoDataHoraFimAfter(
                        StatusIngresso status,
                        Long usuarioId,
                        LocalDateTime agora);

        boolean existsByPoltronaIdAndSessaoDataHoraInicioAfterAndStatus(
                        Long poltronaId,
                        LocalDateTime dataHora,
                        StatusIngresso status);

        boolean existsBySessaoSalaIdAndSessaoDataHoraInicioAfterAndStatus(
                        Long salaId,
                        LocalDateTime dataHora,
                        StatusIngresso status);


        boolean existsByIdAndUsuarioId(Long id, Long usuarioId);

        Page<Ingresso> findAllByUsuarioIdOrderByDataCriacaoDesc(Long id, Pageable pageable);

}
