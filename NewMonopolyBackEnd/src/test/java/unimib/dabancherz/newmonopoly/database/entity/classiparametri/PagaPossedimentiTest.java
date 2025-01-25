package unimib.dabancherz.newmonopoly.database.entity.classiparametri;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class PagaPossedimentiTest {

    @Test
     void testGetSetCostoCasa() {
        PagaPossedimenti pagaPossedimenti = new PagaPossedimenti();
        pagaPossedimenti.setCosto_casa(100);
        assertEquals(100, pagaPossedimenti.getCosto_casa());
    }

    @Test
     void testGetSetCostoAbergo() {
        PagaPossedimenti pagaPossedimenti = new PagaPossedimenti();
        pagaPossedimenti.setCosto_albergo(200);
        assertEquals(200, pagaPossedimenti.getCosto_albergo());
    }
}