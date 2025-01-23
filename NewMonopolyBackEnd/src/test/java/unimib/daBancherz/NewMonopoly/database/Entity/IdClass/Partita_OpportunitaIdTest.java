package unimib.daBancherz.NewMonopoly.database.Entity.IdClass;
import org.junit.jupiter.api.Test;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.IdClass.Partita_OpportunitaId;

import static org.junit.jupiter.api.Assertions.*;

public class Partita_OpportunitaIdTest {

    @Test
    public void testGetSetIdpartita() {
        Partita_OpportunitaId id = new Partita_OpportunitaId();
        id.setIdpartita("P12345");
        assertEquals("P12345", id.getIdpartita());
    }

    @Test
    public void testGetSetIdopportunita() {
        Partita_OpportunitaId id = new Partita_OpportunitaId();
        id.setIdopportunita(10);
        assertEquals(10, id.getIdopportunita());
    }

    @Test
    public void testEquals() {
        Partita_OpportunitaId id1 = new Partita_OpportunitaId();
        id1.setIdpartita("P12345");
        id1.setIdopportunita(10);

        Partita_OpportunitaId id2 = new Partita_OpportunitaId();
        id2.setIdpartita("P12345");
        id2.setIdopportunita(10);

        Partita_OpportunitaId id3 = new Partita_OpportunitaId();
        id3.setIdpartita("P54321");
        id3.setIdopportunita(20);

        assertEquals(id1, id1);
        assertEquals(id1, id2);

        assertNotEquals(id1, id3);
        assertNotEquals(id1, null);
        assertNotEquals(id1, new Object());
    }

    @Test
    public void testHashCode() {
        Partita_OpportunitaId id1 = new Partita_OpportunitaId();
        id1.setIdpartita("P12345");
        id1.setIdopportunita(10);

        Partita_OpportunitaId id2 = new Partita_OpportunitaId();
        id2.setIdpartita("P12345");
        id2.setIdopportunita(10);

        Partita_OpportunitaId id3 = new Partita_OpportunitaId();
        id3.setIdpartita("P54321");
        id3.setIdopportunita(20);

        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), id3.hashCode());
    }
}