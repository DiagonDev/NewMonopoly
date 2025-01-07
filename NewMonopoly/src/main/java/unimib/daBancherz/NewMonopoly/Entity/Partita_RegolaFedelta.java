package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Partita_RegolaFedelta {
    @Id
    @ManyToOne
    private Partita PartitaidPartita;

    @Id
    @ManyToOne
    private RegoleFedelta regoleFedeltaidRegoleFedelta;

    public Partita getPartitaidPartita() {
        return PartitaidPartita;
    }

    public void setPartitaidPartita(Partita partitaidPartita) {
        PartitaidPartita = partitaidPartita;
    }

    public RegoleFedelta getRegoleFedeltaidRegoleFedelta() {
        return regoleFedeltaidRegoleFedelta;
    }

    public void setRegoleFedeltaidRegoleFedelta(RegoleFedelta regoleFedeltaidRegoleFedelta) {
        this.regoleFedeltaidRegoleFedelta = regoleFedeltaidRegoleFedelta;
    }
}
