package unimib.daBancherz.newMonopoly.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.newMonopoly.MessageService;
import unimib.daBancherz.newMonopoly.singleton.GameBoardSingleton;

@Component
public class PawnHandler {

    private final GameHandler gameHandler;
    private final MessageService messageService;
    private final BoxHandler boxHandler;
    GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();

    public PawnHandler(GameHandler gameHandler, MessageService messageService, BoxHandler boxHandler) {
        this.gameHandler = gameHandler;
        this.messageService = messageService;
        this.boxHandler = boxHandler;
    }

    public void movimentoPedina(String gameId, String playerName, int newPosition, int pawnId, WebSocketSession session, boolean viaPay) throws Exception {
        gameBoard.setPlayerPosition(gameId, playerName, newPosition);//aggiorna la posizione del giocatore
        //invia a tutti i giocatori che il "playername" si è postato di tot caselle "newPosition"
        messageService.sendPawnMove(pawnId, playerName, newPosition, gameHandler.getGameSessions(), gameId);
        //metodo che mostra le opzioni disponibili da fare sulla casella dopo che ci si è finiti sopra
        boxHandler.sendBoxUsage(playerName, session, newPosition, gameId, gameHandler.getGameSessions(), pawnId, viaPay);
    }
}
