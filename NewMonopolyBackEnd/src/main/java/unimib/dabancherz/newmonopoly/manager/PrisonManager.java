package unimib.dabancherz.newmonopoly.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.handler.GameHandler;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.singleton.GameBoardSingleton;
import unimib.dabancherz.newmonopoly.database.repository.GiocatoreRepository;
import unimib.dabancherz.newmonopoly.database.repository.PartitaOpportunitaRepository;

import java.util.Map;

@Component
public class PrisonManager {

    public GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();
    private final PartitaOpportunitaRepository partitaOpportunitaRepository;
    private final GiocatoreRepository giocatoreRepository;
    private final MessageService messageService;
    private final GameHandler gameHandler;

    public PrisonManager(PartitaOpportunitaRepository partitaOpportunitaRepository, GiocatoreRepository giocatoreRepository, MessageService messageService, GameHandler gameHandler) {
        this.partitaOpportunitaRepository = partitaOpportunitaRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.messageService = messageService;
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
        }/* else {
            messageService.exitPrisonMessage(false, session);
        }*/
    }

    private void exitPrison(String gameId, String playerName, WebSocketSession session, String cardKey) throws Exception {
        gameBoard.setPlayerPrison(gameId, playerName, false);
        partitaOpportunitaRepository.setGiocatore(gameId, null, "esci_prigione", cardKey);
        gameBoard.setPlayerCountRoll(gameId, playerName, 0);
        messageService.exitPrisonMessage(true, session);
        messageService.sendSystemMessage(gameId, playerName + " è uscito di prigione", gameHandler.getGameSessions(), session);
    }

    public void sendPrisonMessage(WebSocketSession session) throws Exception {
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
            messageService.exitPrisonMessage(true, session);
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
            messageService.exitPrisonMessage(false, session);
        }else {
            gameBoard.setPlayerPrison(gameId, playerName, false);
            giocatoreRepository.setSaldoGiocatore(playerName, gameId, 50);
            messageService.updateBalance(gameHandler.getGameSessions(), gameId, playerName);
            gameBoard.setPlayerCountRoll(gameId, playerName, 0);
            messageService.exitPrisonMessage(true, session);
            messageService.sendSystemMessage(gameId, playerName + " è uscito di prigione", gameHandler.getGameSessions(), session);
        }
    }
}