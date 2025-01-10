package unimib.daBancherz.NewMonopoly.Entity.IdClass;

import java.io.Serializable;
import java.util.Objects;

public class Partita_Casella_PrezzoproprietaId implements Serializable {

    private Integer idpartita;
    private Integer idcasella;


    public Integer getIdpartita() {
        return idpartita;
    }

    public void setIdpartita(Integer idpartita) {
        this.idpartita = idpartita;
    }

    public Integer getIdcasella() {
        return idcasella;
    }

    public void setIdcasella(Integer idcasella) {
        this.idcasella = idcasella;
    }

    // equals() e hashCode() per garantire corretta comparazione degli oggetti

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
