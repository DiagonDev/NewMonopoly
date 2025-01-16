package unimib.daBancherz.NewMonopoly.dataBase.Entity;

import jakarta.persistence.*;

@Entity
public class Casella {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_casella")
    private Integer idCasella;

    private String nome;
    @Column(name = "num_casa")
    private Integer numCasa;
    @Column(name = "num_albergo")
    private Boolean numAlbergo;
    private String colore;
    private String tipo;
    private Integer prezzo;

    @ManyToOne
    @JoinColumn(name = "idgiocatore")
    private Giocatore idgiocatore;

    public Integer getIdCasella() {
        return idCasella;
    }

    public void setIdCasella(Integer idCasella) {
        this.idCasella = idCasella;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getNumCasa() {

        return numCasa;
    }

    public void setNumCasa(Integer numCasa) {
        this.numCasa = numCasa;
    }

    public Boolean getNumAlbergo() {
        return numAlbergo;
    }

    public void setNumAlbergo(Boolean numAlbergo) {
        this.numAlbergo = numAlbergo;
    }

    public String getColore() {
        return colore;
    }

    public void setColore(String colore) {
        this.colore = colore;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(Integer prezzo) {
        this.prezzo = prezzo;
    }

    public Giocatore getIdgiocatore() {
        return idgiocatore;
    }

    public void setIdgiocatore(Giocatore idgiocatore) {
        this.idgiocatore = idgiocatore;
    }
}

