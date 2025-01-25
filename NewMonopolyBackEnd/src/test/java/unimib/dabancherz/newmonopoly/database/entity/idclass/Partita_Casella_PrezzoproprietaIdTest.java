package unimib.dabancherz.newmonopoly.database.entity.idclass;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class Partita_Casella_PrezzoproprietaIdTest {

    @Test
     void testGetSetIdpartita() {
        Partita_Casella_PrezzoproprietaId id = new Partita_Casella_PrezzoproprietaId();
        id.setIdpartita("game-1");
        assertEquals("game-1", id.getIdpartita());
    }

    @Test
     void testGetSetIdcasella() {
        Partita_Casella_PrezzoproprietaId id = new Partita_Casella_PrezzoproprietaId();
        id.setIdcasella(10);
        assertEquals(10, id.getIdcasella());
    }

    @Test
     void testEquals() {
        Partita_Casella_PrezzoproprietaId id1 = new Partita_Casella_PrezzoproprietaId();
        id1.setIdpartita("game-1");
        id1.setIdcasella(10);

        Partita_Casella_PrezzoproprietaId id2 = new Partita_Casella_PrezzoproprietaId();
        id2.setIdpartita("game-1");
        id2.setIdcasella(10);

        Partita_Casella_PrezzoproprietaId id3 = new Partita_Casella_PrezzoproprietaId();
        id3.setIdpartita("game-2");
        id3.setIdcasella(20);

        Partita_Casella_PrezzoproprietaId id4 = null;

        assertEquals(id1, id2);
        assertNotEquals(id1, id3);
        assertNotEquals(id1, id4);
        assertNotEquals(id1, new Object());
    }

    @Test
     void testHashCode() {
        Partita_Casella_PrezzoproprietaId id1 = new Partita_Casella_PrezzoproprietaId();
        id1.setIdpartita("game-1");
        id1.setIdcasella(10);

        Partita_Casella_PrezzoproprietaId id2 = new Partita_Casella_PrezzoproprietaId();
        id2.setIdpartita("game-1");
        id2.setIdcasella(10);

        Partita_Casella_PrezzoproprietaId id3 = new Partita_Casella_PrezzoproprietaId();
        id3.setIdpartita("game-2");
        id3.setIdcasella(20);

        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), id3.hashCode());
    }
}