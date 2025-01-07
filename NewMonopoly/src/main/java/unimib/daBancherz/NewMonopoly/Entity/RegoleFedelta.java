package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class RegoleFedelta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRegoleFedelta;
    private Integer puntiFedelta;
    private String descrizione;

    public Integer getIdRegoleFedelta() {
        return idRegoleFedelta;
    }

    public void setIdRegoleFedelta(Integer idRegoleFedelta) {
        this.idRegoleFedelta = idRegoleFedelta;
    }

    public Integer getPuntiFedelta() {
        return puntiFedelta;
    }

    public void setPuntiFedelta(Integer puntiFedelta) {
        this.puntiFedelta = puntiFedelta;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}
