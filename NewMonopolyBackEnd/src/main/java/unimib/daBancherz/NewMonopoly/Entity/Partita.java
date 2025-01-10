package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;

@Entity
public class Partita {
    @Id @Column(name = "codice_invito")
    private String codiceInvito;

    @Column(name = "livello_difficolta")
    private String livelloDifficolta;
    private String stato;
    private Boolean randomizzazione;

    public String getCodiceInvito() {
        return codiceInvito;
    }

    public void setCodiceInvito(String codiceInvito) {
        this.codiceInvito = codiceInvito;
    }

    public String getLivelloDifficolta() {
        return livelloDifficolta;
    }

    public void setLivelloDifficolta(String livelloDifficolta) {
        this.livelloDifficolta = livelloDifficolta;
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
