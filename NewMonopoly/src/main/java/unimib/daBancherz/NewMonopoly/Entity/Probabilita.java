package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Probabilita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProbabilita;
    private String descrizione;

    public Integer getIdProbabilita() {
        return idProbabilita;
    }

    public void setIdProbabilita(Integer idProbabilita) {
        this.idProbabilita = idProbabilita;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}
