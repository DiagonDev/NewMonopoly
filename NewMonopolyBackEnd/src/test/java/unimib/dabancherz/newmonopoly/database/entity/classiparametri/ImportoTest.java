package unimib.dabancherz.newmonopoly.database.entity.classiparametri;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class ImportoTest {

    @Test
     void testGetSetImporto() {
        Importo importo = new Importo();
        importo.setImporto(100);
        assertEquals(100, importo.getImporto());
    }
}