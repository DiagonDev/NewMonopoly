package unimib.daBancherz.NewMonopoly.dataBase.Entity;

import jakarta.persistence.*;

@Entity
public class Imprevisto extends Azione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imprevisto")
    private Integer idImprevisto;

    public Integer getIdImprevisto() {
        return idImprevisto;
    }

    public void setIdImprevisto(Integer idImprevisto) {
        this.idImprevisto = idImprevisto;
    }
}