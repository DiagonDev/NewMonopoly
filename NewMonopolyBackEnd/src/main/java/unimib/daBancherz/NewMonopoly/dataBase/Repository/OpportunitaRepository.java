package unimib.daBancherz.NewMonopoly.dataBase.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Opportunita;

@Repository
public interface OpportunitaRepository extends JpaRepository<Opportunita, Long> {
    Opportunita findByDescrizioneAndTipo(String descrizione, String tipo);
}
