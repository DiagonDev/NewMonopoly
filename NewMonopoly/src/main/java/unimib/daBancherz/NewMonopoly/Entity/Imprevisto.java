package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;

@Entity
public class Imprevisto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idImprevisto;

    private String descrizione;

    public Integer getIdImprevisto() {
        return idImprevisto;
    }

    public void setIdImprevisto(Integer idImprevisto) {
        this.idImprevisto = idImprevisto;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}