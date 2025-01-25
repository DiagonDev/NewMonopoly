package unimib.daBancherz.newMonopoly.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.newMonopoly.database.entity.Regolafedelta;


@Repository
public interface RegolafedeltaRepository extends JpaRepository<Regolafedelta, Long> {
    Regolafedelta findByDescrizione(String descrizione);
}
