package unimib.daBancherz.NewMonopoly.dataBase.Repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Partita_Casella_Prezzoproprieta;
import unimib.daBancherz.NewMonopoly.model.PlayerProperties;

import java.util.List;

@Repository
public interface PartitaCasellaPrezzoproprietaRepository extends JpaRepository<Partita_Casella_Prezzoproprieta, Long> {
    @Query("""
        SELECT new unimib.daBancherz.NewMonopoly.model.PlayerProperties(
            pcp.prezzoCorrente,
            pcp.idgiocatore.idGiocatore,
            pcp.numCasa,
            pcp.prezzoCasaCorrente,
            c.nome,
            c.colore,
            p.affitto,
            p.affitto1Casa,
            p.affitto2Case,
            p.affitto3Case,
            p.affitto4Case,
            p.affittoAlbergo,
            p.ipoteca
        )
        FROM Partita_Casella_Prezzoproprieta pcp
        JOIN pcp.idcasella c
        JOIN pcp.idprezzoproprieta p
        WHERE pcp.idpartita.codiceInvito = :idPartita
        AND pcp.idgiocatore.nome = :nomeGiocatore
    """)
    List<PlayerProperties> findPlayerProperties(@Param("idPartita") String idPartita, @Param("nomeGiocatore") String nomeGiocatore);

    @Query("""
        SELECT new unimib.daBancherz.NewMonopoly.model.PlayerProperties(
            pcp.prezzoCorrente,
            pcp.idgiocatore.idGiocatore,
            pcp.numCasa,
            pcp.prezzoCasaCorrente,
            c.nome,
            c.colore,
            p.affitto,
            p.affitto1Casa,
            p.affitto2Case,
            p.affitto3Case,
            p.affitto4Case,
            p.affittoAlbergo,
            p.ipoteca
        )
        FROM Partita_Casella_Prezzoproprieta pcp
        JOIN pcp.idcasella c
        JOIN pcp.idprezzoproprieta p
        WHERE pcp.idpartita.codiceInvito = :idPartita
        AND pcp.idgiocatore.nome <> :nomeGiocatore
        AND pcp.idgiocatore.nome IS NOT NULL
    """)
    List<PlayerProperties> findOtherPlayerProperties(@Param("idPartita") String idPartita, @Param("nomeGiocatore") String nomeGiocatore);

