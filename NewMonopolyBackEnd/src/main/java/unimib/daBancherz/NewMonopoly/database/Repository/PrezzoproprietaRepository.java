package unimib.daBancherz.NewMonopoly.database.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.database.Entity.Prezzoproprieta;

// DAO per l'entità Prezzoproprieta
@Repository
public interface PrezzoproprietaRepository extends JpaRepository<Prezzoproprieta, Long> {
}
