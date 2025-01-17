package unimib.daBancherz.NewMonopoly.dataBase.Repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Partita_Probabilita;

@Repository
public interface PartitaProbabilitaRepository extends JpaRepository<Partita_Probabilita, Long> {
    //Metodo per trovare la descrizione di probabilita
    @Query(value = """
        select p.descrizione
        from partita_probabilita pp
        join probabilita p
        on id_probabilita=pp.idprobabilita
        where pp.idpartita= :idPartita
        and pp.utilizzato=false
        limit 1
    """, nativeQuery = true)
    Integer findDescrizioneProbabilita(@Param("idPartita") String idPartita);

    //metodo per settare a true l'utilizzo
    @Transactional
    @Modifying
    @Query("UPDATE Partita_Probabilita pp SET pp.utilizzato = true " +
            "WHERE pp.idpartita.codiceInvito = :idPartita " +
            "AND pp.idprobabilita.descrizione = :descrizione")
    void setUtilizzatoTrue(@Param("idPartita") Integer idPartita, @Param("descrizione") String descrizione);

}