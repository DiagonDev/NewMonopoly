package unimib.daBancherz.NewMonopoly.dataBase.Entity;

import jakarta.persistence.*;

@Entity
public class Pedina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedina")
    private Integer idPedina;

    private String nome;

    public Integer getIdPedina() {
        return idPedina;
    }

    public void setIdPedina(Integer idPedina) {
        this.idPedina = idPedina;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}

