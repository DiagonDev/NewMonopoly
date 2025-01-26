package unimib.dabancherz.newmonopoly;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import unimib.daBancherz.NewMonopoly.NewMonopolyApplication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class NewMonopolyApplicationTests {

    @Test
    void testMain() {
        // Verifica che il metodo main non lanci eccezioni
        assertDoesNotThrow(() -> NewMonopolyApplication.main(new String[] {}));
    }
}