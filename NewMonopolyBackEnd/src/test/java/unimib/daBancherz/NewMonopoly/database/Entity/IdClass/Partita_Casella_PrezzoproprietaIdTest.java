package unimib.daBancherz.NewMonopoly.database.Entity.IdClass;
import org.junit.jupiter.api.Test;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.IdClass.Partita_Casella_PrezzoproprietaId;

import static org.junit.jupiter.api.Assertions.*;

public class Partita_Casella_PrezzoproprietaIdTest {

    @Test
    public void testGetSetIdpartita() {
        Partita_Casella_PrezzoproprietaId id = new Partita_Casella_PrezzoproprietaId();
        id.setIdpartita("P12345");
        assertEquals("P12345", id.getIdpartita());
    }

    @Test
    public void testGetSetIdcasella() {
        Partita_Casella_PrezzoproprietaId id = new Partita_Casella_PrezzoproprietaId();
        id.setIdcasella(10);
        assertEquals(10, id.getIdcasella());
    }

    @Test
    public void testEquals() {
        Partita_Casella_PrezzoproprietaId id1 = new Partita_Casella_PrezzoproprietaId();
        id1.setIdpartita("P12345");
        id1.setIdcasella(10);

        Partita_Casella_PrezzoproprietaId id2 = new Partita_Casella_PrezzoproprietaId();
        id2.setIdpartita("P12345");
        id2.setIdcasella(10);

        Partita_Casella_PrezzoproprietaId id3 = new Partita_Casella_PrezzoproprietaId();
        id3.setIdpartita("P54321");
        id3.setIdcasella(20);

        assertEquals(id1, id2);
        assertNotEquals(id1, id3);
    }

    @Test
    public void testHashCode() {
        Partita_Casella_PrezzoproprietaId id1 = new Partita_Casella_PrezzoproprietaId();
        id1.setIdpartita("P12345");
        id1.setIdcasella(10);

        Partita_Casella_PrezzoproprietaId id2 = new Partita_Casella_PrezzoproprietaId();
        id2.setIdpartita("P12345");
        id2.setIdcasella(10);

        Partita_Casella_PrezzoproprietaId id3 = new Partita_Casella_PrezzoproprietaId();
        id3.setIdpartita("P54321");
        id3.setIdcasella(20);

        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), id3.hashCode());
    }
}