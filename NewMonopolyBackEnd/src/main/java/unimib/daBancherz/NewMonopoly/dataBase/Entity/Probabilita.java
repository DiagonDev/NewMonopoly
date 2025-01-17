package unimib.daBancherz.NewMonopoly.dataBase.Entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.IdCasella;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.Importo;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.PagaPossedimenti;

@Entity
public class Probabilita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_probabilita")
    private Integer idProbabilita;
    private String descrizione;
    @Column(name = "tipo_azione")
    private String tipoAzione;
    private String parametro; // Trattiamo come string il parametro json

    public Integer getIdProbabilita() {
        return idProbabilita;
    }

    public void setIdProbabilita(Integer idProbabilita) {
        this.idProbabilita = idProbabilita;
    }

    public String getTipoAzione() {
        return tipoAzione;
    }

    public void setTipoAzione(String tipoAzione) {
        this.tipoAzione = tipoAzione;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getParametro() {
        return parametro;
    }

    public void setParametro(String parametro) {
        this.parametro = parametro;
    }

    public Object getParametroDeserializzato() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        switch (tipoAzione) {
            case "esci_prigione":
                return null;

            case "sposta_avanti":
                return objectMapper.readValue(parametro, IdCasella.class);  // Tipo Casella

            case "paga_possedimenti":
                return objectMapper.readValue(parametro, PagaPossedimenti.class);

            case "vai_in_prigione":
                return null;

            case "ricevi_importo":
            case "ricevi_importo_giocatore":
            case "paga_importo":
                return objectMapper.readValue(parametro, Importo.class);
        }
        return null;
    }
}
