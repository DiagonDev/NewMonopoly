package unimib.daBancherz.NewMonopoly.dataBase.Repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Partita_Regolafedelta;

@Repository
public interface PartitaRegolafedeltaRepository extends JpaRepository<Partita_Regolafedelta, Long> {
    //Metodo per trovare la descrizione della regola
    @Query(value = """
        SELECT r.descrizione
        FROM partita_regolafedelta pr
        JOIN regolafedelta r
        ON r.id_regolafedelta = pr.idregolafedelta
        WHERE pr.idpartita= :idPartita
        AND pr.utilizzato=false
        ORDER BY RANDOM()
        LIMIT 1
    """, nativeQuery = true)
    String findDescrizioneRegola(@Param("idPartita") String idPartita);

    //metodo per settare a true l'utilizzo
    @Transactional
    @Modifying
    @Query("UPDATE Partita_Regolafedelta pr SET pr.utilizzato = true " +
            "WHERE pr.idpartita.codiceInvito = :idPartita " +
            "AND pr.idregolafedelta.descrizione = :descrizione")
    void setUtilizzatoTrue(@Param("idPartita") String idPartita, @Param("descrizione") String descrizione);

    //metodo per settare a false l'utilizzo
    @Transactional
    @Modifying
    @Query("""
        UPDATE Partita_Regolafedelta pii
        SET pii.utilizzato =  false
        WHERE pii.idpartita.codiceInvito = :idPartita 
    """)
    void setUtilizzatoFalse(@Param("idPartita") String idPartita);

}