package unimib.daBancherz.NewMonopoly.database.Entity;

import jakarta.persistence.*;
import unimib.daBancherz.NewMonopoly.database.Entity.IdClass.Partita_Casella_PrezzoproprietaId;

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

    @Column(name = "prezzo_corrente")
    private Integer prezzoCorrente;

    @Column(name = "prezzo_casa_corrente")
    private Integer prezzoCasaCorrente;
    private Integer posizione;

    @ManyToOne
    @JoinColumn(name = "idgiocatore")
    private Giocatore idgiocatore;


    @Column(name = "num_casa")
    private Integer numCasa;

    public Giocatore getIdgiocatore() {
        return idgiocatore;
    }

    public void setIdgiocatore(Giocatore idgiocatore) {
        this.idgiocatore = idgiocatore;
    }

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

    public Integer getPrezzoCorrente() {
        return prezzoCorrente;
    }

    public void setPrezzoCorrente(Integer prezzoCorrente) {
        this.prezzoCorrente = prezzoCorrente;
    }

    public Integer getPrezzoCasaCorrente() {
        return prezzoCasaCorrente;
    }

    public void setPrezzoCasaCorrente(Integer prezzoCasaCorrente) {
        this.prezzoCasaCorrente = prezzoCasaCorrente;
    }

    public Integer getPosizione() {
        return posizione;
    }

    public void setPosizione(Integer posizione) {
        this.posizione = posizione;
    }

    public Integer getNumCasa() {
        return numCasa;
    }

    public void setNumCasa(Integer numCasa) {
        this.numCasa = numCasa;
    }

}
