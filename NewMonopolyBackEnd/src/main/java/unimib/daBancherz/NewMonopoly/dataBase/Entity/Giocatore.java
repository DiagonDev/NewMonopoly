package unimib.daBancherz.NewMonopoly.dataBase.Entity;

import jakarta.persistence.*;

@Entity
public class Giocatore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_giocatore")
    private Integer idGiocatore;

    private String nome;
    private Integer saldo;
    @Column(name = "punti_fedelta")
    private Integer puntiFedelta;
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "idpedina")
    private Pedina idpedina;

    @ManyToOne
    @JoinColumn(name = "idpartita")
    private Partita idpartita;

    public Integer getIdGiocatore() {
        return idGiocatore;
    }

    public void setIdGiocatore(Integer idGiocatore) {
        this.idGiocatore = idGiocatore;
    }

    public Integer getPuntiFedelta() {
        return puntiFedelta;
    }

    public void setPuntiFedelta(Integer puntiFedelta) {
        this.puntiFedelta = puntiFedelta;
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