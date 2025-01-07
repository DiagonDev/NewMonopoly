package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PrezzoProprietà {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPrezzoProprietà;

    private Integer affitto;
    private Integer ipoteca;
    private Integer casa;
    private Integer hotel;


}
