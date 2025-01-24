package unimib.daBancherz.NewMonopoly.Manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Handler.MessageHandler;

import java.security.SecureRandom;
import java.util.Map;

@Component
public class DiceManager {

    private final SecureRandom secureRandom = new SecureRandom();

    public int[] rollDice() {
        int diceR1 = secureRandom.nextInt(6) + 1; // Genera un numero casuale tra 1 e 6
        int diceR2 = secureRandom.nextInt(6) + 1;
        return new int[]{diceR1, diceR2}; // Restituisce entrambi i valori
    }

}

