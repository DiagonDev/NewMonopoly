package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;
import unimib.daBancherz.NewMonopoly.Entity.IdClass.Partita_ImprevistoId;

@Entity
@IdClass(Partita_ImprevistoId.class) // Definisce la chiave primaria composta
public class Partita_Imprevisto {
    @Id
    @ManyToOne
    @JoinColumn(name = "idpartita")
    private Partita idpartita;

    @Id
    @ManyToOne
    @JoinColumn(name = "idimprevisto")
    private Imprevisto idimprevisto;

    @OneToOne
    @JoinColumn(name = "idgiocatore")
    private Giocatore idgiocatore;

    private boolean utilizzato;

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

    public Partita getIdpartita() {
        return idpartita;
    }

    public void setIdpartita(Partita idpartita) {
        this.idpartita = idpartita;
    }

    public Imprevisto getIdimprevisto() {
        return idimprevisto;
    }

    public void setIdimprevisto(Imprevisto idimprevisto) {
        this.idimprevisto = idimprevisto;
    }
}
