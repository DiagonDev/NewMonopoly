package unimib.daBancherz.NewMonopoly.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.Imprevisto;

// DAO per l'entità Imprevisto
@Repository
public interface ImprevistoDAO extends JpaRepository<Imprevisto, Long> {
}
