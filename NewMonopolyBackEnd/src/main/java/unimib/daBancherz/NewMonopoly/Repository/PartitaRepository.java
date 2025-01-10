package unimib.daBancherz.NewMonopoly.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.Partita;

@Repository
public interface PartitaRepository extends JpaRepository<Partita, Long> {
    Partita findByCodiceInvito(String codiceInvito);
}
