package unimib.daBancherz.newMonopoly.database.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class CasellaTest {

    @Test
     void testGetSetIdCasella() {
        Casella casella = new Casella();
        casella.setIdCasella(1);
        assertEquals(1, casella.getIdCasella());
    }

    @Test
     void testGetSetNome() {
        Casella casella = new Casella();
        casella.setNome("Via");
        assertEquals("Via", casella.getNome());
    }

    @Test
     void testGetSetColore() {
        Casella casella = new Casella();
        casella.setColore("Rosso");
        assertEquals("Rosso", casella.getColore());
    }

    @Test
     void testGetSetTipo() {
        Casella casella = new Casella();
        casella.setTipo("Proprietà");
        assertEquals("Proprietà", casella.getTipo());
    }
}