package unimib.daBancherz.NewMonopoly.Handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.MessageService;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

@Component
public class PawnHandler {

    private final GameHandler gameHandler;
    private final MessageService messageService;
    private final BoxHandler boxHandler;
    private final SecureRandom secureRandom = new SecureRandom();
    GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();

    public PawnHandler(GameHandler gameHandler, MessageService messageService, BoxHandler boxHandler) {
        this.gameHandler = gameHandler;
        this.messageService = messageService;
        this.boxHandler = boxHandler;
    }

    public void movimentoPedina(String gameId, String playerName, int newPosition, Map<String, List<WebSocketSession>> gameSessions, int pawnId, WebSocketSession session, boolean viaPay) throws Exception {
        gameBoard.setPlayerPosition(gameId, playerName, newPosition);//aggiorna la posizione del giocatore
        //invia a tutti i giocatori che il "playername" si è postato di tot caselle "newPosition"
        messageService.sendPawnMove(pawnId, playerName, newPosition, gameHandler.getGameSessions(), gameId);
        //metodo che mostra le opzioni disponibili da fare sulla casella dopo che ci si è finiti sopra
        boxHandler.sendBoxUsage(playerName, session, newPosition, gameId, gameHandler.getGameSessions(), pawnId, viaPay);
    }
}
