package unimib.daBancherz.NewMonopoly.database.Entity;

import jakarta.persistence.*;
import unimib.daBancherz.NewMonopoly.database.Entity.IdClass.Partita_RegolafedeltaId;

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

    private boolean utilizzato;

    public boolean isUtilizzato() {
        return utilizzato;
    }

    public void setUtilizzato(boolean utilizzato) {
        this.utilizzato = utilizzato;
    }

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
