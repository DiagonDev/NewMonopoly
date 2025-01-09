package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;

@Entity
public class Partita_Casella_Prezzoproprieta {
    @Id
    @ManyToOne
    private Partita idpartita;

    @Id
    @ManyToOne
    private Casella idcasella;

    @Id
    @ManyToOne
    private Prezzoproprieta idprezzoproprieta;

    public Partita getIdpartita() {
        return idpartita;
    }

    public void setIdpartita(Partita idpartita) {
        this.idpartita = idpartita;
    }

    public Casella getIdcasella() {
        return idcasella;
    }

    public void setIdcasella(Casella idcasella) {
        this.idcasella = idcasella;
    }
}
