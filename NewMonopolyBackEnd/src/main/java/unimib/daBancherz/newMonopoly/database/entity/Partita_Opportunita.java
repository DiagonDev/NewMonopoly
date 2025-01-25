package unimib.daBancherz.newMonopoly.database.entity;

import jakarta.persistence.*;
import unimib.daBancherz.newMonopoly.database.entity.idClass.Partita_OpportunitaId;

@Entity
@IdClass(Partita_OpportunitaId.class) // Definisce la chiave primaria composta
public class Partita_Opportunita {
    @Id
    @ManyToOne
    @JoinColumn(name = "idpartita")
    private Partita idpartita;

    @Id @ManyToOne
    @JoinColumn(name = "idopportunita")
    private Opportunita idopportunita;

    @OneToOne
    @JoinColumn(name = "idgiocatore")
    private Giocatore idgiocatore;

    private boolean utilizzato;

    public Partita getIdpartita() {
        return idpartita;
    }

    public void setIdpartita(Partita idpartita) {
        this.idpartita = idpartita;
    }

    public Opportunita getIdopportunita() {
        return idopportunita;
    }

    public void setIdopportunita(Opportunita idopportunita) {
        this.idopportunita = idopportunita;
    }

    public Giocatore getIdgiocatore() {
        return idgiocatore;
    }

    public void setIdgiocatore(Giocatore idgiocatore) {
        this.idgiocatore = idgiocatore;
    }

    public boolean isUtilizzato() {
        return utilizzato;
    }

    public void setUtilizzato(boolean utilizzato) {
        this.utilizzato = utilizzato;
    }
}
