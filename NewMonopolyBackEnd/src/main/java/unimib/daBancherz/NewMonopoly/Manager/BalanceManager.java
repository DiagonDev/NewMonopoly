package unimib.daBancherz.NewMonopoly.Manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Handler.GameHandler;
import unimib.daBancherz.NewMonopoly.Handler.MessageHandler;
import unimib.daBancherz.NewMonopoly.database.Repository.GiocatoreRepository;
import unimib.daBancherz.NewMonopoly.database.Repository.PartitaCasellaPrezzoproprietaRepository;
import unimib.daBancherz.NewMonopoly.model.PlayerProperties;

import java.util.List;
import java.util.Map;

@Component
public class BalanceManager {

    private final PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    private final GiocatoreRepository giocatoreRepository;
    private final MessageHandler messageHandler;
    private final GameHandler gameHandler;

    public BalanceManager(PartitaCasellaPrezzoproprietaRepository pCPPRepository, GiocatoreRepository giocatoreRepository, MessageHandler messageHandler, GameHandler gameHandler) {
        this.pCPPRepository = pCPPRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.messageHandler = messageHandler;
        this.gameHandler = gameHandler;
    }

    public void checkBalance(String gameId, String playerName, WebSocketSession session) throws Exception {
        int saldoG = giocatoreRepository.saldoGiocatore(playerName, gameId);
        if (saldoG < 0)
            handleNegativeBalance(gameId, playerName, session);
        else
            updateProperties(gameId, playerName, session);
        List<WebSocketSession> playersInGame = gameHandler.getGameSessions().get(gameId);
        if(playersInGame.size() == 1){
            WebSocketSession vincitore = playersInGame.get(0);
            String winMessage = new ObjectMapper().writeValueAsString(Map.of(
                    "type", "partitaFinita",
                    "flag", "vittoria",
                    "content", "Hai vinto"
            ));
            vincitore.sendMessage(new TextMessage(winMessage));
        }
    }

    private void handleNegativeBalance(String gameId, String playerName, WebSocketSession session) throws Exception {
        String loseMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "partitaFinita",
                "flag", "sconfitta",
                "content", "Il tuo saldo è negativo"
        ));
        session.sendMessage(new TextMessage(loseMessage));
        messageHandler.sendSystemMessage(gameId, playerName + " ha perso", gameHandler.getGameSessions(), session);
        gameHandler.removePlayerFromGame(gameId, session);
    }

    private void updateProperties(String gameId, String playerName, WebSocketSession session) throws Exception {
        List<PlayerProperties> giocatorePropertiesList = pCPPRepository.findPlayerProperties(gameId, playerName);
        String updateGiocatorePropertiesMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "updateProperties",
                "properties", giocatorePropertiesList
        ));
        session.sendMessage(new TextMessage(updateGiocatorePropertiesMessage));
    }
}