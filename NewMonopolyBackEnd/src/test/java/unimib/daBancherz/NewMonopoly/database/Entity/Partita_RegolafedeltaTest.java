package unimib.daBancherz.NewMonopoly.database.Entity;

import org.junit.jupiter.api.Test;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.*;

import static org.junit.jupiter.api.Assertions.*;

public class Partita_RegolafedeltaTest {

    @Test
    public void testGetSetIdpartita() {
        Partita partita = new Partita();
        Partita_Regolafedelta partitaRegolafedelta = new Partita_Regolafedelta();
        partitaRegolafedelta.setIdpartita(partita);
        assertEquals(partita, partitaRegolafedelta.getIdpartita());
    }

    @Test
    public void testGetSetIdregolafedelta() {
        Regolafedelta regolafedelta = new Regolafedelta();
        Partita_Regolafedelta partitaRegolafedelta = new Partita_Regolafedelta();
        partitaRegolafedelta.setIdregolafedelta(regolafedelta);
        assertEquals(regolafedelta, partitaRegolafedelta.getIdregolafedelta());
    }

    @Test
    public void testIsSetUtilizzato() {
        Partita_Regolafedelta partitaRegolafedelta = new Partita_Regolafedelta();
        partitaRegolafedelta.setUtilizzato(true);
        assertTrue(partitaRegolafedelta.isUtilizzato());
    }
}