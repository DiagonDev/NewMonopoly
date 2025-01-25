package unimib.daBancherz.newMonopoly.database.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class Partita_Casella_PrezzoproprietaTest {

    @Test
     void testGetSetIdpartita() {
        Partita partita = new Partita();
        Partita_Casella_Prezzoproprieta partitaCasellaPrezzo = new Partita_Casella_Prezzoproprieta();
        partitaCasellaPrezzo.setIdpartita(partita);
        assertEquals(partita, partitaCasellaPrezzo.getIdpartita());
    }

    @Test
     void testGetSetIdcasella() {
        Casella casella = new Casella();
        Partita_Casella_Prezzoproprieta partitaCasellaPrezzo = new Partita_Casella_Prezzoproprieta();
        partitaCasellaPrezzo.setIdcasella(casella);
        assertEquals(casella, partitaCasellaPrezzo.getIdcasella());
    }

    @Test
     void testGetSetIdprezzoproprieta() {
        Prezzoproprieta prezzoproprieta = new Prezzoproprieta();
        Partita_Casella_Prezzoproprieta partitaCasellaPrezzo = new Partita_Casella_Prezzoproprieta();
        partitaCasellaPrezzo.setIdprezzoproprieta(prezzoproprieta);
        assertEquals(prezzoproprieta, partitaCasellaPrezzo.getIdprezzoproprieta());
    }

    @Test
     void testGetSetPrezzoCorrente() {
        Partita_Casella_Prezzoproprieta partitaCasellaPrezzo = new Partita_Casella_Prezzoproprieta();
        partitaCasellaPrezzo.setPrezzoCorrente(200);
        assertEquals(200, partitaCasellaPrezzo.getPrezzoCorrente());
    }

    @Test
     void testGetSetPrezzoCasaCorrente() {
        Partita_Casella_Prezzoproprieta partitaCasellaPrezzo = new Partita_Casella_Prezzoproprieta();
        partitaCasellaPrezzo.setPrezzoCasaCorrente(50);
        assertEquals(50, partitaCasellaPrezzo.getPrezzoCasaCorrente());
    }

    @Test
     void testGetSetPosizione() {
        Partita_Casella_Prezzoproprieta partitaCasellaPrezzo = new Partita_Casella_Prezzoproprieta();
        partitaCasellaPrezzo.setPosizione(10);
        assertEquals(10, partitaCasellaPrezzo.getPosizione());
    }

    @Test
     void testGetSetIdgiocatore() {
        Giocatore giocatore = new Giocatore();
        Partita_Casella_Prezzoproprieta partitaCasellaPrezzo = new Partita_Casella_Prezzoproprieta();
        partitaCasellaPrezzo.setIdgiocatore(giocatore);
        assertEquals(giocatore, partitaCasellaPrezzo.getIdgiocatore());
    }

    @Test
     void testGetSetNumCasa() {
        Partita_Casella_Prezzoproprieta partitaCasellaPrezzo = new Partita_Casella_Prezzoproprieta();
        partitaCasellaPrezzo.setNumCasa(3);
        assertEquals(3, partitaCasellaPrezzo.getNumCasa());
    }
}