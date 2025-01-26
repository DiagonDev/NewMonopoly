package unimib.dabancherz.newmonopoly.database.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unimib.dabancherz.newmonopoly.database.entity.Partita_Opportunita;

@SuppressWarnings("java:S1192") // Ignora la regola di duplicazione
@Repository
public interface PartitaOpportunitaRepository extends JpaRepository<Partita_Opportunita, Long> {
    //Metodo per trovare la descrizione
    @Query(value = """
        SELECT p.descrizione
        FROM partita_opportunita pp
        JOIN opportunita p
        ON id_opportunita=pp.idopportunita
        WHERE pp.idpartita= :idPartita
        AND pp.utilizzato=false
        AND p.tipo=:tipo
        ORDER BY RANDOM()
        LIMIT 1
    """, nativeQuery = true)
    String findDescrizione(@Param("idPartita") String idPartita, @Param("tipo") String tipo);

    //metodo per settare a true l'utilizzo
    @Transactional
    @Modifying
    @Query("UPDATE Partita_Opportunita pp SET pp.utilizzato = true " +
            "WHERE pp.idpartita.codiceInvito = :idPartita " +
            "AND pp.idopportunita.tipo = :tipo AND pp.idopportunita.descrizione = :descrizione")
    void setUtilizzatoTrue(@Param("idPartita") String idPartita, @Param("descrizione") String descrizione, @Param("tipo") String tipo);

    //metodo per settare a false l'utilizzo
    @Transactional
    @Modifying
    @Query("""
        UPDATE Partita_Opportunita pp
        SET pp.utilizzato = CASE
            WHEN pp.idgiocatore IS NOT NULL THEN true
            ELSE false
        END
        WHERE pp.idpartita.codiceInvito = :idPartita
        AND pp.idopportunita.tipo = :tipo
    """)
    void setUtilizzatoFalse(@Param("idPartita") String idPartita, @Param("tipo") String tipo);

    @Transactional
    @Modifying
    @Query("""
        UPDATE Partita_Opportunita pp SET pp.idgiocatore = (
                SELECT g.idGiocatore FROM Giocatore g
                WHERE g.nome = :nomeGiocatore
                AND g.idpartita.codiceInvito=:idPartita)
                WHERE pp.idopportunita.tipoAzione = :tipoAzione
                AND pp.idpartita.codiceInvito=:idPartita
                AND pp.idopportunita.tipo = :tipo
        """)
    void setGiocatore(@Param("idPartita") String idPartita, @Param("nomeGiocatore") String nomeGiocatore, @Param("tipoAzione") String tipoAzione, @Param("tipo") String tipo);

    @Query(value = """
        SELECT COUNT(pp) > 0
        FROM Partita_Opportunita pp
        JOIN Opportunita o ON pp.idopportunita = o.id_opportunita
        WHERE pp.idgiocatore= (SELECT id_Giocatore FROM Giocatore WHERE nome = :nomeGiocatore AND pp.idpartita = :idPartita)
        AND pp.idpartita = :idPartita
        AND o.tipo = :tipo
        LIMIT 1
    """, nativeQuery = true)
    boolean possiedeCarta(@Param("nomeGiocatore") String nomeGiocatore, @Param("idPartita") String idPartita, @Param("tipo") String tipo);
}