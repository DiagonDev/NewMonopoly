package unimib.daBancherz.NewMonopoly.Manager;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Handler.GameHandler;
import unimib.daBancherz.NewMonopoly.Handler.MessageHandler;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;
import unimib.daBancherz.NewMonopoly.database.Entity.Partita;
import unimib.daBancherz.NewMonopoly.database.Repository.GiocatoreRepository;
import unimib.daBancherz.NewMonopoly.database.Repository.PartitaRepository;

import java.util.List;
import java.util.Map;

@Component
public class TurnManager {

    private final GameHandler gameHandler;
    private final MessageHandler messageHandler;
    private final PrisonManager prisonManager;
    private final BalanceManager balanceManager;
    private final PartitaRepository partitaRepository;
    private final DiceManager diceManager;
    private final BoxManager boxManager;
    private final GiocatoreRepository giocatoreRepository;
    GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();

    public TurnManager(GameHandler gameHandler, MessageHandler messageHandler, PrisonManager prisonManager, BalanceManager balanceManager, PartitaRepository partitaRepository, DiceManager diceManager, BoxManager boxManager, GiocatoreRepository giocatoreRepository) {
        this.gameHandler = gameHandler;
        this.messageHandler = messageHandler;
        this.prisonManager = prisonManager;
        this.balanceManager = balanceManager;
        this.partitaRepository = partitaRepository;
        this.diceManager = diceManager;
        this.boxManager = boxManager;
        this.giocatoreRepository = giocatoreRepository;
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
        WebSocketSession nextPlayer = playersInGame.get(nextIndex);
        startTurn(nextPlayer);
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
        messageHandler.sendDiceResults(session, diceResults[0], diceResults[1]);
        return diceResults;
    }
}