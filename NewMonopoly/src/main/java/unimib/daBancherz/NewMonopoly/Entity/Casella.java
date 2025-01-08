package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;

@Entity
public class Casella {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_casella;

    private String nome;
    private Integer num_casa;
    private Boolean num_albergo;
    private String colore;
    private String tipo;
    private Integer prezzo;

    @ManyToOne
    private Giocatore idgiocatore;

    public Integer getId_casella() {
        return id_casella;
    }

    public void setId_casella(Integer id_casella) {
        this.id_casella = id_casella;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getNum_casa() {
        return num_casa;
    }

    public void setNum_casa(Integer num_casa) {
        this.num_casa = num_casa;
    }

    public Boolean getNum_albergo() {
        return num_albergo;
    }

    public void setNum_albergo(Boolean num_albergo) {
        this.num_albergo = num_albergo;
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

