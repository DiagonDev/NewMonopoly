package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;

@Entity
public class Regolafedelta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_regolafedelta")
    private Integer idRegolafedelta;
    private String descrizione;
    @Column(name = "tipo_azione")
    private String tipoAzione;
    private String parametro; // Trattiamo come string il parametro json
}
