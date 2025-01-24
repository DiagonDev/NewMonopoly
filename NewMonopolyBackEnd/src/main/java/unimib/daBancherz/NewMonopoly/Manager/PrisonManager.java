package unimib.daBancherz.NewMonopoly.Manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Handler.GameHandler;
import unimib.daBancherz.NewMonopoly.Handler.MessageHandler;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;
import unimib.daBancherz.NewMonopoly.database.Repository.GiocatoreRepository;
import unimib.daBancherz.NewMonopoly.database.Repository.PartitaOpportunitaRepository;

import java.util.Map;

@Component
public class PrisonManager {

    private final GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();
    private final PartitaOpportunitaRepository partitaOpportunitaRepository;
    private final GiocatoreRepository giocatoreRepository;
    private final MessageHandler messageHandler;
    private final GameHandler gameHandler;
    private String EXITPRISON_KEY = "exitPrison";

    public PrisonManager(PartitaOpportunitaRepository partitaOpportunitaRepository, GiocatoreRepository giocatoreRepository, MessageHandler messageHandler, GameHandler gameHandler) {
        this.partitaOpportunitaRepository = partitaOpportunitaRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.messageHandler = messageHandler;
        this.gameHandler = gameHandler;
    }

    public boolean isPlayerInPrison(String gameId, String playerName) {
        return gameBoard.isPlayerInPrison(gameId, playerName);
    }

    public void handlePrisonPlayer(String gameId, String playerName, WebSocketSession session) throws Exception {
        if (partitaOpportunitaRepository.possiedeCarta(playerName, gameId, "Probabilità")) {
            exitPrison(gameId, playerName, session, "Probabilità");
        } else if (partitaOpportunitaRepository.possiedeCarta(playerName, gameId, "Imprevisto")) {
            exitPrison(gameId, playerName, session, "Imprevisto");
        } else {
            sendPrisonMessage(session);
        }
    }

    private void exitPrison(String gameId, String playerName, WebSocketSession session, String cardKey) throws Exception {
        gameBoard.setPlayerPrison(gameId, playerName, false);
        partitaOpportunitaRepository.setGiocatore(gameId, null, "esci_prigione", cardKey);
        gameBoard.setPlayerCountRoll(gameId, playerName, 0);
        messageHandler.exitPrisonMessage(true, session);
        messageHandler.sendSystemMessage(gameId, playerName + " è uscito di prigione", gameHandler.getGameSessions(), session);
    }

    private void sendPrisonMessage(WebSocketSession session) throws Exception {
        String prisonMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "prison"
        ));
        session.sendMessage(new TextMessage(prisonMessage));
    }

    public boolean lasciaPrigione( WebSocketSession session, int[] diceResults) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        int countRoll = gameBoard.getPlayerCountRoll(gameId, playerName);
        gameBoard.setPlayerCountRoll(gameId, playerName, countRoll + 1);
        int diceR1 = diceResults[0];
        int diceR2 = diceResults[1];

        if (gameBoard.getPlayerCountRoll(gameId, playerName) == 4 || diceR1 == diceR2) {
            gameBoard.setPlayerPrison(gameId, playerName, false);
            gameBoard.setPlayerCountRoll(gameId, playerName, 0);
            messageHandler.exitPrisonMessage(true, session);
            return true;
        }
        return false;
    }


    public void payPrisonExit(WebSocketSession session) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        //non sono sicuro, caso in cui tiri il dado doppio ma finisci sulla casella in prigione, serve a far si che si azzeri il cunt del tuo tiro dado doppio
        gameBoard.setPlayerCountRollDoubleDice(gameId, playerName, 0);
        if(giocatoreRepository.saldoGiocatore(playerName, gameId) < 50) {
            messageHandler.exitPrisonMessage(false, session);
        }else {
            gameBoard.setPlayerPrison(gameId, playerName, false);
            giocatoreRepository.setSaldoGiocatore(playerName, gameId, 50);
            messageHandler.updateBalance(gameHandler.getGameSessions(), gameId, playerName);
            gameBoard.setPlayerCountRoll(gameId, playerName, 0);
            messageHandler.exitPrisonMessage(false, session);
        }
    }
}