package unimib.daBancherz.NewMonopoly.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.Pedina;

// DAO per l'entità Pedina
@Repository
public interface PedinaDAO extends JpaRepository<Pedina, Long> {
}
