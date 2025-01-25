package unimib.dabancherz.newmonopoly.database.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unimib.dabancherz.newmonopoly.database.entity.classiparametri.*;

import static org.junit.jupiter.api.Assertions.*;

class OpportunitaTest {

    private Opportunita opportunita;

    @BeforeEach
    void setUp() {
        opportunita = new Opportunita();
    }

    @Test
    void testGetAndSetTipo() {
        opportunita.setTipo("test_tipo");
        assertEquals("test_tipo", opportunita.getTipo());
    }

    @Test
    void testGetAndSetDescrizione() {
        opportunita.setDescrizione("test_descrizione");
        assertEquals("test_descrizione", opportunita.getDescrizione());
    }

    @Test
    void testGetAndSetTipoAzione() {
        opportunita.setTipoAzione("test_tipo_azione");
        assertEquals("test_tipo_azione", opportunita.getTipoAzione());
    }

    @Test
    void testGetAndSetParametro() {
        opportunita.setParametro("test_parametro");
        assertEquals("test_parametro", opportunita.getParametro());
    }

    @Test
    void testGetAndSetIdOpportunita() {
        opportunita.setIdOpportunita(123);
        assertEquals(123, opportunita.getIdOpportunita().intValue());
    }

    @Test
    void testGetParametroDeserializzato_IdCasella() throws Exception {
        String json = "{\"id_casella\": 1}";
        opportunita.setTipoAzione("vai_in_prigione");
        opportunita.setParametro(json);

        IdCasella result = (IdCasella) opportunita.getParametroDeserializzato();
        assertEquals(1, result.getId_casella().intValue());
    }

    @Test
    void testGetParametroDeserializzato_TipoCasella() throws Exception {
        String json = "{\"tipo_casella\": \"normale\"}";
        opportunita.setTipoAzione("sposta_avanti");
        opportunita.setParametro(json);

        TipoCasella result = (TipoCasella) opportunita.getParametroDeserializzato();
        assertEquals("normale", result.getTipo_casella());
    }

    @Test
    void testGetParametroDeserializzato_PagaPossedimenti() throws Exception {
        String json = "{\"costo_casa\": 100, \"costo_albergo\": 200}";
        opportunita.setTipoAzione("paga_possedimenti");
        opportunita.setParametro(json);

        PagaPossedimenti result = (PagaPossedimenti) opportunita.getParametroDeserializzato();
        assertEquals(100, result.getCosto_casa().intValue());
        assertEquals(200, result.getCosto_albergo().intValue());
    }

    @Test
    void testGetParametroDeserializzato_Importo() throws Exception {
        String json = "{\"importo\": 500}";
        opportunita.setTipoAzione("ricevi_importo");
        opportunita.setParametro(json);

        Importo result = (Importo) opportunita.getParametroDeserializzato();
        assertEquals(500, result.getImporto().intValue());
    }

    @Test
    void testGetParametroDeserializzato_InvalidTipoAzione() {
        opportunita.setTipoAzione("invalid_azione");
        opportunita.setParametro("{}");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> opportunita.getParametroDeserializzato());

        String expectedMessage = "Tipo azione non riconosciuto: invalid_azione";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}