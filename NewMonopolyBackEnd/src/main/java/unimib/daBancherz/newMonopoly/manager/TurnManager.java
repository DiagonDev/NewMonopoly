package unimib.daBancherz.newMonopoly.manager;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.newMonopoly.handler.GameHandler;
import unimib.daBancherz.newMonopoly.handler.PawnHandler;
import unimib.daBancherz.newMonopoly.MessageService;
import unimib.daBancherz.newMonopoly.singleton.GameBoardSingleton;
import unimib.daBancherz.newMonopoly.database.entity.Partita;
import unimib.daBancherz.newMonopoly.database.repository.GiocatoreRepository;
import unimib.daBancherz.newMonopoly.database.repository.PartitaRepository;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

@Component
public class TurnManager {

    private final GameHandler gameHandler;
    private final MessageService messageService;
    private final PrisonManager prisonManager;
    private final BalanceManager balanceManager;
    private final PartitaRepository partitaRepository;
    private final GiocatoreRepository giocatoreRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final PawnHandler pawnHandler;
    GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();

    public TurnManager(GameHandler gameHandler, MessageService messageService, PrisonManager prisonManager, BalanceManager balanceManager, PartitaRepository partitaRepository, GiocatoreRepository giocatoreRepository, PawnHandler pawnHandler) {
        this.gameHandler = gameHandler;
        this.messageService = messageService;
        this.prisonManager = prisonManager;
        this.balanceManager = balanceManager;
        this.partitaRepository = partitaRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.pawnHandler = pawnHandler;
    }

    public void startTurn(WebSocketSession session) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        Partita partita = partitaRepository.findByCodiceInvito(gameId);
        partita.setStato("Iniziata");
        messageService.sendSystemMessage(gameId, "È il turno di: " + playerName, gameHandler.getGameSessions(), session);

        // Notifica ai giocatori
        notifyPlayersTurn(gameId, playerName, session);

        // Gestione del giocatore in prigione
        if (prisonManager.isPlayerInPrison(gameId, playerName)) {
            prisonManager.handlePrisonPlayer(gameId, playerName, session);
        }
    }

    public void notifyPlayersTurn(String gameId, String playerName, WebSocketSession session) throws Exception {
        String yourTurnMessage = messageService.createTurnMessage(true, playerName);
        String notYourTurnMessage = messageService.createTurnMessage(false, playerName);
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
        messageService.sendSystemMessage(gameId, playerName + " ha tirato i dati: dado1 " + diceR1 + ", dado2 " + diceR2, gameSessions, session);

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
                pawnHandler.movimentoPedina(gameId, playerName, 11, pawnId, session,viaPay);
            }else{
                newPosition = totDice + playerPosition;

                if (newPosition > 40) {
                    newPosition -= 40;
                    viaPay = true;
                }
                pawnHandler.movimentoPedina(gameId, playerName, newPosition, pawnId, session,viaPay);
            }
        }
    }

    public int[] rollDice(WebSocketSession session) throws Exception {
        int diceR1 = secureRandom.nextInt(6) + 1; // Genera un numero casuale tra 1 e 6
        int diceR2 = secureRandom.nextInt(6) + 1;
        messageService.sendDiceResults(session, diceR1, diceR2);
        return new int[]{diceR1, diceR2};
    }
}

