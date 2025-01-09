package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;

@Entity
public class Partita {
    @Id
    private String codice_invito;

    private String livello_difficolta;
    private String stato;
    private Boolean randomizzazione;

    public String getCodice_invito() {
        return codice_invito;
    }

    public void setCodice_invito(String codice_invito) {
        this.codice_invito = codice_invito;
    }

    public String getLivello_difficolta() {
        return livello_difficolta;
    }

    public void setLivello_difficolta(String livello_difficolta) {
        this.livello_difficolta = livello_difficolta;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public Boolean getRandomizzazione() {
        return randomizzazione;
    }

    public void setRandomizzazione(Boolean randomizzazione) {
        this.randomizzazione = randomizzazione;
    }
}
