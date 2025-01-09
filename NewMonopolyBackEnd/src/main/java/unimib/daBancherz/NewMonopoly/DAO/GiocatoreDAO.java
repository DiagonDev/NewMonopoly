package unimib.daBancherz.NewMonopoly.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.Giocatore;

// DAO per l'entità Giocatore
@Repository
public interface GiocatoreDAO extends JpaRepository<Giocatore, Long> {
}
