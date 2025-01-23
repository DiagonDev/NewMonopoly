package unimib.daBancherz.NewMonopoly.database.Entity;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PrezzoproprietaTest {

    @Test
    public void testGetSetIdPrezzoproprieta() {
        Prezzoproprieta prezzoproprieta = new Prezzoproprieta();
        prezzoproprieta.setIdPrezzoproprieta(1);
        assertEquals(1, prezzoproprieta.getIdPrezzoproprieta());
    }

    @Test
    public void testGetSetAffitto() {
        Prezzoproprieta prezzoproprieta = new Prezzoproprieta();
        prezzoproprieta.setAffitto(200);
        assertEquals(200, prezzoproprieta.getAffitto());
    }

    @Test
    public void testGetSetIpoteca() {
        Prezzoproprieta prezzoproprieta = new Prezzoproprieta();
        prezzoproprieta.setIpoteca(100);
        assertEquals(100, prezzoproprieta.getIpoteca());
    }

    @Test
    public void testGetSetCasa() {
        Prezzoproprieta prezzoproprieta = new Prezzoproprieta();
        prezzoproprieta.setCasa(50);
        assertEquals(50, prezzoproprieta.getCasa());
    }

    @Test
    public void testGetSetAffitto1Casa() {
        Prezzoproprieta prezzoproprieta = new Prezzoproprieta();
        prezzoproprieta.setAffitto1Casa(300);
        assertEquals(300, prezzoproprieta.getAffitto1Casa());
    }

    @Test
    public void testGetSetAffitto2Case() {
        Prezzoproprieta prezzoproprieta = new Prezzoproprieta();
        prezzoproprieta.setAffitto2Case(350);
        assertEquals(350, prezzoproprieta.getAffitto2Case());
    }

    @Test
    public void testGetSetAffitto3Case() {
        Prezzoproprieta prezzoproprieta = new Prezzoproprieta();
        prezzoproprieta.setAffitto3Case(400);
        assertEquals(400, prezzoproprieta.getAffitto3Case());
    }

    @Test
    public void testGetSetAffitto4Case() {
        Prezzoproprieta prezzoproprieta = new Prezzoproprieta();
        prezzoproprieta.setAffitto4Case(450);
        assertEquals(450, prezzoproprieta.getAffitto4Case());
    }

    @Test
    public void testGetSetAffittoAlbergo() {
        Prezzoproprieta prezzoproprieta = new Prezzoproprieta();
        prezzoproprieta.setAffittoAlbergo(500);
        assertEquals(500, prezzoproprieta.getAffittoAlbergo());
    }

    @Test
    public void testGetSetCostoAcquisto() {
        Prezzoproprieta prezzoproprieta = new Prezzoproprieta();
        prezzoproprieta.setCostoAcquisto(600);
        assertEquals(600, prezzoproprieta.getCostoAcquisto());
    }
}
