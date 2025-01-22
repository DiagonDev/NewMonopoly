package unimib.daBancherz.NewMonopoly.dataBase.Entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.*;

@MappedSuperclass
public abstract class Azione {
    private String descrizione;
    @Column(name = "tipo_azione")
    private String tipoAzione;
    private String parametro;

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getTipoAzione() {
        return tipoAzione;
    }

    public void setTipoAzione(String tipoAzione) {
        this.tipoAzione = tipoAzione;
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

            case "vai_in_prigione", "sposta_avanti":
                return objectMapper.readValue(parametro, IdCasella.class);

            case "paga_possedimenti":
                return objectMapper.readValue(parametro, PagaPossedimenti.class);

            case "ricevi_importo", "ricevi_importo_giocatore", "paga_importo":
                return objectMapper.readValue(parametro, Importo.class);
            default:
                throw new IllegalArgumentException("Tipo azione non riconosciuto: " + tipoAzione);
        }
    }
}
