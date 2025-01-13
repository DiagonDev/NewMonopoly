package unimib.daBancherz.NewMonopoly.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.Partita;

@Repository
public interface PartitaRepository extends JpaRepository<Partita, String> {
    Partita findByCodiceInvito(String codiceInvito);
    void deleteByCodiceInvito(String codiceInvito);

    @Query("SELECT p.codiceInvito FROM Partita p ORDER BY p.codiceInvito DESC")
    String findLastCodiceInvito();

}
