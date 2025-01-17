package unimib.daBancherz.NewMonopoly.dataBase.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Imprevisto;

import java.util.List;

@Repository
public interface ImprevistoRepository extends JpaRepository<Imprevisto, Long> {
}
