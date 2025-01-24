package unimib.daBancherz.NewMonopoly.Manager;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class DiceManager {

    private final SecureRandom secureRandom = new SecureRandom();

    public int[] rollDice() {
        int diceR1 = secureRandom.nextInt(6) + 1; // Genera un numero casuale tra 1 e 6
        int diceR2 = secureRandom.nextInt(6) + 1;
        return new int[]{diceR1, diceR2}; // Restituisce entrambi i valori
    }

}

