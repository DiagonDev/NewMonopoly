package unimib.daBancherz.NewMonopoly.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Pedina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    // Getters e Setters
}

