package unimib.dabancherz.newmonopoly.database.entity.idclass;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class Partita_OpportunitaIdTest {

    @Test
     void testGetSetIdpartita() {
        Partita_OpportunitaId id = new Partita_OpportunitaId();
        id.setIdpartita("game-1");
        assertEquals("game-1", id.getIdpartita());
    }

    @Test
     void testGetSetIdopportunita() {
        Partita_OpportunitaId id = new Partita_OpportunitaId();
        id.setIdopportunita(10);
        assertEquals(10, id.getIdopportunita());
    }

    @Test
     void testEquals() {
        Partita_OpportunitaId id1 = new Partita_OpportunitaId();
        id1.setIdpartita("game-1");
        id1.setIdopportunita(10);

        Partita_OpportunitaId id2 = new Partita_OpportunitaId();
        id2.setIdpartita("game-1");
        id2.setIdopportunita(10);

        Partita_OpportunitaId id3 = new Partita_OpportunitaId();
        id3.setIdpartita("game-2");
        id3.setIdopportunita(20);

        Partita_OpportunitaId id4 = null;

        assertEquals(id1, id2);
        assertNotEquals(id1, id3);
        assertNotEquals(id1, id4);
        assertNotEquals(id1, new Object());
    }

    @Test
     void testHashCode() {
        Partita_OpportunitaId id1 = new Partita_OpportunitaId();
        id1.setIdpartita("game-1");
        id1.setIdopportunita(10);

        Partita_OpportunitaId id2 = new Partita_OpportunitaId();
        id2.setIdpartita("game-1");
        id2.setIdopportunita(10);

        Partita_OpportunitaId id3 = new Partita_OpportunitaId();
        id3.setIdpartita("game-2");
        id3.setIdopportunita(20);

        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), id3.hashCode());
    }
}