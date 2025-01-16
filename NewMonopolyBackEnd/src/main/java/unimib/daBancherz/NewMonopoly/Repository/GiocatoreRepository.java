package unimib.daBancherz.NewMonopoly.Repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.Entity.Giocatore;
import unimib.daBancherz.NewMonopoly.Entity.Partita;

import java.util.List;

@Repository
public interface GiocatoreRepository extends JpaRepository<Giocatore, Long> {
    boolean existsByNomeAndIdpartita_CodiceInvito(String nome, String codiceInvito);

    @Query("SELECT g.idGiocatore FROM Giocatore g WHERE g.nome = :nomeGiocatore AND g.idpartita.codiceInvito = :gameId")
    Integer findIdByNomeAndPartitaCodiceInvito(@Param("nomeGiocatore") String nomeGiocatore, @Param("gameId") String gameId);

    void deleteByIdGiocatore(Integer idGiocatore);

    @Transactional
    @Modifying
    @Query("UPDATE Giocatore g SET g.idpedina.idPedina = :idpedina WHERE g.nome = :nome AND g.idpartita.codiceInvito = :idpartita")
    void updatePedinaForGiocatore(@Param("nome") String nome, @Param("idpedina") Integer idpedina, @Param("idpartita") String idpartita);

    @Query("SELECT g.idGiocatore FROM Giocatore g WHERE g.nome = :nomeGiocatore AND g.idpartita.codiceInvito = :codiceInvito")
    Integer findIdGiocatoreByNome(@Param("nomeGiocatore") String nomeGiocatore, @Param("codiceInvito") String codiceInvito);

    @Query("SELECT g.nome FROM Giocatore g WHERE g.idpartita.codiceInvito = :codiceInvito AND g.idGiocatore < :idGiocatore")
    List<String> findGiocatoriConIdMinore(@Param("codiceInvito") String codiceInvito, @Param("idGiocatore") Integer idGiocatore);

    @Query("SELECT COUNT(g) FROM Giocatore g WHERE g.idpartita.codiceInvito = :codiceInvito")
    long countGiocatoriByPartita(@Param("codiceInvito") String codiceInvito);

    @Query("SELECT g.idpedina.idPedina FROM Giocatore g  WHERE g.nome=:playername AND g.idpartita.codiceInvito = :idpartita")
    Integer findPedinaFromGiocatore(@Param("playername") String playername, @Param("idpedina") String idpedina);

}

