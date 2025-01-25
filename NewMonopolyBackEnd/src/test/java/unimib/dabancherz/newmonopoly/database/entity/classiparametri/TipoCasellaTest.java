package unimib.dabancherz.newmonopoly.database.entity.classiparametri;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class TipoCasellaTest {

    @Test
     void testGetSetTipo_casella() {
        TipoCasella tipoCasella = new TipoCasella();
        tipoCasella.setTipo_casella("Proprietà");
        assertEquals("Proprietà", tipoCasella.getTipo_casella());
    }
}