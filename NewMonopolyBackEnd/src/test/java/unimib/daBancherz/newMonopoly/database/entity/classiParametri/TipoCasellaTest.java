package unimib.daBancherz.newMonopoly.database.entity.classiParametri;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TipoCasellaTest {

    @Test
    public void testGetSetTipo_casella() {
        TipoCasella tipoCasella = new TipoCasella();
        tipoCasella.setTipo_casella("Proprietà");
        assertEquals("Proprietà", tipoCasella.getTipo_casella());
    }
}