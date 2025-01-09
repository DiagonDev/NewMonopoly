package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;

@Entity
public class Partita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_partita;

    private Integer livello_difficolta;
    private String codice_invito;
    private Integer stato;
    private Boolean randomizzazione;

    public Integer getId_partita() {
        return id_partita;
    }

    public void setId_partita(Integer id_partita) {
        this.id_partita = id_partita;
    }

    public Integer getLivello_difficolta() {
        return livello_difficolta;
    }

    public void setLivello_difficolta(Integer livello_difficolta) {
        this.livello_difficolta = livello_difficolta;
    }

    public String getCodice_invito() {
        return codice_invito;
    }

    public void setCodice_invito(String codice_invito) {
        this.codice_invito = codice_invito;
    }

    public Integer getStato() {
        return stato;
    }

    public void setStato(Integer stato) {
        this.stato = stato;
    }

    public Boolean getRandomizzazione() {
        return randomizzazione;
    }

    public void setRandomizzazione(Boolean randomizzazione) {
        this.randomizzazione = randomizzazione;
    }
}
