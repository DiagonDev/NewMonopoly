package unimib.daBancherz.NewMonopoly.dataBase.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Partita_Probabilita;

@Repository
public interface PartitaProbabilitaRepository extends JpaRepository<Partita_Probabilita, Long> {
}