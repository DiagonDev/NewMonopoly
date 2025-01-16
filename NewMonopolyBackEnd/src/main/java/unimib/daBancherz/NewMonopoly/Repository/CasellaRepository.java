package unimib.daBancherz.NewMonopoly.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.Casella;

import java.util.List;

@Repository
public interface CasellaRepository extends JpaRepository<Casella, Long> {

}
