package unimib.daBancherz.NewMonopoly.database.Entity;

import jakarta.persistence.*;

@Entity
public class Regolafedelta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_regolafedelta")
    private Integer idRegolafedelta;
    private String descrizione;
    @Column(name = "tipo_casella")
    private String tipoCasella;
    private Integer punti;

    public Integer getIdRegolafedelta() {
        return idRegolafedelta;
    }

    public void setIdRegolafedelta(Integer idRegolafedelta) {
        this.idRegolafedelta = idRegolafedelta;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getTipoCasella() {
        return tipoCasella;
    }

    public void setTipoCasella(String tipoCasella) {
        this.tipoCasella = tipoCasella;
    }

    public Integer getPunti() {
        return punti;
    }

    public void setPunti(Integer punti) {
        this.punti = punti;
    }
}
