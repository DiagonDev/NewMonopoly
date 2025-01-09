package unimib.daBancherz.NewMonopoly.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.*;

// DAO per l'entità Partita_Regolafedelta
@Repository
public interface PartitaRegolafedeltaDAO extends JpaRepository<Partita_Regolafedelta, Long> {
}
