package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;

@Entity
public class Partita_Probabilita {
    @Id
    @ManyToOne
    private Partita idpartita;

    @Id @ManyToOne
    private Probabilita idprobabilita;

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
}
