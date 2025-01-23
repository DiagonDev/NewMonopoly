package unimib.daBancherz.NewMonopoly.database.Entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegolafedeltaTest {

    @Test
    public void testGetAndSetIdRegolafedelta() {
        Regolafedelta regolafedelta = new Regolafedelta();
        regolafedelta.setIdRegolafedelta(1);
        assertEquals(1, regolafedelta.getIdRegolafedelta());
    }

    @Test
    public void testGetAndSetDescrizione() {
        Regolafedelta regolafedelta = new Regolafedelta();
        regolafedelta.setDescrizione("Test descrizione");
        assertEquals("Test descrizione", regolafedelta.getDescrizione());
    }

    @Test
    public void testGetAndSetTipoCasella() {
        Regolafedelta regolafedelta = new Regolafedelta();
        regolafedelta.setTipoCasella("Test tipo casella");
        assertEquals("Test tipo casella", regolafedelta.getTipoCasella());
    }

    @Test
    public void testGetAndSetPunti() {
        Regolafedelta regolafedelta = new Regolafedelta();
        regolafedelta.setPunti(10);
        assertEquals(10, regolafedelta.getPunti());
    }
}