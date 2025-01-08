package unimib.daBancherz.NewMonopoly.Entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import unimib.daBancherz.NewMonopoly.Entity.ClassiParametri.*;

@Entity
public class Imprevisto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_imprevisto;

    private String descrizione;
    private String tipo_azione;
    private String parametro; // Trattiamo come string il parametro json

    public Integer getId_imprevisto() {
        return id_imprevisto;
    }

    public void setId_imprevisto(Integer id_imprevisto) {
        this.id_imprevisto = id_imprevisto;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getTipo_azione() {
        return tipo_azione;
    }

    public void setTipo_azione(String tipo_azione) {
        this.tipo_azione = tipo_azione;
    }

    public String getParametro() {
        return parametro;
    }

    public void setParametro(String parametro) {
        this.parametro = parametro;
    }

    public Object getParametroDeserializzato() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        switch (tipo_azione) {
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