package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Partita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPartita;
    private Integer livelloDifficoltà;
    private String codiceInvito;
    private Integer stato;
    private Boolean randomizzazione;
}
