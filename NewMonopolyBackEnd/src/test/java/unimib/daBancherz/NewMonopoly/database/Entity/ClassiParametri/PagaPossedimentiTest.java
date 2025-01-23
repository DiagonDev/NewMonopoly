package unimib.daBancherz.NewMonopoly.database.Entity.ClassiParametri;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PagaPossedimentiTest {

    @Test
    public void testGetSetCostoCasa() {
        PagaPossedimenti pagaPossedimenti = new PagaPossedimenti();
        pagaPossedimenti.setCosto_casa(100);
        assertEquals(100, pagaPossedimenti.getCosto_casa());
    }

    @Test
    public void testGetSetCostoAbergo() {
        PagaPossedimenti pagaPossedimenti = new PagaPossedimenti();
        pagaPossedimenti.setCosto_abergo(200);
        assertEquals(200, pagaPossedimenti.getCosto_abergo());
    }
}