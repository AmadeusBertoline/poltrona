package poltrona.repository;

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
                SET c.ativo = false
                WHERE c.proprietario.id = :proprietarioId
            """)
    void inativarPorProprietario(@Param("proprietarioId") Long proprietarioId);

    boolean existsByCnpj(String cnpj);

    boolean existsByNomeFantasiaAndProprietarioId(String nome, Long idProprietario);

    Page<Cinema> findAllByProprietario(Pageable pageable, Proprietario proprietario);

}
