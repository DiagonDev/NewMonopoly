package unimib.daBancherz.NewMonopoly.database.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Prezzoproprieta;

import static org.junit.jupiter.api.Assertions.*;

class PrezzoproprietaTest {
    private Prezzoproprieta prezzoproprieta;

    @BeforeEach
    void setUp() {
        prezzoproprieta = new Prezzoproprieta();
    }

    @Test
    void testCostoAcquisto() {
        prezzoproprieta.setCostoAcquisto(1000);
        assertEquals(1000, prezzoproprieta.getCostoAcquisto());
    }
}
