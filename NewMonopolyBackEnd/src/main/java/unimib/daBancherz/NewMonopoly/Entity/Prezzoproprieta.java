package unimib.daBancherz.NewMonopoly.Entity;

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
    private Integer albergo;

    public Integer getIdPrezzoproprieta() {
        return idPrezzoproprieta;
    }

    public void setIdPrezzoproprieta(Integer idPrezzoproprieta) {
        this.idPrezzoproprieta = idPrezzoproprieta;
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
