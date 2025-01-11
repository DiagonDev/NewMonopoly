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

    @Modifying
    @Transactional
    @Query(value = """

        INSERT INTO Partita_Casella_PrezzoProprieta (idpartita, idcasella, idprezzoproprieta)
        SELECT
            p.codice_invito,
            c.id_casella,
            CASE
                WHEN c.tipo = 'Proprietà' THEN (
                    SELECT id_prezzoproprieta\s
                    FROM PrezzoProprieta pp
                    WHERE pp.id_prezzoproprieta NOT IN (
                        SELECT pc.idprezzoproprieta\s
                        FROM Partita_Casella_PrezzoProprieta pc\s
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
}
