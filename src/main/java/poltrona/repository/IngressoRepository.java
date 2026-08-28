package poltrona.repository;

import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poltrona.entity.Ingresso;

public interface IngressoRepository extends JpaRepository<Ingresso, Long> {

        boolean existsBySessaoIdAndPoltronaId(Long sessaoId, Long poltronaId);

        @Query("SELECT i.poltrona.id FROM Ingresso i WHERE i.sessao.id = :sessaoId")
        Set<Long> findPoltronaIdsBySessaoId(@Param("sessaoId") Long sessaoId);

        boolean existsBySessaoSalaCinemaProprietarioIdAndSessaoDataHoraFimAfter(
                        Long proprietarioId,
                        LocalDateTime agora);

        boolean existsBySessaoSalaCinemaIdAndSessaoDataHoraFimAfter(
                        Long proprietarioId,
                        LocalDateTime agora);

        boolean existsByUsuarioIdAndSessaoDataHoraFimAfter(Long id, LocalDateTime agora);

}
