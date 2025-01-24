package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Manager.BoxManager;
import unimib.daBancherz.NewMonopoly.Manager.DiceManager;
import unimib.daBancherz.NewMonopoly.Manager.PrisonManager;
import unimib.daBancherz.NewMonopoly.Manager.TurnManager;
import unimib.daBancherz.NewMonopoly.database.Repository.GiocatoreRepository;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;
import unimib.daBancherz.NewMonopoly.database.Repository.PartitaCasellaPrezzoproprietaRepository;
import unimib.daBancherz.NewMonopoly.database.Repository.PartitaOpportunitaRepository;
import unimib.daBancherz.NewMonopoly.database.Repository.PartitaRepository;
import unimib.daBancherz.NewMonopoly.database.Entity.*;
import unimib.daBancherz.NewMonopoly.model.PlayerProperties;

import java.util.List;
import java.util.Map;
import java.security.SecureRandom;

@Component
public class TurnHandler {

    private final TurnManager turnManager;
    private final DiceManager diceManager;
    private final PrisonManager prisonManager;
    private final BoxManager boxManager;
    private final GameHandler gameHandler;
    private final MessageHandler messageHandler;
    private final GiocatoreRepository giocatoreRepository;
    GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();

    public TurnHandler(TurnManager turnManager, DiceManager diceManager, PrisonManager prisonManager, BoxManager boxManager, GameHandler gameHandler, MessageHandler messageHandler, GiocatoreRepository giocatoreRepository) {
        this.turnManager = turnManager;
        this.diceManager = diceManager;
        this.prisonManager = prisonManager;
        this.boxManager = boxManager;
        this.gameHandler = gameHandler;
        this.messageHandler = messageHandler;
        this.giocatoreRepository = giocatoreRepository;
    }

    public void startTurn( WebSocketSession session ) throws Exception {
        turnManager.startTurn(session);
    }

    public void endTurn( WebSocketSession session ) throws Exception {
        turnManager.endTurn(session);
    }

    public boolean lasciaPrigione( WebSocketSession session, int[] diceResults) throws Exception {
        return prisonManager.lasciaPrigione(session, diceResults);
    }

    public void payPrisonExit(WebSocketSession session) throws Exception {
        prisonManager.payPrisonExit(session);
    }

    public void spostaPedina(WebSocketSession session ) throws Exception {
        Map<String, List<WebSocketSession>> gameSessions = gameHandler.getGameSessions();
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        boolean isInPrison = gameBoard.isPlayerInPrison(gameId, playerName);
        int countRollDoubleDice = gameBoard.getPlayerCountRollDoubleDice(gameId, playerName);
        int pawnId = giocatoreRepository.findPedinaFromGiocatore(playerName, gameId);
        boolean viaPay = false;
        int newPosition;
        int playerPosition = gameBoard.getPlayerPosition(gameId, playerName);
        int[] diceResults = rollDice(session);
        int diceR1 = diceResults[0];
        int diceR2 = diceResults[1];
        int totDice = diceR1 + diceR2;
        messageHandler.sendSystemMessage(gameId, playerName + " ha tirato i dati: dado1 " + diceR1 + ", dado2 " + diceR2, gameSessions, session);

        if(isInPrison){
            isInPrison = !lasciaPrigione(session, diceResults);
        }

        if(!isInPrison){
            if(diceR1 == diceR2) gameBoard.setPlayerCountRollDoubleDice(gameId, playerName, ++countRollDoubleDice);
            else gameBoard.setPlayerCountRollDoubleDice(gameId, playerName, 0);

            if(gameBoard.getPlayerCountRollDoubleDice(gameId, playerName) == 3){
                gameBoard.setPlayerPrison(gameId, playerName, true);
                gameBoard.setPlayerCountRollDoubleDice(gameId, playerName, 0);
                gameBoard.setPlayerCountRoll(gameId, playerName, 0);
                gameBoard.setPlayerPosition(gameId, playerName, 11);//aggiorna la posizione del giocatore
                //invia a tutti i giocatori che il "playername" si è postato di tot caselle "newPosition"
                messageHandler.sendPawnMove(pawnId, playerName, 11, gameHandler.getGameSessions(), gameId);

                //metodo che mostra le opzioni disponibili da fare sulla casella dopo che ci si è finiti sopra
                boxManager.sendBoxUsage(playerName, session, 11, gameId, gameHandler.getGameSessions(), pawnId, viaPay);
            }else{
                newPosition = totDice + playerPosition;

                if (newPosition > 40) {
                    newPosition -= 40;
                    viaPay = true;
                }
                gameBoard.setPlayerPosition(gameId, playerName, newPosition);//aggiorna la posizione del giocatore
                //invia a tutti i giocatori che il "playername" si è postato di tot caselle "newPosition"
                messageHandler.sendPawnMove(pawnId, playerName, newPosition, gameHandler.getGameSessions(), gameId);

                //metodo che mostra le opzioni disponibili da fare sulla casella dopo che ci si è finiti sopra
                boxManager.sendBoxUsage(playerName, session, newPosition, gameId, gameHandler.getGameSessions(), pawnId, viaPay);

            }
        }
    }

    public int[] rollDice(WebSocketSession session) throws Exception {
        int[] diceResults = diceManager.rollDice();
        diceManager.sendDiceResults(session, diceResults[0], diceResults[1]);
        return diceResults;
    }

}