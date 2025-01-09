package unimib.daBancherz.NewMonopoly.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.Imprevisto;

@Repository
public interface ImprevistoRepository extends JpaRepository<Imprevisto, Long> {
}
