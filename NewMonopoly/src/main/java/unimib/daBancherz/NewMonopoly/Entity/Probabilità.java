package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Probabilità {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProbabilità;
    private String descrizione;
}
