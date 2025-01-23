package unimib.daBancherz.NewMonopoly.database.Entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrezzoproprietaTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        Prezzoproprieta prezzoproprieta = new Prezzoproprieta();

        // Act
        prezzoproprieta.setIdPrezzoproprieta(1);
        prezzoproprieta.setAffitto(100);
        prezzoproprieta.setIpoteca(50);
        prezzoproprieta.setCasa(200);
        prezzoproprieta.setAffitto1Casa(150);
        prezzoproprieta.setAffitto2Case(200);
        prezzoproprieta.setAffitto3Case(300);
        prezzoproprieta.setAffitto4Case(400);
        prezzoproprieta.setAffittoAlbergo(500);
        prezzoproprieta.setCostoAcquisto(1000);

        // Assert
        assertEquals(1, prezzoproprieta.getIdPrezzoproprieta());
        assertEquals(100, prezzoproprieta.getAffitto());
        assertEquals(50, prezzoproprieta.getIpoteca());
        assertEquals(200, prezzoproprieta.getCasa());
        assertEquals(150, prezzoproprieta.getAffitto1Casa());
        assertEquals(200, prezzoproprieta.getAffitto2Case());
        assertEquals(300, prezzoproprieta.getAffitto3Case());
        assertEquals(400, prezzoproprieta.getAffitto4Case());
        assertEquals(500, prezzoproprieta.getAffittoAlbergo());
        assertEquals(1000, prezzoproprieta.getCostoAcquisto());
    }

    @Test
    void testDefaultConstructor() {
        // Arrange
        Prezzoproprieta prezzoproprieta = new Prezzoproprieta();

        // Assert
        assertNull(prezzoproprieta.getIdPrezzoproprieta());
        assertNull(prezzoproprieta.getAffitto());
        assertNull(prezzoproprieta.getIpoteca());
        assertNull(prezzoproprieta.getCasa());
        assertNull(prezzoproprieta.getAffitto1Casa());
        assertNull(prezzoproprieta.getAffitto2Case());
        assertNull(prezzoproprieta.getAffitto3Case());
        assertNull(prezzoproprieta.getAffitto4Case());
        assertNull(prezzoproprieta.getAffittoAlbergo());
        assertNull(prezzoproprieta.getCostoAcquisto());
    }
}
