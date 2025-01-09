package unimib.daBancherz.NewMonopoly.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.Partita;

// DAO per l'entità Partita
@Repository
public interface PartitaRepository extends JpaRepository<Partita, Long> {
    @Query("SELECT p FROM Partita p WHERE p.codice_invito = :codice_invito")
    Partita findByCodiceInvito(String codice_invito);

}
