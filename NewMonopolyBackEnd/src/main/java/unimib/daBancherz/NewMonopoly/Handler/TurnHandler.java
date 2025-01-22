package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
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
    private final String exitPrison = "exitPrison";
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
                gameBoard.setPlayerPrison(gameId, playerName, false);
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
            String exitPrisonMessage = new ObjectMapper().writeValueAsString(Map.of(
                    "type", exitPrison,
                    "flag", true
            ));
            session.sendMessage(new TextMessage(exitPrisonMessage));
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
            String exitPrisonMessage = new ObjectMapper().writeValueAsString(Map.of(
                    "type", exitPrison,
                    "flag", false
            ));
            session.sendMessage(new TextMessage(exitPrisonMessage));
        }else {
            gameBoard.setPlayerPrison(gameId, playerName, false);
            giocatoreRepository.setSaldoGiocatore(playerName, gameId, 50);
            messageHandler.updateBalance(gameHandler.getGameSessions(), gameId, playerName);
            gameBoard.setPlayerCountRoll(gameId, playerName, 0);
            String exitPrisonMessage = new ObjectMapper().writeValueAsString(Map.of(
                    "type", exitPrison,
                    "flag", true
            ));
            session.sendMessage(new TextMessage(exitPrisonMessage));
        }
    }

    public void spostaPedina(WebSocketSession session ) throws Exception {
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
                messageHandler.sendBoxUsage(playerName, session, 11, gameId, gameHandler.getGameSessions(), pawnId, viaPay);
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
                messageHandler.sendBoxUsage(playerName, session, newPosition, gameId, gameHandler.getGameSessions(), pawnId, viaPay);

            }
        }
        /*int[] diceResults = rollDice();
        int diceR1 = diceResults[0];
        int diceR2 = diceResults[1];
        int totDice = diceR1 + diceR2;
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        int playerPosition = gameBoard.getPlayerPosition(gameId, playerName);
        boolean isInPrison = gameBoard.isPlayerInPrison(gameId, playerName);
        int countRoll = gameBoard.getPlayerCountRoll(gameId, playerName);
        gameBoard.setPlayerCountRoll(gameId, playerName, countRoll+1 );
        int newPosition;
        boolean viaPay = false;
        int countRollDoubleDice = gameBoard.getPlayerCountRollDoubleDice(gameId, playerName);
        int pawnId = giocatoreRepository.findPedinaFromGiocatore(playerName, gameId);

        String diceRolled = new ObjectMapper().writeValueAsString(Map.of(
                "type", "diceRolled",
                "dice1", diceR1,
                "dice2", diceR2
        ));
        session.sendMessage(new TextMessage(diceRolled));//invia il risultato dei dati al giocatore che li ha tirati
        //perchè se per caso ha fatto due volte doppi dadi e alla seconda è finito in prigione per imprevisto/probabilità bisogna azzerargli i tiri doppi
        if(isInPrison){
            gameBoard.setPlayerCountRollDoubleDice(gameId, playerName, 0);
        }

        if(diceR1 == diceR2 && !isInPrison){
            gameBoard.setPlayerCountRollDoubleDice(gameId, playerName, ++countRollDoubleDice);
        }

        if(gameBoard.getPlayerCountRollDoubleDice(gameId, playerName) == 3) {
            gameBoard.setPlayerPrison(gameId, playerName, true);
            gameBoard.setPlayerCountRollDoubleDice(gameId, playerName, 0);
            gameBoard.setPlayerCountRoll(gameId, playerName, 0);
            gameBoard.setPlayerPosition(gameId, playerName, 11);//aggiorna la posizione del giocatore
            //invia a tutti i giocatori che il "playername" si è postato di tot caselle "newPosition"
            messageHandler.sendPawnMove(pawnId, playerName, 11, gameHandler.getGameSessions(), gameId);

            //metodo che mostra le opzioni disponibili da fare sulla casella dopo che ci si è finiti sopra
            messageHandler.sendBoxUsage(playerName, session, 11, gameId, gameHandler.getGameSessions(), pawnId, viaPay);

        }
        else {
            if (gameBoard.getPlayerCountRoll(gameId, playerName) == 4) {
                gameBoard.setPlayerPrison(gameId, playerName, false);
                gameBoard.setPlayerCountRoll(gameId, playerName, 0);
                //bisogna vedere se mandare il messaggio, perchè in teoria dal prossio turno lui sara furoi e non dal terzo
                String exitPrisonMEssage = new ObjectMapper().writeValueAsString(Map.of(
                        "type", "exitPrison",
                        "flag", true
                ));
                session.sendMessage(new TextMessage(exitPrisonMEssage));
                isInPrison = false;
            }
            if(!isInPrison && (diceR1 != diceR2)){
                gameBoard.setPlayerCountRollDoubleDice(gameId, playerName, 0);
                gameBoard.setPlayerCountRoll(gameId, playerName, 0);
                newPosition = totDice + playerPosition;
                //serve a recuperare la pawnId del giocatore

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
            else if (!isInPrison || (diceR1 == diceR2)) {
                gameBoard.setPlayerPrison(gameId, playerName, false);
                //gameBoard.setPlayerCountRollDoubleDice(gameId, playerName, 0);
                gameBoard.setPlayerCountRoll(gameId, playerName, 0);
                newPosition = totDice + playerPosition;
                //serve a recuperare la pawnId del giocatore

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
        }*/
    }

    public int[] rollDice(WebSocketSession session) throws Exception {
        int diceR1 = ThreadLocalRandom.current().nextInt(1, 7);
        int diceR2 = ThreadLocalRandom.current().nextInt(1, 7);
        String diceRolled = new ObjectMapper().writeValueAsString(Map.of(
                "type", "diceRolled",
                "dice1", diceR1,
                "dice2", diceR2
        ));
        session.sendMessage(new TextMessage(diceRolled));//invia il risultato dei dati al giocatore che li ha tirati
        return new int[]{diceR1, diceR2}; // Restituisce entrambi i valori
    }

}
