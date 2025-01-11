package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;
import unimib.daBancherz.NewMonopoly.Entity.IdClass.Partita_Casella_PrezzoproprietaId;

@Entity
@IdClass(Partita_Casella_PrezzoproprietaId.class)
public class Partita_Casella_Prezzoproprieta {
    @Id
    @ManyToOne
    @JoinColumn(name = "idpartita")
    private Partita idpartita;

    @Id
    @ManyToOne
    @JoinColumn(name = "idcasella")
    private Casella idcasella;


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
