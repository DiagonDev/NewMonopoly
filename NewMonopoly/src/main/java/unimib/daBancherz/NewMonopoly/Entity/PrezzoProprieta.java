package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PrezzoProprieta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPrezzoProprieta;

    private Integer affitto;
    private Integer ipoteca;
    private Integer casa;
    private Integer hotel;

    public Integer getIdPrezzoProprieta() {
        return idPrezzoProprieta;
    }

    public void setIdPrezzoProprieta(Integer idPrezzoProprieta) {
        this.idPrezzoProprieta = idPrezzoProprieta;
    }

    public Integer getAffitto() {
        return affitto;
    }

    public void setAffitto(Integer affitto) {
        this.affitto = affitto;
    }

    public Integer getIpoteca() {
        return ipoteca;
    }

    public void setIpoteca(Integer ipoteca) {
        this.ipoteca = ipoteca;
    }

    public Integer getCasa() {
        return casa;
    }

    public void setCasa(Integer casa) {
        this.casa = casa;
    }

    public Integer getHotel() {
        return hotel;
    }

    public void setHotel(Integer hotel) {
        this.hotel = hotel;
    }
}
