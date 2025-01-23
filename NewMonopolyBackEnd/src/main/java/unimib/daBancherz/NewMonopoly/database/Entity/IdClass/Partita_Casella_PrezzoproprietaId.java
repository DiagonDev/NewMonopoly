package unimib.daBancherz.NewMonopoly.database.Entity.IdClass;

import java.io.Serializable;
import java.util.Objects;

public class Partita_Casella_PrezzoproprietaId implements Serializable {

    private String idpartita;
    private Integer idcasella;


    public String getIdpartita() {
        return idpartita;
    }

    public void setIdpartita(String idpartita) {
        this.idpartita = idpartita;
    }

    public Integer getIdcasella() {
        return idcasella;
    }

    public void setIdcasella(Integer idcasella) {
        this.idcasella = idcasella;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Partita_Casella_PrezzoproprietaId that = (Partita_Casella_PrezzoproprietaId) o;
        return Objects.equals(idpartita, that.idpartita) &&
               Objects.equals(idcasella, that.idcasella);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idpartita, idcasella);
    }
}
