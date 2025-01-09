package unimib.daBancherz.NewMonopoly.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.Probabilita;

// DAO per l'entità Probabilita
@Repository
public interface ProbabilitaRepository extends JpaRepository<Probabilita, Long> {
}
