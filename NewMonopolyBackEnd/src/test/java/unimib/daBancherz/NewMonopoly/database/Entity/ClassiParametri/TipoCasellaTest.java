package unimib.daBancherz.NewMonopoly.database.Entity.ClassiParametri;

import org.junit.jupiter.api.Test;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.TipoCasella;

import static org.junit.jupiter.api.Assertions.*;

public class TipoCasellaTest {

    @Test
    public void testGetSetTipo_casella() {
        TipoCasella tipoCasella = new TipoCasella();
        tipoCasella.setTipo_casella("Proprietà");
        assertEquals("Proprietà", tipoCasella.getTipo_casella());
    }
}