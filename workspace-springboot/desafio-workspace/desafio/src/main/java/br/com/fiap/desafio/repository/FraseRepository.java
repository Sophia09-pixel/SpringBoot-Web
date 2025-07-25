package br.com.fiap.desafio.repository;

import br.com.fiap.desafio.model.Frase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FraseRepository extends JpaRepository<Frase, Long> {
    Optional<Frase> findById(int id);

    @Query("SELECT f FROM Frase f order by function('RANDOM') LIMIT 1")
    Frase buscaFraseAleatoria();
}
