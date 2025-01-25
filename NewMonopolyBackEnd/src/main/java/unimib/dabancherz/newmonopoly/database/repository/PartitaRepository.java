package unimib.dabancherz.newmonopoly.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import unimib.dabancherz.newmonopoly.database.entity.Partita;

@Repository
public interface PartitaRepository extends JpaRepository<Partita, String> {
    Partita findByCodiceInvito(String codiceInvito);
    void deleteByCodiceInvito(String codiceInvito);

    @Query("SELECT p.codiceInvito FROM Partita p ORDER BY p.codiceInvito DESC LIMIT 1")
    String findLastCodiceInvito();

}
