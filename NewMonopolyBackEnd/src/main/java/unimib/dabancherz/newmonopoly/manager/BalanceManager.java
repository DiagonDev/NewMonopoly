package unimib.dabancherz.newmonopoly.manager;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.handler.GameHandler;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.database.repository.GiocatoreRepository;

import java.util.List;

@Component
public class BalanceManager {

    private final GiocatoreRepository giocatoreRepository;
    private final MessageService messageService;
    private final GameHandler gameHandler;

    public BalanceManager(GiocatoreRepository giocatoreRepository, MessageService messageService, GameHandler gameHandler) {
        this.giocatoreRepository = giocatoreRepository;
        this.messageService = messageService;
        this.gameHandler = gameHandler;
    }

    public void checkBalance(String gameId, String playerName, WebSocketSession session) throws Exception {
        int saldoG = giocatoreRepository.saldoGiocatore(playerName, gameId);
        if (saldoG < 0)
            handleNegativeBalance(session);
        else
            messageService.updateProperties(gameId, playerName, session);

        checkWin(gameId
        );
    }

    public void handleNegativeBalance( WebSocketSession session) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        messageService.sendLoseMessage(session);
        messageService.sendSystemMessage(gameId, playerName + " ha perso", gameHandler.getGameSessions(), session);
        gameHandler.removePlayerFromGame(gameId, session);
    }

    public void handleWinPlayer(String gameId, String vincitore, WebSocketSession session) throws Exception {
        messageService.sendSystemMessage(gameId, vincitore + " ha vinto la partita", gameHandler.getGameSessions(), session);
        messageService.sendVictoryMessage(session);
        gameHandler.removePlayerFromGame(gameId, session);
    }

    public void checkWin (String gameId) throws Exception {
        List<WebSocketSession> playersInGame = gameHandler.getGameSessions().get(gameId);
        if(playersInGame.size() == 1){
            WebSocketSession sessionVincitore = playersInGame.get(0);
            String vincitore = gameHandler.getPlayerNameBySession(sessionVincitore);
            handleWinPlayer(gameId, vincitore, sessionVincitore);
        }
    }
}