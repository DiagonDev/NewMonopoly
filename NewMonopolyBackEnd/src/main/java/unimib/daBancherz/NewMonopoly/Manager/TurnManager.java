package unimib.daBancherz.NewMonopoly.Manager;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Handler.GameHandler;
import unimib.daBancherz.NewMonopoly.Handler.MessageHandler;
import unimib.daBancherz.NewMonopoly.database.Entity.Partita;
import unimib.daBancherz.NewMonopoly.database.Repository.PartitaRepository;
import unimib.daBancherz.NewMonopoly.model.PlayerProperties;

import java.util.List;

@Component
public class TurnManager {

    private final GameHandler gameHandler;
    private final MessageHandler messageHandler;
    private final PrisonManager prisonManager;
    private final BalanceManager balanceManager;
    private final PartitaRepository partitaRepository;

    public TurnManager(GameHandler gameHandler, MessageHandler messageHandler, PrisonManager prisonManager, BalanceManager balanceManager, PartitaRepository partitaRepository) {
        this.gameHandler = gameHandler;
        this.messageHandler = messageHandler;
        this.prisonManager = prisonManager;
        this.balanceManager = balanceManager;
        this.partitaRepository = partitaRepository;
    }

    public void startTurn(WebSocketSession session) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        Partita partita = partitaRepository.findByCodiceInvito(gameId);
        partita.setStato("Iniziata");
        messageHandler.sendSystemMessage(gameId, "È il turno di: " + playerName, gameHandler.getGameSessions(), session);

        // Notifica ai giocatori
        notifyPlayersTurn(gameId, playerName, session);

        // Gestione del giocatore in prigione
        if (prisonManager.isPlayerInPrison(gameId, playerName)) {
            prisonManager.handlePrisonPlayer(gameId, playerName, session);
        }
    }

    private void notifyPlayersTurn(String gameId, String playerName, WebSocketSession session) throws Exception {
        String yourTurnMessage = messageHandler.createTurnMessage(true, playerName);
        String notYourTurnMessage = messageHandler.createTurnMessage(false, playerName);
        for (WebSocketSession playerSession : gameHandler.getGameSessions().get(gameId)) {
            if (!playerSession.equals(session)) {
                playerSession.sendMessage(new TextMessage(notYourTurnMessage));
            } else {
                session.sendMessage(new TextMessage(yourTurnMessage));
            }
        }
    }

    public void endTurn(WebSocketSession session) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        balanceManager.checkBalance(gameId, playerName, session);

        // Calcola l'indice della prossima sessione in modo circolare
        List<WebSocketSession> playersInGame = gameHandler.getGameSessions().get(gameId);
        int nextIndex = (playersInGame.indexOf(session) + 1) % playersInGame.size();
        List<PlayerProperties> giocatorePropertiesList;
        WebSocketSession nextPlayer = playersInGame.get(nextIndex);
        startTurn(nextPlayer);
    }
}