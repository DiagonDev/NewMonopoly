package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;

@Entity
public class Giocatore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_giocatore;

    private String nome;
    private Integer saldo;
    private Integer punti_fedelta;
    private String tipo;

    @ManyToOne
    private Pedina idpedina;

    @ManyToOne
    private Partita idpartita;

    public Integer getId_giocatore() {
        return id_giocatore;
    }

    public void setId_giocatore(Integer id_giocatore) {
        this.id_giocatore = id_giocatore;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getSaldo() {
        return saldo;
    }

    public void setSaldo(Integer saldo) {
        this.saldo = saldo;
    }

    public Integer getPunti_fedelta() {
        return punti_fedelta;
    }

    public void setPunti_fedelta(Integer punti_fedelta) {
        this.punti_fedelta = punti_fedelta;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Pedina getIdpedina() {
        return idpedina;
    }

    public void setIdpedina(Pedina idpedina) {
        this.idpedina = idpedina;
    }

    public Partita getIdpartita() {
        return idpartita;
    }

    public void setIdpartita(Partita idpartita) {
        this.idpartita = idpartita;
    }
}