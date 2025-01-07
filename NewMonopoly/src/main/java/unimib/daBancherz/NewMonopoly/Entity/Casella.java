package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;

@Entity
public class Casella {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCasella;

    private String nome;
    private Integer numCase;
    private Boolean numAlbergo;
    private String colore;
    private String tipo;
    private Integer prezzo;

    @ManyToOne
    private Partita PartitaidPartita;

    @OneToOne
    private PrezzoProprietà PrezzoProprietàidPrezzoProprietà;

    @ManyToOne
    private Banca BancaidBanca;

    @ManyToOne
    private Giocatore GiocatoreidGiocatore;

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

    public Integer getNumCase() {
        return numCase;
    }

    public void setNumCase(Integer numCase) {
        this.numCase = numCase;
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

    public Partita getPartitaidPartita() {
        return PartitaidPartita;
    }

    public void setPartitaidPartita(Partita partitaidPartita) {
        PartitaidPartita = partitaidPartita;
    }

    public PrezzoProprietà getPrezzoProprietàidPrezzoProprietà() {
        return PrezzoProprietàidPrezzoProprietà;
    }

    public void setPrezzoProprietàidPrezzoProprietà(PrezzoProprietà prezzoProprietàidPrezzoProprietà) {
        PrezzoProprietàidPrezzoProprietà = prezzoProprietàidPrezzoProprietà;
    }

    public Banca getBancaidBanca() {
        return BancaidBanca;
    }

    public void setBancaidBanca(Banca bancaidBanca) {
        BancaidBanca = bancaidBanca;
    }

    public Giocatore getGiocatoreidGiocatore() {
        return GiocatoreidGiocatore;
    }

    public void setGiocatoreidGiocatore(Giocatore giocatoreidGiocatore) {
        GiocatoreidGiocatore = giocatoreidGiocatore;
    }
}

