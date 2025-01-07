package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;

@Entity
public class Giocatore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idGiocatore;

    private String nome;
    private Integer saldo;
    private Integer puntiFedeltà;
    private String tipo;

    @OneToOne
    private Pedina PedinaidPedina;

    @ManyToOne
    private Partita PartitaidPartita;

    public Integer getIdGiocatore() {
        return idGiocatore;
    }

    public void setIdGiocatore(Integer idGiocatore) {
        this.idGiocatore = idGiocatore;
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

    public Integer getPuntiFedeltà() {
        return puntiFedeltà;
    }

    public void setPuntiFedeltà(Integer puntiFedeltà) {
        this.puntiFedeltà = puntiFedeltà;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Pedina getPedinaidPedina() {
        return PedinaidPedina;
    }

    public void setPedinaidPedina(Pedina pedinaidPedina) {
        PedinaidPedina = pedinaidPedina;
    }

    public Partita getPartitaidPartita() {
        return PartitaidPartita;
    }

    public void setPartitaidPartita(Partita partitaidPartita) {
        PartitaidPartita = partitaidPartita;
    }
}