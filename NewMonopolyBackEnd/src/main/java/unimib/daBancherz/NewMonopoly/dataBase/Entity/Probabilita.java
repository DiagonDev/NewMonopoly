package unimib.daBancherz.NewMonopoly.dataBase.Entity;

import jakarta.persistence.*;

@Entity
public class Probabilita extends Azione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_probabilita")
    private Integer idProbabilita;

    public Integer getIdProbabilita() {
        return idProbabilita;
    }

    public void setIdProbabilita(Integer idProbabilita) {
        this.idProbabilita = idProbabilita;
    }
}
