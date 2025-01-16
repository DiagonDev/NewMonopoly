package unimib.daBancherz.NewMonopoly.dataBase.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Partita_Regolafedelta;

@Repository
public interface PartitaRegolafedeltaRepository extends JpaRepository<Partita_Regolafedelta, Long> {
}