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
    // Metodo per popolare la tabella quando non c'è la randomizzazione
    @Modifying
    @Transactional
    @Query(value = """
    INSERT INTO Partita_Casella_Prezzoproprieta (idpartita, idcasella, idprezzoproprieta, posizione)
    SELECT
        p.codice_invito,
        c.id_casella,
        CASE
            WHEN c.tipo = 'Proprietà' THEN c.id_casella
            ELSE NULL
        END AS idprezzoproprieta,
        c.id_casella AS posizione
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
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO Partita_Casella_Prezzoproprieta (idpartita, idcasella, idprezzoproprieta, posizione)
        WITH caselle_non_proprieta AS (
            SELECT 
                c.id_casella AS id_casella,
                c.id_casella AS posizione,
                NULL::INTEGER AS idprezzoproprieta
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
            SELECT 
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
            WHEN dati.tipo = 'Proprietà' THEN dati.costo_acquisto *
                CASE
                    WHEN dati.livello_difficolta = 'Facile' THEN 1
                    WHEN dati.livello_difficolta = 'Medio' THEN 1.05
                    WHEN dati.livello_difficolta = 'Difficile' THEN 1.10
                    ELSE 1
                END
            WHEN dati.tipo = 'Via' THEN dati.prezzo
            WHEN dati.prezzo IS NOT NULL THEN dati.prezzo *
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
            ELSE pcp.prezzo_casa_corrente
        END
    FROM (
        SELECT\s
            pcp.idcasella,
            c.prezzo,
            c.tipo,
            pp.costo_acquisto,
            pp.casa,
            pt.livello_difficolta
        FROM\s
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
}
