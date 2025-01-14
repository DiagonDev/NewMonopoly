package unimib.daBancherz.NewMonopoly.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.Pedina;

import java.util.List;

// DAO per l'entità Pedina
@Repository
public interface PedinaRepository extends JpaRepository<Pedina, Long> {
    @Query(value = """
        SELECT p.id_pedina FROM Pedina p WHERE p.id_pedina NOT IN (
        SELECT g.idpedina FROM Giocatore g WHERE g.idpartita = :idpartita)
    """, nativeQuery = true)List<Integer> findUnusedPedineByPartita(@Param("idpartita") String idpartita);
}
