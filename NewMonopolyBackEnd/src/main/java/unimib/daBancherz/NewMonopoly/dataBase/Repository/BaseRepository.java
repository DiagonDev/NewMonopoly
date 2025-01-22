package unimib.daBancherz.NewMonopoly.dataBase.Repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

public interface BaseRepository {
    @Transactional
    @Modifying
    void setUtilizzatoTrue(@Param("idPartita") String idPartita, @Param("descrizione") String descrizione);

    @Transactional
    @Modifying
    void setUtilizzatoFalse(@Param("idPartita") String idPartita);

    @Transactional
    @Modifying
    void setGiocatore(@Param("idPartita") String idPartita, @Param("nomeGiocatore") String nomeGiocatore, @Param("tipoAzione") String tipoAzione);

    boolean possiedeCarta(@Param("nomeGiocatore") String nomeGiocatore, @Param("idPartita") String idPartita);
}
