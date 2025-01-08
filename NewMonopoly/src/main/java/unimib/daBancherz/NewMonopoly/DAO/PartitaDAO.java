package unimib.daBancherz.NewMonopoly.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.Partita;

// DAO per l'entità Partita
@Repository
public interface PartitaDAO extends JpaRepository<Partita, Long> {
}
