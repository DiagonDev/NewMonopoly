package unimib.daBancherz.newMonopoly.database.entity.idClass;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Partita_RegolafedeltaIdTest {

    @Test
    public void testGetSetIdpartita() {
        Partita_RegolafedeltaId id = new Partita_RegolafedeltaId();
        id.setIdpartita("game-1");
        assertEquals("game-1", id.getIdpartita());
    }

    @Test
    public void testGetSetIdregolafedelta() {
        Partita_RegolafedeltaId id = new Partita_RegolafedeltaId();
        id.setIdregolafedelta(10);
        assertEquals(10, id.getIdregolafedelta());
    }

    @Test
    public void testEquals() {
        Partita_RegolafedeltaId id1 = new Partita_RegolafedeltaId();
        id1.setIdpartita("game-1");
        id1.setIdregolafedelta(10);

        Partita_RegolafedeltaId id2 = new Partita_RegolafedeltaId();
        id2.setIdpartita("game-1");
        id2.setIdregolafedelta(10);

        Partita_RegolafedeltaId id3 = new Partita_RegolafedeltaId();
        id3.setIdpartita("game-2");
        id3.setIdregolafedelta(20);

        Partita_RegolafedeltaId id4 = null;

        assertEquals(id1, id2);
        assertNotEquals(id1, id3);
        assertNotEquals(id1, id4);
        assertNotEquals(id1, new Object());
    }

    @Test
    public void testHashCode() {
        Partita_RegolafedeltaId id1 = new Partita_RegolafedeltaId();
        id1.setIdpartita("game-1");
        id1.setIdregolafedelta(10);

        Partita_RegolafedeltaId id2 = new Partita_RegolafedeltaId();
        id2.setIdpartita("game-1");
        id2.setIdregolafedelta(10);

        Partita_RegolafedeltaId id3 = new Partita_RegolafedeltaId();
        id3.setIdpartita("game-2");
        id3.setIdregolafedelta(20);

        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), id3.hashCode());
    }
}