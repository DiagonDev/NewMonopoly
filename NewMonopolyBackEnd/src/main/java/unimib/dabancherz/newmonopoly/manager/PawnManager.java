package unimib.dabancherz.newmonopoly.manager;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.handler.GameHandler;
import unimib.dabancherz.newmonopoly.singleton.GameBoardSingleton;

@Component
public class PawnManager {

    private final GameHandler gameHandler;
    private final MessageService messageService;
    private final BoxManager boxManager;
    GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();

    public PawnManager(GameHandler gameHandler, MessageService messageService, BoxManager boxManager) {
        this.gameHandler = gameHandler;
        this.messageService = messageService;
        this.boxManager = boxManager;
    }

    public void movimentoPedina(String gameId, String playerName, int newPosition, int pawnId, WebSocketSession session, boolean viaPay) throws Exception {
        gameBoard.setPlayerPosition(gameId, playerName, newPosition);//aggiorna la posizione del giocatore
        //invia a tutti i giocatori che il "playername" si è postato di tot caselle "newPosition"
        messageService.sendPawnMove(pawnId, playerName, newPosition, gameHandler.getGameSessions(), gameId);
        //metodo che mostra le opzioni disponibili da fare sulla casella dopo che ci si è finiti sopra
        boxManager.sendBoxUsage(playerName, session, newPosition, gameId, gameHandler.getGameSessions(), pawnId, viaPay);
    }
}
