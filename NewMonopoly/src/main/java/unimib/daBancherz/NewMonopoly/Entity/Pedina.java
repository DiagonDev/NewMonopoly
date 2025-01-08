package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Pedina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_pedina;

    private String nome;

    public Integer getId_pedina() {
        return id_pedina;
    }

    public void setId_pedina(Integer id_pedina) {
        this.id_pedina = id_pedina;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}

