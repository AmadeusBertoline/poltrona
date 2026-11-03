package poltrona.repository;

import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;
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
                        Long cinemaId,
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

        boolean existsBySessaoSalaCinemaId(Long cinemaId);


        boolean existsByIdAndUsuarioId(Long id, Long usuarioId);

        @EntityGraph(attributePaths = {
                        "usuario", "sessao", "sessao.filme", "sessao.sala", "sessao.sala.cinema",
                        "poltrona", "poltrona.sala", "poltrona.sala.cinema"
        })
        Page<Ingresso> findAllByUsuarioIdOrderByDataCriacaoDesc(Long id, Pageable pageable);

        @EntityGraph(attributePaths = {
                        "usuario", "sessao", "sessao.filme", "sessao.sala", "sessao.sala.cinema",
                        "poltrona", "poltrona.sala", "poltrona.sala.cinema"
        })
        Page<Ingresso> findAllBySessaoSalaCinemaId(Long cinemaId, Pageable pageable);

        @EntityGraph(attributePaths = {
                        "usuario", "sessao", "sessao.filme", "sessao.sala", "sessao.sala.cinema",
                        "poltrona", "poltrona.sala", "poltrona.sala.cinema"
        })
        Page<Ingresso> findAllBySessaoSalaCinemaProprietarioId(Long proprietarioId, Pageable pageable);

        @EntityGraph(attributePaths = {
                        "usuario", "sessao", "sessao.filme", "sessao.sala", "sessao.sala.cinema",
                        "poltrona", "poltrona.sala", "poltrona.sala.cinema"
        })
        @Override
        Optional<Ingresso> findById(Long id);

        @EntityGraph(attributePaths = {
                        "usuario", "sessao", "sessao.filme", "sessao.sala", "sessao.sala.cinema",
                        "poltrona", "poltrona.sala", "poltrona.sala.cinema"
        })
        Page<Ingresso> findAll(Pageable pageable);

        boolean existsBySessaoSalaId(Long salaId);

        Long countBySessaoId(Long sessaoId);

}
