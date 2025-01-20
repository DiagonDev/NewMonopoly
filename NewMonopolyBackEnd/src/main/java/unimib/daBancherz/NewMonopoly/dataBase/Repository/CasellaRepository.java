package unimib.daBancherz.NewMonopoly.dataBase.Repository;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Casella;

import java.util.List;

@Repository
public interface CasellaRepository extends JpaRepository<Casella, Long> {
    @Query("SELECT c FROM Casella c")
    List<Casella> findAll();
}
