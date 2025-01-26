package unimib.dabancherz.newmonopoly.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unimib.dabancherz.newmonopoly.database.entity.Casella;

import java.util.List;
import java.util.Map;

@Repository
public interface CasellaRepository extends JpaRepository<Casella, Long> {
    @Query("SELECT c FROM Casella c")
    List<Casella> findAll();

    @Query("SELECT COUNT(c) FROM Casella c WHERE c.colore = :colore")
    int countByColore(@Param("colore") String colore);

    @Query(value = """
        SELECT c.id_casella FROM Casella c
        JOIN public.partita_casella_prezzoproprieta pcp on c.id_casella = pcp.idcasella
        WHERE pcp.idpartita = :idPartita
        ORDER BY pcp.posizione
    """, nativeQuery = true)
    int[] findByOrder(@Param("idPartita") String idPartita);
}