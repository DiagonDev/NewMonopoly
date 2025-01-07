package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;

@Entity
public class Partita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPartita;

    private Integer livelloDifficolta;
    private String codiceInvito;
    private Integer stato;
    private Boolean randomizzazione;

    public Integer getIdPartita() {
        return idPartita;
    }

    public void setIdPartita(Integer idPartita) {
        this.idPartita = idPartita;
    }

    public Integer getLivelloDifficolta() {
        return livelloDifficolta;
    }

    public void setLivelloDifficolta(Integer livelloDifficolta) {
        this.livelloDifficolta = livelloDifficolta;
    }

    public String getCodiceInvito() {
        return codiceInvito;
    }

    public void setCodiceInvito(String codiceInvito) {
        this.codiceInvito = codiceInvito;
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
