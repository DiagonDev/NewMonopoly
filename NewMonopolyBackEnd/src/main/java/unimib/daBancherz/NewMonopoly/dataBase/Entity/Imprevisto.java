package unimib.daBancherz.NewMonopoly.dataBase.Entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.IdCasella;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.Importo;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.PagaPossedimenti;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.TipoCasella;

@Entity
public class Imprevisto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imprevisto")
    private Integer idImprevisto;

    private String descrizione;
    @Column(name = "tipo_azione")
    private String tipoAzione;
    private String parametro; // Trattiamo come string il parametro json

    public Integer getIdImprevisto() {
        return idImprevisto;
    }

    public void setIdImprevisto(Integer idImprevisto) {
        this.idImprevisto = idImprevisto;
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
                if (parametro.contains("id_casella")) {
                    return objectMapper.readValue(parametro, IdCasella.class);  // Tipo Casella
                } else if (parametro.contains("tipo_casella")) {
                    return objectMapper.readValue(parametro, TipoCasella.class);  // Tipo Casella
                }
                break;
            case "paga_possedimenti":
                return objectMapper.readValue(parametro, PagaPossedimenti.class);

            case "vai_in_prigione":
                return null;

            case "ricevi_importo":
                return objectMapper.readValue(parametro, Importo.class);

            case "paga_importo_giocatore":
                return objectMapper.readValue(parametro, Importo.class);

            case "paga_importo":
                return objectMapper.readValue(parametro, Importo.class);
        }
        return null;
    }
}