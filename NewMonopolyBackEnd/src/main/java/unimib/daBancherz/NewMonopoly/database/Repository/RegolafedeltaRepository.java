package unimib.daBancherz.NewMonopoly.database.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.database.Entity.Regolafedelta;


@Repository
public interface RegolafedeltaRepository extends JpaRepository<Regolafedelta, Long> {
    Regolafedelta findByDescrizione(String descrizione);
}
