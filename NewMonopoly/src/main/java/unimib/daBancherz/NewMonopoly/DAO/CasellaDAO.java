package unimib.daBancherz.NewMonopoly.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.Casella;

// DAO per l'entità Casella
@Repository
public interface CasellaDAO extends JpaRepository<Casella, Long> {
}
