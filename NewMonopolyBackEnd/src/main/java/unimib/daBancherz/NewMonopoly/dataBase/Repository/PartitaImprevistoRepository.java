package unimib.daBancherz.NewMonopoly.dataBase.Repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Partita_Imprevisto;

@Repository
public interface PartitaImprevistoRepository extends JpaRepository<Partita_Imprevisto, Long> {
    //Metodo per trovare la descrizione di imprevisto
    @Query(value = """
        SELECT i.descrizione
        FROM Partita_Imprevisto pii
        JOIN imprevisto i
        ON i.id_imprevisto = pii.idimprevisto
        WHERE pii.idpartita= :idPartita
        AND pii.utilizzato=false
        ORDER BY RANDOM()
        LIMIT 1
    """, nativeQuery = true)
    String findDescrizioneImprevsto(@Param("idPartita") String idPartita);

    //metodo per settare a true l'utilizzo
    @Transactional
    @Modifying
    @Query("UPDATE Partita_Imprevisto pii SET pii.utilizzato = true " +
            "WHERE pii.idpartita.codiceInvito = :idPartita " +
            "AND pii.idimprevisto.descrizione = :descrizione")
    void setUtilizzatoTrue(@Param("idPartita") String idPartita, @Param("descrizione") String descrizione);

    //metodo per settare a false l'utilizzo
    @Transactional
    @Modifying
    @Query("""
        UPDATE Partita_Imprevisto pii
        SET pii.utilizzato = CASE 
            WHEN pii.idgiocatore IS NOT NULL THEN true 
            ELSE false 
        END
        WHERE pii.idpartita.codiceInvito = :idPartita 
    """)
    void setUtilizzatoFalse(@Param("idPartita") String idPartita);

    @Transactional
    @Modifying
    @Query("UPDATE Partita_Imprevisto pii SET pii.idgiocatore = (" +
            "SELECT g.idGiocatore FROM Giocatore g " +
            "WHERE g.nome = :nomeGiocatore " +
            "AND g.idpartita.codiceInvito=:idPartita) " +
            "WHERE pii.idimprevisto.tipoAzione = :tipoAzione")
    void setGiocatore(@Param("idPartita") String idPartita, @Param("nomeGiocatore") String nomeGiocatore, @Param("tipoAzione") String tipoAzione);

    @Query("""
        SELECT COUNT(pp) > 0
        FROM Partita_Probabilita pp
        WHERE pp.idgiocatore.nome = :nomeGiocatore
        AND pp.idpartita.codiceInvito = :idPartita
    """)
    boolean possiedeCarta(@Param("nomeGiocatore") String nomeGiocatore, @Param("idPartita") String idPartita);
}
