package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.*;

@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"PartitaidPartita", "PedinaidPedina"})
)
/*  definisce che la combinazione di queste due colonne (PartitaidPartita e PedinaidPedina)
    deve essere unica nella tabella corrispondente al database. */
@Entity
public class Giocatore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idGiocatore;

    private String nome;
    private Integer saldo;
    private Integer puntiFedelta;
    private String tipo;

    @ManyToOne
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

    public Integer getPuntiFedelta() {
        return puntiFedelta;
    }

    public void setPuntiFedelta(Integer puntiFedeltà) {
        this.puntiFedelta = puntiFedeltà;
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