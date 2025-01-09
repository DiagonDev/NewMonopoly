package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;
@Entity
public class Partita_Imprevisto {
    @Id
    @ManyToOne
    @JoinColumn(name = "idpartita")
    private Partita idpartita;

    @Id
    @ManyToOne
    @JoinColumn(name = "idimprevisto")
    private Imprevisto idimprevisto;

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
