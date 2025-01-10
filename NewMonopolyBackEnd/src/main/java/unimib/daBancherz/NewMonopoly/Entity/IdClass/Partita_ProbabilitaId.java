package unimib.daBancherz.NewMonopoly.Entity.IdClass;

import java.io.Serializable;
import java.util.Objects;

public class Partita_ProbabilitaId implements Serializable {

    private String idpartita;
    private Integer idprobabilita;

    public String getIdpartita() {
        return idpartita;
    }

    public void setIdpartita(String idpartita) {
        this.idpartita = idpartita;
    }

    public Integer getIdprobabilita() {
        return idprobabilita;
    }

    public void setIdprobabilita(Integer idprobabilita) {
        this.idprobabilita = idprobabilita;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Partita_ProbabilitaId that = (Partita_ProbabilitaId) o;
        return Objects.equals(idpartita, that.idpartita) &&
                Objects.equals(idprobabilita, that.idprobabilita);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idpartita, idprobabilita);
    }
}
