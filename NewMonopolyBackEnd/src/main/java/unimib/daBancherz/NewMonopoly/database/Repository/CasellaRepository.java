package unimib.daBancherz.NewMonopoly.database.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.database.Entity.Casella;

import java.util.List;

@Repository
public interface CasellaRepository extends JpaRepository<Casella, Long> {
    @Query("SELECT c FROM Casella c")
    List<Casella> findAll();

    @Query("SELECT COUNT(c) FROM Casella c WHERE c.colore = :colore")
    int countByColore(@Param("colore") String colore);
}
