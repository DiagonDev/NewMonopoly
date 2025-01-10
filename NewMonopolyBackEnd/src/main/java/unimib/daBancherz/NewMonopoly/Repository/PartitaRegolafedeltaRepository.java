package unimib.daBancherz.NewMonopoly.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.*;

@Repository
public interface PartitaRegolafedeltaRepository extends JpaRepository<Partita_Regolafedelta, Long> {
}