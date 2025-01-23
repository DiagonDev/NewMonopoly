package unimib.daBancherz.NewMonopoly.database.Entity;

import org.junit.jupiter.api.Test;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Partita;

import static org.junit.jupiter.api.Assertions.*;

public class PartitaTest {

    @Test
    public void testGetSetCodiceInvito() {
        Partita partita = new Partita();
        partita.setCodiceInvito("ABC123");
        assertEquals("ABC123", partita.getCodiceInvito());
    }

    @Test
    public void testGetSetLivelloDifficolta() {
        Partita partita = new Partita();
        partita.setLivelloDifficolta("Facile");
        assertEquals("Facile", partita.getLivelloDifficolta());
    }

    @Test
    public void testGetSetStato() {
        Partita partita = new Partita();
        partita.setStato("In corso");
        assertEquals("In corso", partita.getStato());
    }

    @Test
    public void testGetSetRandomizzazione() {
        Partita partita = new Partita();
        partita.setRandomizzazione(true);
        assertEquals(true, partita.getRandomizzazione());
    }
}