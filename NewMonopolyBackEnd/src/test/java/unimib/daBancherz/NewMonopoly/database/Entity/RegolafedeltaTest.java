package unimib.daBancherz.NewMonopoly.database.Entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RegolafedeltaTest {

    @Test
    public void testGetSetIdRegolafedelta() {
        Regolafedelta regolafedelta = new Regolafedelta();
        regolafedelta.setIdRegolafedelta(1);
        assertEquals(1, regolafedelta.getIdRegolafedelta());
    }

    @Test
    public void testGetSetDescrizione() {
        Regolafedelta regolafedelta = new Regolafedelta();
        regolafedelta.setDescrizione("Test descrizione");
        assertEquals("Test descrizione", regolafedelta.getDescrizione());
    }

    @Test
    public void testGetSetTipoCasella() {
        Regolafedelta regolafedelta = new Regolafedelta();
        regolafedelta.setTipoCasella("Test tipo casella");
        assertEquals("Test tipo casella", regolafedelta.getTipoCasella());
    }

    @Test
    public void testGetSetPunti() {
        Regolafedelta regolafedelta = new Regolafedelta();
        regolafedelta.setPunti(100);
        assertEquals(100, regolafedelta.getPunti());
    }
}