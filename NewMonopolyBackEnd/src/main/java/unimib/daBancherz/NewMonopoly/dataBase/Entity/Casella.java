package unimib.daBancherz.NewMonopoly.dataBase.Entity;

import jakarta.persistence.*;

@Entity
public class Casella {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_casella")
    private Integer idCasella;

    private String nome;
    private String colore;
    private String tipo;

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


}

