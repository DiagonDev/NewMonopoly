package unimib.dabancherz.newmonopoly.database.entity;

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

    public void setIdPrezzoproprieta(Integer idPrezzoproprieta) {
        this.idPrezzoproprieta = idPrezzoproprieta;
    }

    public void setAffitto(Integer affitto) {
        this.affitto = affitto;
    }

    public void setIpoteca(Integer ipoteca) {
        this.ipoteca = ipoteca;
    }

    public void setCasa(Integer casa) {
        this.casa = casa;
    }

    public void setAffitto1Casa(Integer affitto1Casa) {
        this.affitto1Casa = affitto1Casa;
    }

    public void setAffitto2Case(Integer affitto2Case) {
        this.affitto2Case = affitto2Case;
    }

    public void setAffitto3Case(Integer affitto3Case) {
        this.affitto3Case = affitto3Case;
    }

    public void setAffitto4Case(Integer affitto4Case) {
        this.affitto4Case = affitto4Case;
    }

    public void setAffittoAlbergo(Integer affittoAlbergo) {
        this.affittoAlbergo = affittoAlbergo;
    }
}
