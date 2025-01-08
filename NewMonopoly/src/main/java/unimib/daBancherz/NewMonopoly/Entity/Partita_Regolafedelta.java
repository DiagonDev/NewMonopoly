package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Partita_Regolafedelta {
    @Id
    @ManyToOne
    private Partita idpartita;

    @Id
    @ManyToOne
    private Regolafedelta idregolafedelta;

    public Partita getIdpartita() {
        return idpartita;
    }

    public void setIdpartita(Partita idpartita) {
        this.idpartita = idpartita;
    }

    public Regolafedelta getIdregolafedelta() {
        return idregolafedelta;
    }

    public void setIdregolafedelta(Regolafedelta idregolafedelta) {
        this.idregolafedelta = idregolafedelta;
    }
}
