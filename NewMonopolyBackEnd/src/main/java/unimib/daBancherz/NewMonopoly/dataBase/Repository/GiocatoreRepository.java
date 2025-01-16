package unimib.daBancherz.NewMonopoly.dataBase.Repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Giocatore;

import java.util.List;

@Repository
public interface GiocatoreRepository extends JpaRepository<Giocatore, Long> {
    boolean existsByNomeAndIdpartita_CodiceInvito(String nome, String codiceInvito);

    @Query("SELECT g.idGiocatore FROM Giocatore g WHERE g.nome = :nomeGiocatore AND g.idpartita.codiceInvito = :idPartita")
    Integer findIdByNomeAndPartitaCodiceInvito(@Param("nomeGiocatore") String nomeGiocatore, @Param("idPartita") String idPartita);

    void deleteByIdGiocatore(Integer idGiocatore);

    @Transactional
    @Modifying
    @Query("UPDATE Giocatore g SET g.idpedina.idPedina = :idpedina WHERE g.nome = :nome AND g.idpartita.codiceInvito = :idPartita")
    void updatePedinaForGiocatore(@Param("nome") String nome, @Param("idpedina") Integer idpedina, @Param("idPartita") String idPartita);

    @Query("SELECT g.idGiocatore FROM Giocatore g WHERE g.nome = :nomeGiocatore AND g.idpartita.codiceInvito = :idPartita")
    Integer findIdGiocatoreByNome(@Param("nomeGiocatore") String nomeGiocatore, @Param("idPartita") String idPartita);

    @Query("SELECT g.nome FROM Giocatore g WHERE g.idpartita.codiceInvito = :idPartita AND g.idGiocatore < :idGiocatore")
    List<String> findGiocatoriConIdMinore(@Param("idPartita") String idPartita, @Param("idGiocatore") Integer idGiocatore);

    @Query("SELECT COUNT(g) FROM Giocatore g WHERE g.idpartita.codiceInvito = :idPartita")
    long countGiocatoriByPartita(@Param("idPartita") String idPartita);

    @Query("SELECT g.idpedina.idPedina FROM Giocatore g  WHERE g.nome=:playername AND g.idpartita.codiceInvito = :idPartita")
    Integer findPedinaFromGiocatore(@Param("playername") String playername, @Param("idPartita") String idPartita);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE giocatore
            SET saldo = saldo - (
                SELECT
                    CASE
                        WHEN c.num_albergo = true THEN p.affitto_albergo
                        WHEN c.num_casa = 4 THEN p.affitto_4case
                        WHEN c.num_casa = 3 THEN p.affitto_3case
                        WHEN c.num_casa = 2 THEN p.affitto_2case
                        WHEN c.num_casa = 1 THEN p.affitto_1casa
                        ELSE p.affitto
                    END
                FROM partita_casella_prezzoproprieta pcp
                JOIN casella c ON pcp.idcasella = c.id_casella
                JOIN prezzoproprieta p ON pcp.idprezzoproprieta = p.id_prezzoproprieta
                JOIN giocatore g2 ON pcp.idgiocatore = g2.id_giocatore
                WHERE g2.nome = :nomeGiocatore
                AND pcp.posizione = :posizione
                AND pcp.idpartita = :idPartita
            )
            WHERE nome = :nomeGiocatore
    """, nativeQuery = true)
    void diminuisciSaldoGiocatore(@Param("playerName") String playerName, @Param("idPartita") String idPartita, @Param("posizione") Integer posizione);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE giocatore
            SET saldo = saldo + (
                SELECT
                    CASE
                        WHEN c.num_albergo = true THEN p.affitto_albergo
                        WHEN c.num_casa = 4 THEN p.affitto_4case
                        WHEN c.num_casa = 3 THEN p.affitto_3case
                        WHEN c.num_casa = 2 THEN p.affitto_2case
                        WHEN c.num_casa = 1 THEN p.affitto_1casa
                        ELSE p.affitto
                    END
                FROM partita_casella_prezzoproprieta pcp
                JOIN casella c ON pcp.idcasella = c.id_casella
                JOIN prezzoproprieta p ON pcp.idprezzoproprieta = p.id_prezzoproprieta
                JOIN giocatore g2 ON pcp.idgiocatore = g2.id_giocatore
                WHERE g2.nome = :nomeGiocatore
                AND pcp.posizione = :posizione
                AND pcp.idpartita = :idPartita
            )
            WHERE nome = :nomeGiocatore
    """, nativeQuery = true)
    int aumentoSaldoGiocatore(@Param("nomeGiocatore") String nomeGiocatore, @Param("idPartita") String idPartita, @Param("posizione") Integer posizione);


    //Aggiorna i soldi quando passi dal via
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE giocatore
        SET saldo = saldo + (
            SELECT pcp.prezzo_corrente
            FROM partita_casella_prezzoproprieta pcp
            JOIN giocatore g2 ON pcp.idgiocatore = g2.id_giocatore
            WHERE g2.nome = :nomeGiocatore
            AND pcp.posizione = :posizione
            AND pcp.idpartita = :idPartita
        )
        WHERE nome = :nomeGiocatore
    """, nativeQuery = true)
    int aumentoSaldoGiocatoreVia(@Param("nomeGiocatore") String nomeGiocatore, @Param("idPartita") String idPartita, @Param("posizione") Integer posizione);

}

