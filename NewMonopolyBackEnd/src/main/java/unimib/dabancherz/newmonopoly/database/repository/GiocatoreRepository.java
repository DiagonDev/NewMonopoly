package unimib.dabancherz.newmonopoly.database.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unimib.dabancherz.newmonopoly.database.entity.Giocatore;

import java.util.List;

@Repository
public interface GiocatoreRepository extends JpaRepository<Giocatore, Long> {
    boolean existsByNomeAndIdpartita_CodiceInvito(String nome, String codiceInvito);

    @Query(value = """
        SELECT g.* FROM giocatore g WHERE g.idpartita = :idPartita AND g.idpedina IS NOT NULL
    """, nativeQuery = true)
    List<Giocatore> findGiocatoreWithPedina (@Param("idPartita") String gameId);

    @Query("SELECT g.idGiocatore FROM Giocatore g WHERE g.nome = :nomeGiocatore AND g.idpartita.codiceInvito = :idPartita")
    Integer findIdByNomeAndPartitaCodiceInvito(@Param("nomeGiocatore") String nomeGiocatore, @Param("idPartita") String idPartita);

    void deleteByIdGiocatore(Integer idGiocatore);

    @Query("SELECT g.saldo FROM Giocatore g WHERE g.nome = :nomeGiocatore AND g.idpartita.codiceInvito = :idPartita")
    Integer saldoGiocatore(@Param("nomeGiocatore") String nomeGiocatore, @Param("idPartita") String idPartita);

    @Transactional
    @Modifying
    @Query("UPDATE Giocatore g SET g.idpedina.idPedina = :idpedina WHERE g.nome = :nome AND g.idpartita.codiceInvito = :idPartita")
    void updatePedinaForGiocatore(@Param("nome") String nome, @Param("idpedina") Integer idpedina, @Param("idPartita") String idPartita);

    @Query("SELECT g.idGiocatore FROM Giocatore g WHERE g.nome = :nomeGiocatore AND g.idpartita.codiceInvito = :idPartita")
    Integer findIdGiocatoreByNome(@Param("nomeGiocatore") String nomeGiocatore, @Param("idPartita") String idPartita);

    @Query("SELECT g.nome FROM Giocatore g WHERE g.idGiocatore = :idGiocatore")
    String findNomeByidGiocatore(@Param("idGiocatore") Integer idGiocatore);

    @Query("SELECT g.nome FROM Giocatore g WHERE g.idpartita.codiceInvito = :idPartita AND g.idGiocatore < :idGiocatore")
    List<String> findGiocatoriConIdMinore(@Param("idPartita") String idPartita, @Param("idGiocatore") Integer idGiocatore);

    @Query("SELECT g.nome FROM Giocatore g WHERE g.idpartita.codiceInvito = :idPartita")
    List<String> findGiocatori(@Param("idPartita") String idPartita);

    @Query("SELECT COUNT(g) FROM Giocatore g WHERE g.idpartita.codiceInvito = :idPartita")
    Integer countGiocatoriByPartita(@Param("idPartita") String idPartita);

    @Query("SELECT g.idpedina.idPedina FROM Giocatore g  WHERE g.nome=:playername AND g.idpartita.codiceInvito = :idPartita")
    Integer findPedinaFromGiocatore(@Param("playername") String playername, @Param("idPartita") String idPartita);

    //Aggiorna i soldi
    // Il via sarà negativo perchè quando passi dal via devi prendere i soldi
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE giocatore
            SET saldo = saldo - :soldi
                WHERE nome = :nomeGiocatore
                      AND idpartita= :idPartita
        """, nativeQuery = true)
    void setSaldoGiocatore(@Param("nomeGiocatore") String nomeGiocatore, @Param("idPartita") String idPartita, @Param("soldi") Integer soldi);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE giocatore
        SET saldo = saldo + :importo
        WHERE idpartita = :idPartita
        AND nome <> :nomeGiocatoreEscluso
    """, nativeQuery = true)
    void pagaImportoGiocatori(@Param("importo") Integer importo, @Param("idPartita") String idPartita, @Param("nomeGiocatoreEscluso") String nomeGiocatoreEscluso);

    @Query(value = """
        SELECT COUNT(*)
        FROM giocatore
        WHERE idpartita = :idPartita
    """, nativeQuery = true)
    Integer contaGiocatoriInPartita(@Param("idPartita") String idPartita);

}

