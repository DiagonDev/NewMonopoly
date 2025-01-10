package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Partita_Regolafedelta {
    @Id
    @ManyToOne
    @JoinColumn(name = "idpartita")
    private Partita idpartita;

    @Id
    @ManyToOne
    @JoinColumn(name = "idregolafedelta")
    private Regolafedelta idregolafedelta;

    public Partita getIdpartita() {
        return idpartita;
    }

    public void setIdpartita(Partita idpartita) {
        this.idpartita = idpartita;
    }
df
    public Regolafedelta getIdregolafedelta() {
        return idregolafedelta;
    }

    public void setIdregolafedelta(Regolafedelta idregolafedelta) {
        this.idregolafedelta = idregolafedelta;
    }
}
