package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;
import unimib.daBancherz.NewMonopoly.Entity.IdClass.Partita_ProbabilitaId;

@Entity
@IdClass(Partita_ProbabilitaId.class) // Definisce la chiave primaria composta
public class Partita_Probabilita {
    @Id
    @ManyToOne
    @JoinColumn(name = "idpartita")
    private Partita idpartita;

    @Id @ManyToOne
    @JoinColumn(name = "idprobabilita")
    private Probabilita idprobabilita;

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

    public Probabilita getIdprobabilita() {
        return idprobabilita;
    }

    public void setIdprobabilita(Probabilita idprobabilita) {
        this.idprobabilita = idprobabilita;
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
