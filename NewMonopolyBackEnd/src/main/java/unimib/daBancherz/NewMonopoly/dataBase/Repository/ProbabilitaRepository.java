package unimib.daBancherz.NewMonopoly.dataBase.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Probabilita;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProbabilitaRepository extends JpaRepository<Probabilita, Long> {
    Probabilita findByDescrizione(String descrizione);
}
