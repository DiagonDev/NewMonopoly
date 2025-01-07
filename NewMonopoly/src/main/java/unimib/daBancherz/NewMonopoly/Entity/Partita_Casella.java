package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;

@Entity
public class Partita_Casella {
    @Id
    @ManyToOne
    private Partita PartitaidPartita;

    @Id
    @ManyToOne
    private Casella CasellaidCasella;

    public Partita getPartitaidPartita() {
        return PartitaidPartita;
    }

    public void setPartitaidPartita(Partita partitaidPartita) {
        PartitaidPartita = partitaidPartita;
    }

    public Casella getCasellaidCasella() {
        return CasellaidCasella;
    }

    public void setCasellaidCasella(Casella casellaidCasella) {
        CasellaidCasella = casellaidCasella;
    }
}
