package unimib.daBancherz.NewMonopoly.dataBase.Entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.IdCasella;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.Importo;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.PagaPossedimenti;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.TipoCasella;

@Entity
public class Opportunita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_probabilita")
    private Integer idProbabilita;
    private String descrizione;
    @Column(name = "tipo_azione")
    private String tipoAzione;
    private String parametro;
    private String tipo;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

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
                if (parametro.contains("id_casella")) {
                    return objectMapper.readValue(parametro, IdCasella.class);  // Id Casella
                } else if (parametro.contains("tipo_casella")) {
                    return objectMapper.readValue(parametro, TipoCasella.class);  // Tipo Casella
                }

            case "paga_possedimenti":
                return objectMapper.readValue(parametro, PagaPossedimenti.class);

            case "ricevi_importo", "ricevi_importo_giocatore", "paga_importo_giocatore", "paga_importo":
                return objectMapper.readValue(parametro, Importo.class);

            default:
                throw new IllegalArgumentException("Tipo azione non riconosciuto: " + tipoAzione);
        }
    }

    public Integer getIdProbabilita() {
        return idProbabilita;
    }

    public void setIdProbabilita(Integer idProbabilita) {
        this.idProbabilita = idProbabilita;
    }
}
