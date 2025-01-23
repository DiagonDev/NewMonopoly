package unimib.daBancherz.NewMonopoly.database.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Regolafedelta;

import static org.junit.jupiter.api.Assertions.*;

class RegolafedeltaTest {
    private Regolafedelta regolafedelta;

    @BeforeEach
    void setUp() {
        regolafedelta = new Regolafedelta();
    }

    @Test
    void testIdRegolafedelta() {
        regolafedelta.setIdRegolafedelta(1);
        assertEquals(1, regolafedelta.getIdRegolafedelta());
    }

    @Test
    void testDescrizione() {
        regolafedelta.setDescrizione("Bonus fedeltà");
        assertEquals("Bonus fedeltà", regolafedelta.getDescrizione());
    }

    @Test
    void testTipoCasella() {
        regolafedelta.setTipoCasella("Speciale");
        assertEquals("Speciale", regolafedelta.getTipoCasella());
    }

    @Test
    void testPunti() {
        regolafedelta.setPunti(50);
        assertEquals(50, regolafedelta.getPunti());
    }
}