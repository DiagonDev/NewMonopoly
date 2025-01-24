package unimib.daBancherz.NewMonopoly.database.Entity;

import org.junit.jupiter.api.Test;
import unimib.daBancherz.NewMonopoly.database.Entity.ClassiParametri.*;

import static org.junit.jupiter.api.Assertions.*;

public class OpportunitaTest {

    @Test
    public void testGetSetIdOpportunita() {
        Opportunita opportunita = new Opportunita();
        opportunita.setIdOpportunita(1);
        assertEquals(1, opportunita.getIdOpportunita());
    }

    @Test
    public void testGetSetDescrizione() {
        Opportunita opportunita = new Opportunita();
        opportunita.setDescrizione("Descrizione Test");
        assertEquals("Descrizione Test", opportunita.getDescrizione());
    }

    @Test
    public void testGetSetTipoAzione() {
        Opportunita opportunita = new Opportunita();
        opportunita.setTipoAzione("vai_in_prigione");
        assertEquals("vai_in_prigione", opportunita.getTipoAzione());
    }

    @Test
    public void testGetSetParametro() {
        Opportunita opportunita = new Opportunita();
        opportunita.setParametro("{\"id_casella\": 5}");
        assertEquals("{\"id_casella\": 5}", opportunita.getParametro());
    }

    @Test
    public void testGetSetTipo() {
        Opportunita opportunita = new Opportunita();
        opportunita.setTipo("Tipo Test");
        assertEquals("Tipo Test", opportunita.getTipo());
    }

    @Test
    public void testGetParametroDeserializzato_EsciPrigione() throws Exception {
        Opportunita opportunita = new Opportunita();
        opportunita.setTipoAzione("esci_prigione");
        opportunita.setParametro("{}");

        Object parametroDeserializzato = opportunita.getParametroDeserializzato();
        assertNull(parametroDeserializzato);
    }

    @Test
    public void testGetParametroDeserializzato_IdCasella() throws Exception {
        Opportunita opportunita = new Opportunita();
        opportunita.setTipoAzione("sposta_avanti");
        opportunita.setParametro("{\"id_casella\": 5}");

        Object parametroDeserializzato = opportunita.getParametroDeserializzato();
        assertTrue(parametroDeserializzato instanceof IdCasella);
        assertEquals(5, ((IdCasella) parametroDeserializzato).getId_casella());
    }

    @Test
    public void testGetParametroDeserializzato_TipoCasella() throws Exception {
        Opportunita opportunita = new Opportunita();
        opportunita.setTipoAzione("sposta_avanti");
        opportunita.setParametro("{\"tipo_casella\": \"Proprieta\"}");

        Object parametroDeserializzato = opportunita.getParametroDeserializzato();
        assertTrue(parametroDeserializzato instanceof TipoCasella);
        assertEquals("Proprieta", ((TipoCasella) parametroDeserializzato).getTipo_casella());
    }

    @Test
    public void testGetParametroDeserializzato_PagaPossedimenti() throws Exception {
        Opportunita opportunita = new Opportunita();
        opportunita.setTipoAzione("paga_possedimenti");
        opportunita.setParametro("{\"costo_casa\": 100, \"costo_abergo\": 200}");

        Object parametroDeserializzato = opportunita.getParametroDeserializzato();
        assertTrue(parametroDeserializzato instanceof PagaPossedimenti);
        assertEquals(100, ((PagaPossedimenti) parametroDeserializzato).getCosto_casa());
        assertEquals(200, ((PagaPossedimenti) parametroDeserializzato).getCosto_albergo());
    }

    @Test
    public void testGetParametroDeserializzato_Importo() throws Exception {
        Opportunita opportunita = new Opportunita();
        opportunita.setTipoAzione("ricevi_importo");
        opportunita.setParametro("{\"importo\": 200}");

        Object parametroDeserializzato = opportunita.getParametroDeserializzato();
        assertTrue(parametroDeserializzato instanceof Importo);
        assertEquals(200, ((Importo) parametroDeserializzato).getImporto());
    }

    @Test
    public void testGetParametroDeserializzato_InvalidTipoAzione() {
        Opportunita opportunita = new Opportunita();
        opportunita.setTipoAzione("tipo_azione_non_valido");
        opportunita.setParametro("{\"param\": \"value\"}");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            opportunita.getParametroDeserializzato();
        });

        String expectedMessage = "Tipo azione non riconosciuto: tipo_azione_non_valido";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}