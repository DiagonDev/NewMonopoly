package unimib.daBancherz.NewMonopoly.database.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Giocatore;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Partita;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Pedina;

class GiocatoreTest {
    private Giocatore giocatore;

    @BeforeEach
    void setUp() {
        giocatore = new Giocatore();
    }

    @Test
    void testIdGiocatore() {
        giocatore.setIdGiocatore(1);
        assertEquals(1, giocatore.getIdGiocatore());
    }

    @Test
    void testNome() {
        giocatore.setNome("Mario");
        assertEquals("Mario", giocatore.getNome());
    }

    @Test
    void testSaldo() {
        giocatore.setSaldo(1000);
        assertEquals(1000, giocatore.getSaldo());
    }

    @Test
    void testPuntiFedelta() {
        giocatore.setPuntiFedelta(200);
        assertEquals(200, giocatore.getPuntiFedelta());
    }

    @Test
    void testTipo() {
        giocatore.setTipo("VIP");
        assertEquals("VIP", giocatore.getTipo());
    }

    @Test
    void testIdPedina() {
        Pedina pedina = Mockito.mock(Pedina.class);
        giocatore.setIdpedina(pedina);
        assertEquals(pedina, giocatore.getIdpedina());
    }

    @Test
    void testIdPartita() {
        Partita partita = Mockito.mock(Partita.class);
        giocatore.setIdpartita(partita);
        assertEquals(partita, giocatore.getIdpartita());
    }
}
