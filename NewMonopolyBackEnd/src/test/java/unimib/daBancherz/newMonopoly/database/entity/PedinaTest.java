package unimib.daBancherz.newMonopoly.database.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PedinaTest {
    private Pedina pedina;

    @BeforeEach
    void setUp() {
        pedina = new Pedina();
    }

    @Test
    void testIdPedina() {
        pedina.setIdPedina(1);
        assertEquals(1, pedina.getIdPedina());
    }

    @Test
    void testNome() {
        pedina.setNome("Cavallo");
        assertEquals("Cavallo", pedina.getNome());
    }
}