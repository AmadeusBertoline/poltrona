package poltrona.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import poltrona.entity.Filme;

public interface FilmeRepository extends JpaRepository<Filme, Long> {

    boolean existsByTituloIgnoreCase(String titulo);

}
