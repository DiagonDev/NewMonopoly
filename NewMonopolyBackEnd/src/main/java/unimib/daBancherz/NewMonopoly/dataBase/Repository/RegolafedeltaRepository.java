package unimib.daBancherz.NewMonopoly.dataBase.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Regolafedelta;

// DAO per l'entità Regolafedelta
@Repository
public interface RegolafedeltaRepository extends JpaRepository<Regolafedelta, Long> {
}
