package unimib.daBancherz.newMonopoly.database.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class PartitaTest {

    @Test
     void testGetSetCodiceInvito() {
        Partita partita = new Partita();
        partita.setCodiceInvito("ABC123");
        assertEquals("ABC123", partita.getCodiceInvito());
    }

    @Test
     void testGetSetLivelloDifficolta() {
        Partita partita = new Partita();
        partita.setLivelloDifficolta("Facile");
        assertEquals("Facile", partita.getLivelloDifficolta());
    }

    @Test
     void testGetSetStato() {
        Partita partita = new Partita();
        partita.setStato("In corso");
        assertEquals("In corso", partita.getStato());
    }

    @Test
     void testGetSetRandomizzazione() {
        Partita partita = new Partita();
        partita.setRandomizzazione(true);
        assertEquals(true, partita.getRandomizzazione());
    }
}