package poltrona.repository;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poltrona.dto.filme.FilmeFiltroDTO;
import poltrona.entity.Filme;

public interface FilmeRepository extends JpaRepository<Filme, Long> {

    boolean existsByTituloIgnoreCaseAndDataLancamento(String titulo, LocalDate dataLancamento);

    boolean existsByTituloIgnoreCaseAndDataLancamentoAndIdNot(
            String titulo,
            LocalDate dataLancamento,
            Long id);

    @Query("""
                SELECT DISTINCT f FROM Filme f
                LEFT JOIN f.generos g
                LEFT JOIN f.formatoFilme fmt
                WHERE f.ativo = true
                  AND (:#{#filtro.titulo} IS NULL OR LOWER(f.titulo) LIKE LOWER(CONCAT('%', :#{#filtro.titulo}, '%')))
                  AND (:#{#filtro.generoFilme} IS NULL OR g = :#{#filtro.generoFilme})
                  AND (:#{#filtro.diretor} IS NULL OR LOWER(f.diretor) LIKE LOWER(CONCAT('%', :#{#filtro.diretor}, '%')))
                  AND (:#{#filtro.distribuidora} IS NULL OR LOWER(f.distribuidora) LIKE LOWER(CONCAT('%', :#{#filtro.distribuidora}, '%')))
                  AND (:#{#filtro.dataLancamento} IS NULL OR f.dataLancamento = :#{#filtro.dataLancamento})
                  AND (:#{#filtro.classificacaoIndicativa} IS NULL OR f.classificacaoIndicativa = :#{#filtro.classificacaoIndicativa})
                  AND (:#{#filtro.formatoFilme} IS NULL OR fmt = :#{#filtro.formatoFilme})
            """)
    Page<Filme> buscarComFiltrosCliente(@Param("filtro") FilmeFiltroDTO filtro, Pageable pageable);

}
