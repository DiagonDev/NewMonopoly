package unimib.daBancherz.newMonopoly.database.entity.idClass;

import java.io.Serializable;
import java.util.Objects;

public class Partita_OpportunitaId implements Serializable {

    private String idpartita;
    private Integer idopportunita;

    public String getIdpartita() {
        return idpartita;
    }

    public void setIdpartita(String idpartita) {
        this.idpartita = idpartita;
    }

    public Integer getIdopportunita() {
        return idopportunita;
    }

    public void setIdopportunita(Integer idopportunita) {
        this.idopportunita = idopportunita;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Partita_OpportunitaId that = (Partita_OpportunitaId) o;
        return Objects.equals(idpartita, that.idpartita) &&
                Objects.equals(idopportunita, that.idopportunita);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idpartita, idopportunita);
    }
}
