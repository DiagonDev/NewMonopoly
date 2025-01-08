package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Regolafedelta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_regolafedelta;
    private String descrizione;
    private String tipo_azione;
    private String parametro; // Trattiamo come string il parametro json
}
