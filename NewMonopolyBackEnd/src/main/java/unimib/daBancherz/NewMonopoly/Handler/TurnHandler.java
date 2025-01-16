package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class TurnHandler {

    private final GameHandler gameHandler;
    private final MessageHandler messageHandler;
    GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();

    @Autowired
    public TurnHandler(GameHandler gameHandler, MessageHandler messageHandler) {
        this.gameHandler = gameHandler;
        this.messageHandler = messageHandler;
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

    public void startTurn( WebSocketSession session ) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        List<WebSocketSession> playersInGame = gameHandler.getGameSessions().get(gameId);

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

    }

    public void rollDice(WebSocketSession session) throws Exception {
        int diceR1 = ThreadLocalRandom.current().nextInt(1, 7);  // Valore da 1 a 6 per il primo dado
        int diceR2 = ThreadLocalRandom.current().nextInt(1, 7);  // Valore da 1 a 6 per il secondo dado
        int totDice = diceR1 + diceR2;
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        int playerPosition = gameBoard.getPlayerPosition(gameId, playerName);
        int newPosition = totDice + playerPosition;

        String diceRolled = new ObjectMapper().writeValueAsString(Map.of(
                "type", "diceRolled",
                "dice1", diceR1,
                "dice2", diceR2
        ));

        //TODO: metodo per inviare i messaggi per che tipo è la casella
        messageHandler.sendTypeBox(gameId, playerName, newPosition, gameHandler.getGameSessions());

        if( newPosition > 40 ){
            newPosition -= 40;
            //TODO: aggiungere al saldo 200, perchè signfica che è passato dal VIA
        }
        session.sendMessage(new TextMessage(diceRolled));//invia il risultato dei dati al giocatore che li ha tirati
        gameBoard.setPlayerPosition(gameId, playerName, newPosition);//aggiorna la posizione del giocatore

        //serve come controllo per capire dove finisce la pedina, SERVE PER IL DEBUG/TEST
        System.out.println("La posizione di: " + playerName + " di game: " + gameId + " è: " + newPosition);
    }
}
