package unimib.daBancherz.NewMonopoly.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.Prezzoproprieta;

// DAO per l'entità Prezzoproprieta
@Repository
public interface PrezzoproprietaDAO extends JpaRepository<Prezzoproprieta, Long> {
}
