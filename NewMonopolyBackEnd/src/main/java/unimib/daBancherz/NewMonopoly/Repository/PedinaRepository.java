package unimib.daBancherz.NewMonopoly.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.Pedina;

// DAO per l'entità Pedina
@Repository
public interface PedinaRepository extends JpaRepository<Pedina, Long> {
}