    // Metodo per popolare la tabella quando non c'è la randomizzazione
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO Partita_Casella_Prezzoproprieta (idpartita, idcasella, idprezzoproprieta, posizione)
        SELECT
            :idPartita AS idpartita,
            c.id_casella AS idcasella,
            CASE
                WHEN c.tipo IN ('Proprietà', 'Stazione', 'Società') THEN c.id_casella
                ELSE NULL
            END AS idprezzoproprieta,
            c.id_casella AS posizione
        FROM Casella c
        JOIN Partita p
            ON p.codice_invito = :idPartita
        WHERE p.randomizzazione = false
    """, nativeQuery = true)
    void populateWithRandomizationFalse(@Param("idPartita") String idPartita);


    // Metodo per popolare la tabella quando c'è la randomizzazione
    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO Partita_Casella_Prezzoproprieta (idpartita, idcasella, idprezzoproprieta, posizione)
            WITH caselle_non_proprieta AS (
                SELECT
                    c.id_casella AS id_casella,
                    c.id_casella AS posizione,
                    CASE
                        WHEN c.tipo IN ('Società', 'Stazione') THEN c.id_casella
                        ELSE NULL
                    END AS idprezzoproprieta
                FROM
                    Casella c
                WHERE
                    c.tipo <> 'Proprietà'
            ),
            numeri_occupati AS (
                SELECT DISTINCT c.id_casella AS posizione
                FROM Casella c
                WHERE c.tipo <> 'Proprietà'
            ),
            numeri_disponibili AS (
                SELECT
                    n.num AS posizione
                FROM
                    generate_series(1, 40) AS n(num)
                WHERE
                    n.num NOT IN (SELECT posizione FROM numeri_occupati)
            ),
            caselle_proprieta_random AS (
                SELECT
                    c.id_casella AS id_casella,
                    ROW_NUMBER() OVER (ORDER BY RANDOM()) AS rnd_posizione
                FROM
                    Casella c
                WHERE
                    c.tipo = 'Proprietà'
            ),
            posizioni_random AS (
                SELECT
                    nd.posizione,
                    ROW_NUMBER() OVER (ORDER BY RANDOM()) AS rnd_numero
                FROM
                    numeri_disponibili nd
            ),
            prezzi_random AS (
                SELECT
                    pp.id_prezzoproprieta,
                    ROW_NUMBER() OVER (ORDER BY RANDOM()) AS rnd_prezzoproprieta
                FROM
                    Prezzoproprieta pp
            ),
            caselle_proprieta AS (
                SELECT
                    cpr.id_casella,
                    pr.posizione,
                    pp.id_prezzoproprieta
                FROM
                    caselle_proprieta_random cpr
                JOIN
                    posizioni_random pr
                ON
                    cpr.rnd_posizione = pr.rnd_numero
                JOIN
                    prezzi_random pp
                ON
                    cpr.rnd_posizione = pp.rnd_prezzoproprieta
            ),
            tutte_caselle AS (
                SELECT
                    cnp.id_casella,
                    cnp.posizione,
                    cnp.idprezzoproprieta
                FROM
                    caselle_non_proprieta cnp
                UNION ALL
                SELECT\s
                    cp.id_casella,
                    cp.posizione,
                    cp.id_prezzoproprieta
                FROM
                    caselle_proprieta cp
            )
            SELECT
                :idPartita AS idpartita,
                tc.id_casella,
                tc.idprezzoproprieta,
                tc.posizione
            FROM
                tutte_caselle tc
        """, nativeQuery = true)
    void populateWithRandomizationTrue(@Param("idPartita") String idPartita);

    /* Metodo per popolare il costo dell'acquisto e della casa  in base al livello di difficolta:
       Se è facile i prezzi rimangono uguali
       Se è medio i prezzi aumentano del 5%
       Se è difficile i prezzi aumentano del 10%
     */
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE Partita_Casella_Prezzoproprieta AS pcp
            SET
                prezzo_corrente = CASE
                    WHEN dati.tipo IN ('Proprietà', 'Stazione', 'Società') THEN dati.costo_acquisto *
                        CASE
                            WHEN dati.livello_difficolta = 'Medio' THEN 1.05
                            WHEN dati.livello_difficolta = 'Difficile' THEN 1.10
                            ELSE 1
                        END
                    WHEN dati.tipo = 'Via' THEN dati.prezzo
                    ELSE NULL
                END,
                prezzo_casa_corrente = CASE
                    WHEN dati.tipo = 'Proprietà' THEN dati.casa *
                        CASE
                            WHEN dati.livello_difficolta = 'Medio' THEN 1.05
                            WHEN dati.livello_difficolta = 'Difficile' THEN 1.10
                            ELSE 1
                        END
                    ELSE pcp.prezzo_casa_corrente
                END
            FROM (
                SELECT
                    pcp.idcasella,
                    c.prezzo,
                    c.tipo,
                    pp.costo_acquisto,
                    pp.casa,
                    pt.livello_difficolta
                FROM
                    Partita_Casella_Prezzoproprieta pcp
                JOIN
                    Casella c ON pcp.idcasella = c.id_casella
                LEFT JOIN
                    Prezzoproprieta pp ON pcp.idprezzoproprieta = pp.id_prezzoproprieta
                JOIN
                    Partita pt ON pcp.idpartita = pt.codice_invito
                WHERE
                    pt.codice_invito = :idPartita
            ) AS dati
            WHERE pcp.idcasella = dati.idcasella;
    """, nativeQuery = true)
    void updatePrices(@Param("idPartita") String idPartita);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE Partita_Casella_Prezzoproprieta pcp
        SET prezzo_corrente =  :costo
        WHERE pcp.posizione = :posizione
        AND pcp.idpartita = :gameId
    """, nativeQuery = true)
    void setPrezzoCorrente(@Param("costo") Integer costo, @Param("gameId") String gameId, @Param("posizione") Integer posizione);


    // Metodo per capire di che tipo è la casella
    @Query("SELECT p.idcasella.tipo FROM Partita_Casella_Prezzoproprieta p WHERE p.posizione = :posizione AND p.idpartita.codiceInvito = :idPartita")
    String findTipoByPosizione(@Param("posizione") Integer posizione, @Param("idPartita") String idPartita);

    // Metodo per capire se la casella è appartenente a qualcuno
    @Query("SELECT g.nome FROM Partita_Casella_Prezzoproprieta pcp JOIN pcp.idgiocatore g WHERE pcp.posizione = :position AND pcp.idpartita.codiceInvito = :gameId")
    String findNomeGiocatoreByPosizioneAndGameId(@Param("position") Integer position, @Param("gameId") String gameId);

    @Query("SELECT c.nome FROM Partita_Casella_Prezzoproprieta pcp JOIN pcp.idcasella c WHERE pcp.posizione = :position AND pcp.idpartita.codiceInvito = :gameId")
    String findNomeCasellaByPosizioneAndGameId(@Param("position") Integer position, @Param("gameId") String gameId);

    @Query("SELECT pcp.posizione FROM Partita_Casella_Prezzoproprieta pcp JOIN pcp.idcasella c WHERE c.nome = :nome AND pcp.idpartita.codiceInvito = :gameId")
    Integer findPosizioneByNomeCasellaAndIdpartita(@Param("nome") String nome, @Param("gameId") String gameId);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE Partita_Casella_Prezzoproprieta pcp
        SET idgiocatore =  (SELECT g.id_giocatore FROM Giocatore g WHERE g.nome = :playerName AND g.idpartita = :gameId)
        WHERE pcp.idcasella = (SELECT c.id_casella FROM Casella c WHERE c.nome = :nomeCasella)
        AND pcp.idpartita = :gameId
    """, nativeQuery = true)
    void setGiocatore(@Param("playerName") String playerName, @Param("gameId") String gameId, @Param("nomeCasella") String nomeCasella);

    // Metodo per trovare il costo di acquisto di una proprieta
    @Query(value = """
        SELECT pcp.prezzo_corrente
        FROM partita_casella_prezzoproprieta pcp
        WHERE pcp.posizione = :posizione
          AND pcp.idpartita = (SELECT p.codice_invito FROM partita p WHERE p.codice_invito = :idPartita)
    """, nativeQuery = true)
    Integer prezzoCasella(@Param("posizione") Integer posizione, @Param("idPartita") String idPartita);

    // Metodo per trovare il costo di acquisto di una proprieta
    @Query(value = """
        SELECT pcp.prezzo_corrente
        FROM partita_casella_prezzoproprieta pcp
        WHERE pcp.idcasella = (SELECT c.id_casella FROM  casella c WHERE c.nome = :nomeCasella)
        AND pcp.idpartita = (SELECT p.codice_invito FROM partita p WHERE p.codice_invito = :idPartita)
    """, nativeQuery = true)
    Integer prezzoCasella2(@Param("nomeCasella") String nomeCasella, @Param("idPartita") String idPartita);

    // Metodo per trovare il costo di acquisto di una casa/albergo di una proprieta
    @Query(value = """
        SELECT pcp.prezzo_casa_corrente
        FROM partita_casella_prezzoproprieta pcp
        WHERE pcp.posizione = :posizione
        AND pcp.idpartita = (SELECT p.codice_invito FROM partita p WHERE p.codice_invito = :idPartita)
    """, nativeQuery = true)
    Integer prezzoCasa(@Param("posizione") Integer posizione, @Param("idPartita") String idPartita);

    //Seleziona il costo dell'affitto
    @Query(value = """
        SELECT
            CASE
                -- Caso Proprietà
                WHEN c.tipo = 'Proprietà' THEN
                    CASE
                        WHEN pcp.num_casa = 5 THEN p.affitto_albergo
                        WHEN pcp.num_casa = 4 THEN p.affitto_4case
                        WHEN pcp.num_casa = 3 THEN p.affitto_3case
                        WHEN pcp.num_casa = 2 THEN p.affitto_2case
                        WHEN pcp.num_casa = 1 THEN p.affitto_1casa
                        ELSE p.affitto
                    END
                -- Caso Stazione
                WHEN c.tipo = 'Stazione' THEN
                    CASE
                        WHEN :count = 1 THEN p.affitto
                        WHEN :count = 2 THEN p.affitto_1casa
                        WHEN :count = 3 THEN p.affitto_2case
                        WHEN :count = 4 THEN p.affitto_3case
                    END
                -- Caso Società
                WHEN c.tipo = 'Società' THEN
                    CASE
                        WHEN :count = 1 THEN p.affitto
                        WHEN :count = 2 THEN p.affitto_1casa
                    END
            END AS affitto
        FROM partita_casella_prezzoproprieta pcp
        JOIN prezzoproprieta p ON pcp.idprezzoproprieta = p.id_prezzoproprieta
        JOIN casella c ON pcp.idcasella = c.id_casella
        WHERE pcp.posizione = :posizione
        AND pcp.idpartita = :idPartita
    """, nativeQuery = true)
    int calcolaAffitto(@Param("idPartita") String idPartita, @Param("posizione") Integer posizione, @Param("idGiocatore") Integer idGiocatore, @Param("count") Integer count);


    //Metodo per contare quante caselle di quel tipo ha l'utente
    @Query(value =  """
        SELECT COUNT(*) AS numero_caselle
        FROM Partita_Casella_Prezzoproprieta pc
        JOIN Giocatore g ON pc.idgiocatore = g.id_giocatore
        JOIN Casella c ON pc.idcasella = c.id_casella
        JOIN Partita p ON pc.idpartita = p.codice_invito
        WHERE g.nome = :nomeGiocatore
          AND c.tipo = :casellaTipo
          AND p.codice_invito = :idPartita
    """, nativeQuery = true)
    int countProprieta(@Param("nomeGiocatore") String nomeGiocatore, @Param("casellaTipo") String casellaTipo, @Param("idPartita") String idPartita);

    @Query(value = """
        SELECT COALESCE(SUM(pcp.num_casa), 0) AS total_casa
        FROM giocatore g
        LEFT JOIN partita_casella_prezzoproprieta pcp
        ON pcp.idgiocatore = g.id_giocatore
        AND pcp.idpartita = :idPartita
        WHERE g.nome = :nomeGiocatore
        AND num_casa<>5
    """, nativeQuery = true)
    int contaCaseTot(@Param("nomeGiocatore") String nomeGiocatore, @Param("idPartita") String idPartita);

    @Query(value = """
        SELECT pcp.num_casa
        FROM partita_casella_prezzoproprieta pcp
        WHERE pcp.posizione = :posizione
        AND num_casa<>5
    """, nativeQuery = true)
    int contaCase(@Param("posizione") Integer posizione);

    @Query(value = """
        SELECT COUNT(*) 
        FROM partita_casella_prezzoproprieta  pcp
        JOIN giocatore g ON pcp.idgiocatore = g.id_giocatore
        WHERE g.nome= :nomeGiocatore
        AND pcp.num_casa=5
        AND pcp.idpartita = :idPartita
    """, nativeQuery = true)
    int contaAlberghiTot(@Param("nomeGiocatore") String nomeGiocatore, @Param("idPartita") String idPartita);

    @Query(value = """
        SELECT CASE
            WHEN COUNT(*) > 0 THEN true
            ELSE false
        END
        FROM partita_casella_prezzoproprieta pcp
        WHERE pcp.posizione = :posizione
        AND pcp.num_casa = 5
    """, nativeQuery = true)
    boolean contaAlbergo(@Param("posizione") Integer posizione);


    //Metodo per trovare c.tipo piu vicino alla posizione dove ci si trova
    @Query(value = """
        SELECT p.posizione
        FROM Partita_Casella_Prezzoproprieta p
        JOIN Casella c ON p.idcasella = c.id_casella
        WHERE c.tipo = :tipoCasella
        AND p.posizione > :posizioneCorrente
        AND p.idpartita = :idPartita
        ORDER BY p.posizione ASC
        LIMIT 1
    """, nativeQuery = true)
    Integer findNextCasellaByTipo(@Param("tipoCasella") String tipoCasella, @Param("posizioneCorrente") Integer posizioneCorrente, @Param("idPartita") String idPartita);

    @Modifying
    @Query(value = """
        UPDATE partita_casella_prezzoproprieta pcp
            SET num_casa = num_casa + :numeroCase
            WHERE pcp.idcasella IN (
                SELECT c.id_casella
                FROM casella c
                WHERE c.colore = :colore
            )
    """, nativeQuery = true)
    void aggiungiCase(@Param("colore") String colore, @Param("numeroCase") Integer numeroCase);

}
