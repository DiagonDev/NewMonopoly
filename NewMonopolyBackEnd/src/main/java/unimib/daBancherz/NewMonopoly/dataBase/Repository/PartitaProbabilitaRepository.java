package unimib.daBancherz.NewMonopoly.dataBase.Repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Partita_Probabilita;

@Repository
public interface PartitaProbabilitaRepository extends JpaRepository<Partita_Probabilita, Long> {
    //Metodo per trovare la descrizione di probabilita
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

    //metodo per settare a true l'utilizzo
    @Transactional
    @Modifying
    @Query("UPDATE Partita_Probabilita pp SET pp.utilizzato = true " +
            "WHERE pp.idpartita.codiceInvito = :idPartita " +
            "AND pp.idprobabilita.descrizione = :descrizione")
    void setUtilizzatoTrue(@Param("idPartita") String idPartita, @Param("descrizione") String descrizione);

    //metodo per settare a false l'utilizzo
    @Transactional
    @Modifying
    @Query("""
        UPDATE Partita_Probabilita pp
        SET pp.utilizzato = CASE
            WHEN pp.idgiocatore IS NOT NULL THEN true 
            ELSE false 
        END
        WHERE pp.idpartita.codiceInvito = :idPartita 
    """)
    void setUtilizzatoFalse(@Param("idPartita") String idPartita);

    @Transactional
    @Modifying
    @Query("UPDATE Partita_Probabilita pp SET pp.idgiocatore = (" +
            "SELECT g.idGiocatore FROM Giocatore g " +
            "WHERE g.nome = :nomeGiocatore " +
            "AND g.idpartita.codiceInvito=:idPartita) " +
            "WHERE pp.idprobabilita.tipoAzione = :tipoAzione")
    void setGiocatore(@Param("idPartita") String idPartita, @Param("nomeGiocatore") String nomeGiocatore, @Param("tipoAzione") String tipoAzione);
}