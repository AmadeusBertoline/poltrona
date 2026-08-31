package poltrona.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import poltrona.entity.Filme;

public interface FilmeRepository extends JpaRepository<Filme, Long> {

    boolean existsByTituloIgnoreCaseAndDataLancamento(String titulo, LocalDate dataLancamento);

    boolean existsByTituloIgnoreCaseAndDataLancamentoAndIdNot(
            String titulo,
            LocalDate dataLancamento,
            Long id);

}
