package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.GiocatoreRepository;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.PartitaImprevistoRepository;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.PartitaProbabilitaRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class TurnHandler {

    private final GameHandler gameHandler;
    private final MessageHandler messageHandler;
    private final GiocatoreRepository giocatoreRepository;
    private final PartitaProbabilitaRepository partitaProbabilitaRepository;
    private final PartitaImprevistoRepository partitaImprevistoRepository;
    GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();
    int counterRollDice = 0;

    public TurnHandler(GameHandler gameHandler, MessageHandler messageHandler, GiocatoreRepository giocatoreRepository, PartitaProbabilitaRepository partitaProbabilitaRepository, PartitaImprevistoRepository partitaImprevistoRepository) {
        this.gameHandler = gameHandler;
        this.messageHandler = messageHandler;
        this.giocatoreRepository = giocatoreRepository;
        this.partitaProbabilitaRepository = partitaProbabilitaRepository;
        this.partitaImprevistoRepository = partitaImprevistoRepository;
    }

    public void startTurn( WebSocketSession session ) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        List<WebSocketSession> playersInGame = gameHandler.getGameSessions().get(gameId);
        boolean isInPrison = gameBoard.isPlayerInPrison(gameId, playerName);

        String yourTurnMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "turn",
                "content", true,
                "playername", playerName // non so se serve al giocatore che sta facendo il turno, si può anche togliere
        ));

        String notYourTurnMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "turn",
                "content", false,
                "playername", playerName
        ));

        messageHandler.sendSystemMessage(gameId, "È il turno di: " + playerName, gameHandler.getGameSessions(), session);

        for (WebSocketSession playerSession : playersInGame) {
            if (!playerSession.equals(session)) {
                playerSession.sendMessage(new TextMessage(notYourTurnMessage));
            }
            else{
                session.sendMessage(new TextMessage(yourTurnMessage));
            }
        }

        if(isInPrison){
            boolean possiedeProbabilita = partitaProbabilitaRepository.possiedeCarta(gameId, playerName);
            boolean possiedeImprevisto = partitaImprevistoRepository.possiedeCarta(gameId, playerName);
            if(possiedeProbabilita){
                gameBoard.setPlayerPrison(gameId, playerName, false);;
                partitaProbabilitaRepository.setGiocatore(gameId, null, "esci_prigione");
                messageHandler.sendSystemMessage(gameId, playerName + " è uscito di prigione", gameHandler.getGameSessions(), session);
            } else if(possiedeImprevisto){
                gameBoard.setPlayerPrison(gameId, playerName, false);
                partitaImprevistoRepository.setGiocatore(gameId, null, "esci_prigione");
                messageHandler.sendSystemMessage(gameId, playerName + " è uscito di prigione", gameHandler.getGameSessions(), session);
            }else {
                String prisonMessage = new ObjectMapper().writeValueAsString(Map.of(
                        "type", "prison"
                ));
                session.sendMessage(new TextMessage(prisonMessage));
            }
        }
    }

    public void endTurn( WebSocketSession session ) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        List<WebSocketSession> playersInGame = gameHandler.getGameSessions().get(gameId);
        messageHandler.sendSystemMessage(gameId, playerName + " ha concluso il turno", gameHandler.getGameSessions(), session);
        int currentIndex = playersInGame.indexOf(session);

        // Calcola l'indice della prossima sessione in modo circolare
        int nextIndex = (currentIndex + 1) % playersInGame.size();

        // Assegna la sessione successiva
        WebSocketSession nextPlayer = playersInGame.get(nextIndex);

        // Avvia il turno per la prossima sessione
        startTurn(nextPlayer);
    }

    public void rollDice(WebSocketSession session) throws Exception {
        int diceR1 = ThreadLocalRandom.current().nextInt(1, 7);  // Valore da 1 a 6 per il primo dado
        int diceR2 = ThreadLocalRandom.current().nextInt(1, 7);  // Valore da 1 a 6 per il secondo dado
        int totDice = diceR1 + diceR2;
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        int playerPosition = gameBoard.getPlayerPosition(gameId, playerName);
        boolean isInPrison = gameBoard.isPlayerInPrison(gameId, playerName);
        int countRoll = gameBoard.getPlayerCountRoll(gameId, playerName);
        gameBoard.setPlayerCountRoll(gameId, playerName, countRoll+1 );
        int newPosition;
        boolean viaPay = false;

        String diceRolled = new ObjectMapper().writeValueAsString(Map.of(
                "type", "diceRolled",
                "dice1", diceR1,
                "dice2", diceR2
        ));
        session.sendMessage(new TextMessage(diceRolled));//invia il risultato dei dati al giocatore che li ha tirati


        if(gameBoard.getPlayerCountRoll(gameId, playerName) == 4){
            gameBoard.setPlayerPrison(gameId, playerName, false);
            gameBoard.setPlayerCountRoll(gameId, playerName, 0);
            String exitPrisonMEssage = new ObjectMapper().writeValueAsString(Map.of(
                    "type", "exitPrison",
                    "flag", true
            ));
            session.sendMessage(new TextMessage(exitPrisonMEssage));
        }
        if(!isInPrison || (diceR1 == diceR2)) {
            gameBoard.setPlayerPrison(gameId, playerName, false);
            newPosition = totDice + playerPosition;
            //serve a recuperare la pawnId del giocatore
            int pawnId = giocatoreRepository.findPedinaFromGiocatore(playerName, gameId);
            if (newPosition > 40) {
                newPosition -= 40;
                viaPay = true;

            }
            gameBoard.setPlayerPosition(gameId, playerName, newPosition);//aggiorna la posizione del giocatore
            //invia a tutti i giocatori che il "playername" si è postato di tot caselle "newPosition"
            messageHandler.sendPawnMove(pawnId, playerName, newPosition, gameHandler.getGameSessions(), gameId);

            //metodo che mostra le opzioni disponibili da fare sulla casella dopo che ci si è finiti sopra
            messageHandler.sendBoxUsage(playerName, session, newPosition, gameId, gameHandler.getGameSessions(), pawnId, viaPay);
        }
    }

    public void payPrisonExit(WebSocketSession session) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        gameBoard.setPlayerPrison(gameId, playerName, false);
        if(giocatoreRepository.saldoGiocatore(playerName, gameId) < 50) {
            String exitPrisonMEssage = new ObjectMapper().writeValueAsString(Map.of(
                    "type", "exitPrison",
                    "flag", false
            ));
            session.sendMessage(new TextMessage(exitPrisonMEssage));
        }else {
            giocatoreRepository.aggiornamentoSaldo(playerName, gameId, 50);
            messageHandler.updateBalance(gameHandler.getGameSessions(), gameId, playerName);
            String exitPrisonMEssage = new ObjectMapper().writeValueAsString(Map.of(
                    "type", "exitPrison",
                    "flag", true
            ));
            session.sendMessage(new TextMessage(exitPrisonMEssage));
        }
    }
}
