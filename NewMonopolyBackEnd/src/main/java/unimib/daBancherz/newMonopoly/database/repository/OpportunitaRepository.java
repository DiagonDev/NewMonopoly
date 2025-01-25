package unimib.daBancherz.newMonopoly.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.newMonopoly.database.entity.Opportunita;

@Repository
public interface OpportunitaRepository extends JpaRepository<Opportunita, Long> {
    Opportunita findByDescrizioneAndTipo(String descrizione, String tipo);
}
