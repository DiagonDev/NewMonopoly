package unimib.daBancherz.newMonopoly.database.entity.classiParametri;

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