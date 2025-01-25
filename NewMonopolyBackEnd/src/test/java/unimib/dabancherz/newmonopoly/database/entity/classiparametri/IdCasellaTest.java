package unimib.dabancherz.newmonopoly.database.entity.classiparametri;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class IdCasellaTest {

    @Test
     void testGetSetId_casella() {
        IdCasella idCasella = new IdCasella();
        idCasella.setId_casella(5);
        assertEquals(5, idCasella.getId_casella());
    }
}