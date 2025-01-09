package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;

@Entity
public class Partita_Casella_Prezzoproprieta {
    @Id
    @ManyToOne
    @JoinColumn(name = "idpartita")
    private Partita idpartita;

    @Id
    @ManyToOne
    @JoinColumn(name = "idcasella")
    private Casella idcasella;

    @Id
    @ManyToOne
    @JoinColumn(name = "idprezzoproprieta")
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

    public Prezzoproprieta getIdprezzoproprieta() {
        return idprezzoproprieta;
    }

    public void setIdprezzoproprieta(Prezzoproprieta idprezzoproprieta) {
        this.idprezzoproprieta = idprezzoproprieta;
    }
}
