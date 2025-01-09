package unimib.daBancherz.NewMonopoly.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.Partita_Casella_Prezzoproprieta;

// DAO per l'entità Partita_Casella_Prezzoproprieta
@Repository
public interface PartitaCasellaPrezzoproprietaDAO extends JpaRepository<Partita_Casella_Prezzoproprieta, Long> {
}
