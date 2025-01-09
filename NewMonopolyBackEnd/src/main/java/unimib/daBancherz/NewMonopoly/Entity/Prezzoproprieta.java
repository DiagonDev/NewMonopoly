package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Prezzoproprieta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_prezzoproprieta;

    private Integer affitto;
    private Integer ipoteca;
    private Integer casa;
    private Integer albergo;

    public Integer getId_prezzoproprieta() {
        return id_prezzoproprieta;
    }

    public void setId_prezzoproprieta(Integer id_prezzoproprieta) {
        this.id_prezzoproprieta = id_prezzoproprieta;
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

    public Integer getAlbergo() {
        return albergo;
    }

    public void setAlbergo(Integer albergo) {
        this.albergo = albergo;
    }
}
