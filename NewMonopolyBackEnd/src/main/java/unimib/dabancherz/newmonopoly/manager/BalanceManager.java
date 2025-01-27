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
            gameHandler.removePlayerFromGame(gameId, session);
        else
            messageService.updateProperties(gameId, playerName, session);
        List<WebSocketSession> playersInGame = gameHandler.getGameSessions().get(gameId);
        if(playersInGame.size() == 1){
            WebSocketSession sessionVincitore = playersInGame.get(0);
            gameHandler.removePlayerFromGame(gameId, sessionVincitore);
        }
    }
}