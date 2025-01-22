package unimib.daBancherz.NewMonopoly.dataBase.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Probabilita;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Regolafedelta;


@Repository
public interface RegolafedeltaRepository extends JpaRepository<Regolafedelta, Long> {
    Regolafedelta findByDescrizione(String descrizione);
}
