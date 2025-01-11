package unimib.daBancherz.NewMonopoly.Entity.IdClass;

import java.io.Serializable;
import java.util.Objects;

public class Partita_RegolafedeltaId implements Serializable {

    private String idpartita;
    private Integer idregolafedelta;

    public String getIdpartita() {
        return idpartita;
    }

    public void setIdpartita(String idpartita) {
        this.idpartita = idpartita;
    }

    public Integer getIdregolafedelta() {
        return idregolafedelta;
    }

    public void setIdregolafedelta(Integer idregolafedelta) {
        this.idregolafedelta = idregolafedelta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Partita_RegolafedeltaId that = (Partita_RegolafedeltaId) o;
        return Objects.equals(idpartita, that.idpartita) &&
                Objects.equals(idregolafedelta, that.idregolafedelta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idpartita, idregolafedelta);
    }
}
