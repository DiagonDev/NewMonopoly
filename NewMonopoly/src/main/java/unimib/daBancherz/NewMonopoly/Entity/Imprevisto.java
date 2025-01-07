package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Imprevisto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idImprevisto;

    private String descrizione;


}