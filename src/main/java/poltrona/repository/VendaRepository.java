package poltrona.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poltrona.entity.Cliente;
import poltrona.entity.Venda;
import jakarta.persistence.LockModeType;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    @EntityGraph(attributePaths = {
            "cliente", "itens", "itens.ingresso", "itens.ingresso.usuario", "itens.ingresso.sessao",
            "itens.ingresso.sessao.filme", "itens.ingresso.sessao.sala.cinema",
            "itens.ingresso.poltrona.sala.cinema", "itens.produto"
    })
    Page<Venda> findByCliente(Cliente cliente, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {
            "cliente", "itens", "itens.ingresso", "itens.ingresso.usuario", "itens.ingresso.sessao",
            "itens.ingresso.sessao.filme", "itens.ingresso.sessao.sala.cinema",
            "itens.ingresso.poltrona.sala.cinema", "itens.produto"
    })
    Optional<Venda> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Venda v WHERE v.id = :id")
    Optional<Venda> findByIdWithLock(@Param("id") Long id);

    @EntityGraph(attributePaths = {
            "cliente", "itens", "itens.ingresso", "itens.ingresso.usuario", "itens.ingresso.sessao",
            "itens.ingresso.sessao.filme", "itens.ingresso.sessao.sala.cinema",
            "itens.ingresso.poltrona.sala.cinema", "itens.produto"
    })
    @Query("""
                SELECT DISTINCT v FROM Venda v
                LEFT JOIN v.itens item
                LEFT JOIN item.ingresso ingresso
                LEFT JOIN ingresso.sessao sessao
                LEFT JOIN sessao.sala sala
                WHERE (:clienteId IS NULL OR v.cliente.id = :clienteId)
                AND (:cinemaId IS NULL OR sala.cinema.id = :cinemaId)
            """)
    Page<Venda> buscarVendas(@Param("clienteId") Long clienteId, @Param("cinemaId") Long cinemaId,
            Pageable pageable);

    @EntityGraph(attributePaths = {
            "cliente", "itens", "itens.ingresso", "itens.ingresso.usuario", "itens.ingresso.sessao",
            "itens.ingresso.sessao.filme", "itens.ingresso.sessao.sala.cinema",
            "itens.ingresso.poltrona.sala.cinema", "itens.produto"
    })
    @Query("""
                SELECT DISTINCT v FROM Venda v
                JOIN v.itens item JOIN item.ingresso ingresso
                WHERE ingresso.sessao.sala.cinema.id = :cinemaId
            """)
    Page<Venda> findAllByCinemaId(@Param("cinemaId") Long cinemaId, Pageable pageable);

    @EntityGraph(attributePaths = {
            "cliente", "itens", "itens.ingresso", "itens.ingresso.usuario", "itens.ingresso.sessao",
            "itens.ingresso.sessao.filme", "itens.ingresso.sessao.sala",
            "itens.ingresso.poltrona", "itens.ingresso.poltrona.sala.cinema", "itens.produto"
    })
    @Query("""
                SELECT DISTINCT v FROM Venda v
                JOIN v.itens item JOIN item.ingresso ingresso
                WHERE ingresso.sessao.sala.cinema.proprietario.id = :proprietarioId
                AND (:clienteId IS NULL OR v.cliente.id = :clienteId)
            """)
    Page<Venda> findAllByProprietarioId(@Param("proprietarioId") Long proprietarioId,
            @Param("clienteId") Long clienteId, Pageable pageable);
}
