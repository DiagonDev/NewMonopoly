package unimib.daBancherz.NewMonopoly.dataBase.Repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Partita_Probabilita;

@Repository
public interface PartitaProbabilitaRepository extends JpaRepository<Partita_Probabilita, Long>, BaseRepository {
    @Query(value = """
        SELECT p.descrizione
        FROM partita_probabilita pp
        JOIN probabilita p
        ON id_probabilita=pp.idprobabilita
        WHERE pp.idpartita= :idPartita
        AND pp.utilizzato=false
        ORDER BY RANDOM()
        LIMIT 1
    """, nativeQuery = true)
    String findDescrizioneProbabilita(@Param("idPartita") String idPartita);
}
