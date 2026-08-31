package poltrona.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poltrona.entity.Cinema;
import poltrona.entity.Proprietario;

public interface CinemaRepository extends JpaRepository<Cinema, Long> {

        @Modifying
        @Query("""
                            UPDATE Cinema c
                            SET c.status = 0
                            WHERE c.proprietario.id = :proprietarioId
                        """)
        void inativarPorProprietario(@Param("proprietarioId") Long proprietarioId);

        @Query("""
                            SELECT COUNT(c) > 0
                            FROM Cinema c
                            WHERE c.nomeFantasia = :nomeFantasia
                              AND c.proprietario.id = :proprietarioId
                              AND c.id <> :id
                        """)
        boolean existsByNomeFantasiaAndProprietarioIdAndIdNot(
                        @Param("nomeFantasia") String nomeFantasia,
                        @Param("proprietarioId") Long proprietarioId,
                        @Param("id") Long id);

        boolean existsByCnpj(String cnpj);

        boolean existsByNomeFantasiaAndProprietarioId(String nome, Long idProprietario);

        Page<Cinema> findAllByProprietario(Pageable pageable, Proprietario proprietario);

        Optional<Cinema> findByIdAndProprietarioId(Long id, Long idProprietario);

        boolean existsByIdAndProprietarioId(Long id, Long idProprietario);

}
