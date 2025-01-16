package unimib.daBancherz.NewMonopoly.dataBase.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Probabilita;

import java.util.List;

@Repository
public interface ProbabilitaRepository extends JpaRepository<Probabilita, Long> {
    List<Probabilita> findAll();
}
