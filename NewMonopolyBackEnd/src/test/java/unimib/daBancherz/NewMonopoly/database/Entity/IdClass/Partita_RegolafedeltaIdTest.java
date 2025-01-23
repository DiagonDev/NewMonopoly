package unimib.daBancherz.NewMonopoly.database.Entity.IdClass;
import org.junit.jupiter.api.Test;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.IdClass.Partita_RegolafedeltaId;

import static org.junit.jupiter.api.Assertions.*;

public class Partita_RegolafedeltaIdTest {

    @Test
    public void testGetSetIdpartita() {
        Partita_RegolafedeltaId id = new Partita_RegolafedeltaId();
        id.setIdpartita("P12345");
        assertEquals("P12345", id.getIdpartita());
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
        id1.setIdpartita("P12345");
        id1.setIdregolafedelta(10);

        Partita_RegolafedeltaId id2 = new Partita_RegolafedeltaId();
        id2.setIdpartita("P12345");
        id2.setIdregolafedelta(10);

        Partita_RegolafedeltaId id3 = new Partita_RegolafedeltaId();
        id3.setIdpartita("P54321");
        id3.setIdregolafedelta(20);

        assertEquals(id1, id2);
        assertNotEquals(id1, id3);
    }

    @Test
    public void testHashCode() {
        Partita_RegolafedeltaId id1 = new Partita_RegolafedeltaId();
        id1.setIdpartita("P12345");
        id1.setIdregolafedelta(10);

        Partita_RegolafedeltaId id2 = new Partita_RegolafedeltaId();
        id2.setIdpartita("P12345");
        id2.setIdregolafedelta(10);

        Partita_RegolafedeltaId id3 = new Partita_RegolafedeltaId();
        id3.setIdpartita("P54321");
        id3.setIdregolafedelta(20);

        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), id3.hashCode());
    }
}