package unimib.daBancherz.NewMonopoly.Repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.Casella;
import unimib.daBancherz.NewMonopoly.Entity.Partita_Casella_Prezzoproprieta;
import unimib.daBancherz.NewMonopoly.Entity.Partita_Imprevisto;

import java.util.List;

@Repository
public interface PartitaImprevistoRepository extends JpaRepository<Partita_Imprevisto, Long> {

}
