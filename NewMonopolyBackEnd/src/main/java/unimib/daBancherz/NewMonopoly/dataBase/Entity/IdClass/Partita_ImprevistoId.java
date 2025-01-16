package unimib.daBancherz.NewMonopoly.dataBase.Entity.IdClass;

import java.io.Serializable;
import java.util.Objects;

public class Partita_ImprevistoId implements Serializable {

    private String idpartita;
    private Integer idimprevisto;

    public String getIdpartita() {
        return idpartita;
    }

    public void setIdpartita(String idpartita) {
        this.idpartita = idpartita;
    }

    public Integer getIdimprevisto() {
        return idimprevisto;
    }

    public void setIdimprevisto(Integer idimprevisto) {
        this.idimprevisto = idimprevisto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Partita_ImprevistoId that = (Partita_ImprevistoId) o;
        return Objects.equals(idpartita, that.idpartita) &&
                Objects.equals(idimprevisto, that.idimprevisto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idpartita, idimprevisto);
    }
}