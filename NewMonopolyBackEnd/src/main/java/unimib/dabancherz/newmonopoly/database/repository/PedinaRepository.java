package unimib.dabancherz.newmonopoly.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unimib.dabancherz.newmonopoly.database.entity.Pedina;
import java.util.List;

// DAO per l'entità Pedina
@Repository
public interface PedinaRepository extends JpaRepository<Pedina, Long> {
    @Query(value = """
    SELECT p.id_pedina
    FROM pedina p 
    WHERE p.id_pedina NOT IN (
        SELECT g.idpedina 
        FROM giocatore g 
        WHERE g.idpartita = :idpartita
        AND g.idpedina IS NOT NULL
    )
""", nativeQuery = true)
    List<Integer> findUnusedPedineByPartita(@Param("idpartita") String idpartita);

}
