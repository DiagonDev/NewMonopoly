package unimib.dabancherz.newmonopoly.database.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class Partita_RegolafedeltaTest {

    @Test
     void testGetSetIdpartita() {
        Partita partita = new Partita();
        Partita_Regolafedelta partitaRegolafedelta = new Partita_Regolafedelta();
        partitaRegolafedelta.setIdpartita(partita);
        assertEquals(partita, partitaRegolafedelta.getIdpartita());
    }

    @Test
     void testGetSetIdregolafedelta() {
        Regolafedelta regolafedelta = new Regolafedelta();
        Partita_Regolafedelta partitaRegolafedelta = new Partita_Regolafedelta();
        partitaRegolafedelta.setIdregolafedelta(regolafedelta);
        assertEquals(regolafedelta, partitaRegolafedelta.getIdregolafedelta());
    }

    @Test
     void testIsSetUtilizzato() {
        Partita_Regolafedelta partitaRegolafedelta = new Partita_Regolafedelta();
        partitaRegolafedelta.setUtilizzato(true);
        assertTrue(partitaRegolafedelta.isUtilizzato());
    }
}