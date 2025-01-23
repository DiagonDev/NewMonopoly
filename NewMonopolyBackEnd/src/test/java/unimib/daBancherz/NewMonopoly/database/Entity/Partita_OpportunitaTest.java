package unimib.daBancherz.NewMonopoly.database.Entity;

import org.junit.jupiter.api.Test;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.*;
import static org.junit.jupiter.api.Assertions.*;

public class Partita_OpportunitaTest {

    @Test
    public void testGetSetIdpartita() {
        Partita partita = new Partita();
        Partita_Opportunita partitaOpportunita = new Partita_Opportunita();
        partitaOpportunita.setIdpartita(partita);
        assertEquals(partita, partitaOpportunita.getIdpartita());
    }

    @Test
    public void testGetSetIdopportunita() {
        Opportunita opportunita = new Opportunita();
        Partita_Opportunita partitaOpportunita = new Partita_Opportunita();
        partitaOpportunita.setIdopportunita(opportunita);
        assertEquals(opportunita, partitaOpportunita.getIdopportunita());
    }

    @Test
    public void testGetSetIdgiocatore() {
        Giocatore giocatore = new Giocatore();
        Partita_Opportunita partitaOpportunita = new Partita_Opportunita();
        partitaOpportunita.setIdgiocatore(giocatore);
        assertEquals(giocatore, partitaOpportunita.getIdgiocatore());
    }

    @Test
    public void testIsSetUtilizzato() {
        Partita_Opportunita partitaOpportunita = new Partita_Opportunita();
        partitaOpportunita.setUtilizzato(true);
        assertTrue(partitaOpportunita.isUtilizzato());
    }
}