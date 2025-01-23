package unimib.daBancherz.NewMonopoly.database.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Pedina;

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