package unimib.daBancherz.NewMonopoly.database.Entity.ClassiParametri;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IdCasellaTest {

    @Test
    public void testGetSetId_casella() {
        IdCasella idCasella = new IdCasella();
        idCasella.setId_casella(5);
        assertEquals(5, idCasella.getId_casella());
    }
}