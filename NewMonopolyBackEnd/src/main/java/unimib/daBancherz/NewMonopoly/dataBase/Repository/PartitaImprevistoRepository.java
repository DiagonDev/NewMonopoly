package unimib.daBancherz.NewMonopoly.dataBase.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Partita_Imprevisto;

@Repository
public interface PartitaImprevistoRepository extends JpaRepository<Partita_Imprevisto, Long>, BaseRepository {
    @Query(value = """
        SELECT i.descrizione
        FROM Partita_Imprevisto pii
        JOIN imprevisto i
        ON i.id_imprevisto = pii.idimprevisto
        WHERE pii.idpartita= :idPartita
        AND pii.utilizzato=false
        ORDER BY RANDOM()
        LIMIT 1
    """, nativeQuery = true)
    String findDescrizioneImprevsto(@Param("idPartita") String idPartita);
}
