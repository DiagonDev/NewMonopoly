package unimib.daBancherz.newMonopoly.database.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CasellaTest {

    @Test
    public void testGetSetIdCasella() {
        Casella casella = new Casella();
        casella.setIdCasella(1);
        assertEquals(1, casella.getIdCasella());
    }

    @Test
    public void testGetSetNome() {
        Casella casella = new Casella();
        casella.setNome("Via");
        assertEquals("Via", casella.getNome());
    }

    @Test
    public void testGetSetColore() {
        Casella casella = new Casella();
        casella.setColore("Rosso");
        assertEquals("Rosso", casella.getColore());
    }

    @Test
    public void testGetSetTipo() {
        Casella casella = new Casella();
        casella.setTipo("Proprietà");
        assertEquals("Proprietà", casella.getTipo());
    }
}