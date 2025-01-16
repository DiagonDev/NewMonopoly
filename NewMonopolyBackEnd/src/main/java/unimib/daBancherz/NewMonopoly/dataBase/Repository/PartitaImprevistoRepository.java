package unimib.daBancherz.NewMonopoly.dataBase.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Partita_Imprevisto;

@Repository
public interface PartitaImprevistoRepository extends JpaRepository<Partita_Imprevisto, Long> {

}
