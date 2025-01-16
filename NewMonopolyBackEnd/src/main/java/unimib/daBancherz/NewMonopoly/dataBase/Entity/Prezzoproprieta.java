package unimib.daBancherz.NewMonopoly.dataBase.Entity;

import jakarta.persistence.*;

@Entity
public class Prezzoproprieta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prezzoproprieta")
    private Integer idPrezzoproprieta;

    private Integer affitto;
    private Integer ipoteca;
    private Integer casa;
    @Column(name = "affitto_1casa")
    private Integer affitto1Casa;
    @Column(name = "affitto_2case")
    private Integer affitto2Case;
    @Column(name = "affitto_3case")
    private Integer affitto3Case;
    @Column(name = "affitto_4case")
    private Integer affitto4Case;
    @Column(name = "affitto_albergo")
    private Integer affittoAlbergo;
    @Column(name = "costo_acquisto")
    private Integer costoAcquisto;

    public Integer getIdPrezzoproprieta() {
        return idPrezzoproprieta;
    }

    public Integer getAffitto() {
        return affitto;
    }

    public Integer getIpoteca() {
        return ipoteca;
    }

    public Integer getCasa() {
        return casa;
    }

    public Integer getAffitto1Casa() {
        return affitto1Casa;
    }

    public Integer getAffitto2Case() {
        return affitto2Case;
    }

    public Integer getAffitto3Case() {
        return affitto3Case;
    }

    public Integer getAffitto4Case() {
        return affitto4Case;
    }

    public Integer getAffittoAlbergo() {
        return affittoAlbergo;
    }

    public Integer getCostoAcquisto() {
        return costoAcquisto;
    }

    public void setCostoAcquisto(Integer costoAcquisto) {
        this.costoAcquisto = costoAcquisto;
    }
}
