package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;
import unimib.daBancherz.NewMonopoly.Entity.IdClass.Partita_RegolafedeltaId;

@Entity
@IdClass(Partita_RegolafedeltaId.class) // Definisce la chiave primaria composta
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

    public Regolafedelta getIdregolafedelta() {
        return idregolafedelta;
    }

    public void setIdregolafedelta(Regolafedelta idregolafedelta) {
        this.idregolafedelta = idregolafedelta;
    }
}
