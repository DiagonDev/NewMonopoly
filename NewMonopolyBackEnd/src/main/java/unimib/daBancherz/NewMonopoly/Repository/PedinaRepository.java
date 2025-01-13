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
    @Query("SELECT p.idPedina FROM Pedina p WHERE p.idPedina NOT IN (" +
            "SELECT g.idpedina.idPedina FROM Giocatore g WHERE g.idpartita.codiceInvito = :idpartita)")
    List<Integer> findUnusedPedineByPartita(@Param("idpartita") String idpartita);
}
