package unimib.daBancherz.NewMonopoly.database.Entity.ClassiParametri;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ImportoTest {

    @Test
    public void testGetSetImporto() {
        Importo importo = new Importo();
        importo.setImporto(100);
        assertEquals(100, importo.getImporto());
    }
}