package unimib.dabancherz.newmonopoly.database.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class Partita_OpportunitaTest {

    @Test
     void testGetSetIdpartita() {
        Partita partita = new Partita();
        Partita_Opportunita partitaOpportunita = new Partita_Opportunita();
        partitaOpportunita.setIdpartita(partita);
        assertEquals(partita, partitaOpportunita.getIdpartita());
    }

    @Test
     void testGetSetIdopportunita() {
        Opportunita opportunita = new Opportunita();
        Partita_Opportunita partitaOpportunita = new Partita_Opportunita();
        partitaOpportunita.setIdopportunita(opportunita);
        assertEquals(opportunita, partitaOpportunita.getIdopportunita());
    }

    @Test
     void testGetSetIdgiocatore() {
        Giocatore giocatore = new Giocatore();
        Partita_Opportunita partitaOpportunita = new Partita_Opportunita();
        partitaOpportunita.setIdgiocatore(giocatore);
        assertEquals(giocatore, partitaOpportunita.getIdgiocatore());
    }

    @Test
     void testIsSetUtilizzato() {
        Partita_Opportunita partitaOpportunita = new Partita_Opportunita();
        partitaOpportunita.setUtilizzato(true);
        assertTrue(partitaOpportunita.isUtilizzato());
    }
}