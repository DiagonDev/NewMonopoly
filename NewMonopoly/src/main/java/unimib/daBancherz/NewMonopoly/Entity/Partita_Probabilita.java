package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;

@Entity
public class Partita_Probabilita {
    @Id
    @ManyToOne
    private Partita PartitaidPartita;

    @Id @ManyToOne
    private Probabilita probabilitaidProbabilita;

    public Partita getPartitaidPartita() {
        return PartitaidPartita;
    }

    public void setPartitaidPartita(Partita partitaidPartita) {
        PartitaidPartita = partitaidPartita;
    }

    public Probabilita getProbabilitaidProbabilita() {
        return probabilitaidProbabilita;
    }

    public void setProbabilitaidProbabilita(Probabilita probabilitaidProbabilita) {
        this.probabilitaidProbabilita = probabilitaidProbabilita;
    }
}
