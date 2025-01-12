package unimib.daBancherz.NewMonopoly.Repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.Casella;
import unimib.daBancherz.NewMonopoly.Entity.Partita_Casella_Prezzoproprieta;

import java.util.List;

@Repository
public interface PartitaCasellaPrezzoproprietaRepository extends JpaRepository<Partita_Casella_Prezzoproprieta, Long> {

    // Metodo per ottenere le caselle di una partita ordinate per idcasella
    @Query("SELECT p.idcasella FROM Partita_Casella_Prezzoproprieta p WHERE p.idpartita.codiceInvito = :idPartita ORDER BY p.idcasella.idCasella ASC")
    List<Casella> findCaselleByPartita(String idPartita);


    // Metodo per popolare la tabella quando non c'è la randomizzazione
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO Partita_Casella_Prezzoproprieta (idpartita, idcasella, idprezzoproprieta)
        SELECT
            p.codice_invito,
            c.id_casella,
            CASE
                WHEN c.tipo = 'Proprietà' THEN c.id_casella
                ELSE NULL
            END AS idprezzoproprieta
            FROM
                Partita p
            JOIN
                Casella c
            ON
                1 = 1
            WHERE
                p.randomizzazione = false
                AND p.codice_invito = :idPartita
            """, nativeQuery = true)
    void populateWithRandomizationFalse(@Param("idPartita") String idPartita);


    // Metodo per popolare la tabella quando c'è la randomizzazione
    // TODO: controllo per far si che non vengano ripetuti gli idprezzoproprieta!
    @Modifying
    @Transactional
    @Query(value = """

        INSERT INTO Partita_Casella_PrezzoProprieta (idpartita, idcasella, idprezzoproprieta)
        SELECT
            p.codice_invito,
            c.id_casella,
            CASE
                WHEN c.tipo = 'Proprietà' THEN (
                    SELECT id_prezzoproprieta
                    FROM PrezzoProprieta pp
                    WHERE pp.id_prezzoproprieta NOT IN (
                        SELECT pc.idprezzoproprieta
                        FROM Partita_Casella_PrezzoProprieta pc
                        WHERE pc.idpartita = p.codice_invito
                    )
                    ORDER BY RANDOM()
                    LIMIT 1
                )
                ELSE NULL
            END AS idprezzoproprieta
        FROM
            Partita p
        JOIN
            Casella c ON TRUE
        WHERE
            p.randomizzazione = true
            AND p.codice_invito = :idPartita
        ORDER BY
            RANDOM();
        
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
            WHEN dati.prezzo IS NOT NULL AND dati.tipo <> 'Via' THEN dati.prezzo *
                CASE
                    WHEN dati.livello_difficolta = 'Facile' THEN 1
                    WHEN dati.livello_difficolta = 'Medio' THEN 1.05
                    WHEN dati.livello_difficolta = 'Difficile' THEN 1.10
                    ELSE 1
                END
            WHEN dati.tipo = 'Proprietà' THEN dati.costo_acquisto *
                CASE
                    WHEN dati.livello_difficolta = 'Facile' THEN 1
                    WHEN dati.livello_difficolta = 'Medio' THEN 1.05
                    WHEN dati.livello_difficolta = 'Difficile' THEN 1.10
                    ELSE 1
                END
            ELSE NULL
        END,
        prezzo_casa_corrente = CASE
            WHEN dati.tipo = 'Proprietà' THEN dati.casa *
                CASE
                    WHEN dati.livello_difficolta = 'Facile' THEN 1
                    WHEN dati.livello_difficolta = 'Medio' THEN 1.05
                    WHEN dati.livello_difficolta = 'Difficile' THEN 1.10
                    ELSE 1
                END
            ELSE NULL
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
        JOIN
            Prezzoproprieta pp ON pcp.idprezzoproprieta = pp.id_prezzoproprieta
        JOIN
            Partita pt ON pcp.idpartita = pt.codice_invito
        WHERE
            pt.codice_invito = :idPartita
    ) AS dati
    WHERE pcp.idcasella = dati.idcasella;
""", nativeQuery = true)
    void updatePrices(@Param("idPartita") String idPartita);



}
