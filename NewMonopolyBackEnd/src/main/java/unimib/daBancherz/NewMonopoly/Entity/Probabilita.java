package unimib.daBancherz.NewMonopoly.Entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import unimib.daBancherz.NewMonopoly.Entity.ClassiParametri.*;

@Entity
public class Probabilita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_probabilita;
    private String descrizione;
    private String tipo_azione;
    private String parametro; // Trattiamo come string il parametro json

    public Integer getId_probabilita() {
        return id_probabilita;
    }

    public void setId_probabilita(Integer id_probabilita) {
        this.id_probabilita = id_probabilita;
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
                return objectMapper.readValue(parametro, IdCasella.class);  // Tipo Casella

            case "paga_possedimenti":
                return objectMapper.readValue(parametro, PagaPossedimenti.class);

            case "vai_in_prigione":
                return null;

            case "ricevi_importo":
                return objectMapper.readValue(parametro, Importo.class);

            case "ricevi_importo_giocatore":
                return objectMapper.readValue(parametro, Importo.class);

            case "paga_importo":
                return objectMapper.readValue(parametro, Importo.class);
        }
        return null;
    }
}
