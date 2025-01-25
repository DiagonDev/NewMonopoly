package unimib.daBancherz.newMonopoly.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerPropertiesTest {

    @Test
    public void testGetSetPrezzoCorrente() {
        PlayerProperties playerProperties = new PlayerProperties();
        playerProperties.setPrezzoCorrente(100);
        assertEquals(100, playerProperties.getPrezzoCorrente());
    }

    @Test
    public void testGetSetIdGiocatore() {
        PlayerProperties playerProperties = new PlayerProperties();
        playerProperties.setIdGiocatore(1);
        assertEquals(1, playerProperties.getIdGiocatore());
    }

    @Test
    public void testGetSetNumCasa() {
        PlayerProperties playerProperties = new PlayerProperties();
        playerProperties.setNumCasa(3);
        assertEquals(3, playerProperties.getNumCasa());
    }

    @Test
    public void testGetSetPrezzoCasaCorrente() {
        PlayerProperties playerProperties = new PlayerProperties();
        playerProperties.setPrezzoCasaCorrente(200);
        assertEquals(200, playerProperties.getPrezzoCasaCorrente());
    }

    @Test
    public void testGetSetNome() {
        PlayerProperties playerProperties = new PlayerProperties();
        playerProperties.setNome("Test Nome");
        assertEquals("Test Nome", playerProperties.getNome());
    }

    @Test
    public void testGetSetColore() {
        PlayerProperties playerProperties = new PlayerProperties();
        playerProperties.setColore("Rosso");
        assertEquals("Rosso", playerProperties.getColore());
    }

    @Test
    public void testGetSetAffitto() {
        PlayerProperties playerProperties = new PlayerProperties();
        playerProperties.setAffitto(50);
        assertEquals(50, playerProperties.getAffitto());
    }

    @Test
    public void testGetSetIpoteca() {
        PlayerProperties playerProperties = new PlayerProperties();
        playerProperties.setIpoteca(75);
        assertEquals(75, playerProperties.getIpoteca());
    }

    @Test
    public void testGetSetAffitto1Casa() {
        PlayerProperties playerProperties = new PlayerProperties();
        playerProperties.setAffitto1Casa(60);
        assertEquals(60, playerProperties.getAffitto1Casa());
    }

    @Test
    public void testGetSetAffitto2Case() {
        PlayerProperties playerProperties = new PlayerProperties();
        playerProperties.setAffitto2Case(70);
        assertEquals(70, playerProperties.getAffitto2Case());
    }

    @Test
    public void testGetSetAffitto3Case() {
        PlayerProperties playerProperties = new PlayerProperties();
        playerProperties.setAffitto3Case(80);
        assertEquals(80, playerProperties.getAffitto3Case());
    }

    @Test
    public void testGetSetAffitto4Case() {
        PlayerProperties playerProperties = new PlayerProperties();
        playerProperties.setAffitto4Case(90);
        assertEquals(90, playerProperties.getAffitto4Case());
    }

    @Test
    public void testGetSetAffittoAlbergo() {
        PlayerProperties playerProperties = new PlayerProperties();
        playerProperties.setAffittoAlbergo(100);
        assertEquals(100, playerProperties.getAffittoAlbergo());
    }

    @Test
    public void testAllArgsConstructor() {
        PlayerProperties playerProperties = new PlayerProperties(100, 1, 3, 200, "Test Nome", "Rosso", 50, 60, 70, 80, 90, 100, 75);
        assertEquals(100, playerProperties.getPrezzoCorrente());
        assertEquals(1, playerProperties.getIdGiocatore());
        assertEquals(3, playerProperties.getNumCasa());
        assertEquals(200, playerProperties.getPrezzoCasaCorrente());
        assertEquals("Test Nome", playerProperties.getNome());
        assertEquals("Rosso", playerProperties.getColore());
        assertEquals(50, playerProperties.getAffitto());
        assertEquals(60, playerProperties.getAffitto1Casa());
        assertEquals(70, playerProperties.getAffitto2Case());
        assertEquals(80, playerProperties.getAffitto3Case());
        assertEquals(90, playerProperties.getAffitto4Case());
        assertEquals(100, playerProperties.getAffittoAlbergo());
        assertEquals(75, playerProperties.getIpoteca());
    }
}