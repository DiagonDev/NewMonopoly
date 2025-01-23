package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
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

    private final GameHandler gameHandler;
    private final MessageHandler messageHandler;
    private final GiocatoreRepository giocatoreRepository;
    private final PartitaOpportunitaRepository partitaOpportunitaRepository;
    private final PartitaRepository partitaRepository;
    private final PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();
    private final String EXITPRISON_KEY = "exitPrison";
    private final String IMPREVISTO_KEY = "Imprevisto";
    private final String PROBABILITA_KEY = "Probabilità";


    public TurnHandler(GameHandler gameHandler, MessageHandler messageHandler, GiocatoreRepository giocatoreRepository, PartitaOpportunitaRepository partitaOpportunitaRepository, PartitaRepository partitaRepository, PartitaCasellaPrezzoproprietaRepository pCPPRepository) {
        this.gameHandler = gameHandler;
        this.messageHandler = messageHandler;
        this.giocatoreRepository = giocatoreRepository;
        this.partitaOpportunitaRepository = partitaOpportunitaRepository;
        this.partitaRepository = partitaRepository;
        this.pCPPRepository = pCPPRepository;
    }

    public void startTurn( WebSocketSession session ) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        List<WebSocketSession> playersInGame = gameHandler.getGameSessions().get(gameId);
        Partita partita = partitaRepository.findByCodiceInvito(gameId);
        partita.setStato("Iniziata");
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
            boolean possiedeProbabilita = partitaOpportunitaRepository.possiedeCarta(playerName, gameId, PROBABILITA_KEY);
            boolean possiedeImprevisto = partitaOpportunitaRepository.possiedeCarta(playerName, gameId, IMPREVISTO_KEY);
            System.out.println(possiedeImprevisto);
            System.out.println(possiedeProbabilita);
            if(possiedeProbabilita){
                gameBoard.setPlayerPrison(gameId, playerName, false);
                partitaOpportunitaRepository.setGiocatore(gameId, null, "esci_prigione", PROBABILITA_KEY);
                gameBoard.setPlayerCountRoll(gameId, playerName, 0);
                String exitPrisonMessage = new ObjectMapper().writeValueAsString(Map.of(
                        "type", EXITPRISON_KEY,
                        "flag", true
                ));
                session.sendMessage(new TextMessage(exitPrisonMessage));
                messageHandler.sendSystemMessage(gameId, playerName + " è uscito di prigione", gameHandler.getGameSessions(), session);
            } else if(possiedeImprevisto){
                gameBoard.setPlayerPrison(gameId, playerName, false);
                partitaOpportunitaRepository.setGiocatore(gameId, null, "esci_prigione", IMPREVISTO_KEY);
                gameBoard.setPlayerCountRoll(gameId, playerName, 0);
                String exitPrisonMessage = new ObjectMapper().writeValueAsString(Map.of(
                        "type", EXITPRISON_KEY,
                        "flag", true
                ));
                session.sendMessage(new TextMessage(exitPrisonMessage));
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
        System.out.println(gameId + "  " + playerName);
        int saldoG = giocatoreRepository.saldoGiocatore(playerName, gameId);
        messageHandler.sendSystemMessage(gameId, playerName + " ha concluso il turno", gameHandler.getGameSessions(), session);
        int currentIndex = playersInGame.indexOf(session);
        String content = "Il tuo saldo è negativo";
        // Calcola l'indice della prossima sessione in modo circolare
        int nextIndex = (currentIndex + 1) % playersInGame.size();
        List<PlayerProperties> giocatorePropertiesList;

        // Assegna la sessione successiva
        WebSocketSession nextPlayer = playersInGame.get(nextIndex);
        if(saldoG < 0) {
            String loseMessage = new ObjectMapper().writeValueAsString(Map.of(
                    "type", "partitaFinita",
                    "flag", "sconfitta",
                    "content", content
            ));
            session.sendMessage(new TextMessage(loseMessage));
            messageHandler.sendSystemMessage(gameId, playerName + " ha perso", gameHandler.getGameSessions(), session);
            gameHandler.removePlayerFromGame(gameId, session);
            playersInGame = gameHandler.getGameSessions().get(gameId);

            if(playersInGame.size() == 1){
                WebSocketSession vincitore = playersInGame.get(0);
                content = "Hai vinto";
                String winMessage = new ObjectMapper().writeValueAsString(Map.of(
                        "type", "partitaFinita",
                        "flag", "vittoria",
                        "content", content
                ));
                vincitore.sendMessage(new TextMessage(winMessage));
                return;
            }
        }

        giocatorePropertiesList = pCPPRepository.findPlayerProperties(gameId,playerName);
        String updateGiocatorePropertiesMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "updateProperties",
                "properties", giocatorePropertiesList
        ));
        session.sendMessage(new TextMessage(updateGiocatorePropertiesMessage));

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
                    "type", EXITPRISON_KEY,
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
                    "type", EXITPRISON_KEY,
                    "flag", false
            ));
            session.sendMessage(new TextMessage(exitPrisonMessage));
        }else {
            gameBoard.setPlayerPrison(gameId, playerName, false);
            giocatoreRepository.setSaldoGiocatore(playerName, gameId, 50);
            messageHandler.updateBalance(gameHandler.getGameSessions(), gameId, playerName);
            gameBoard.setPlayerCountRoll(gameId, playerName, 0);
            String exitPrisonMessage = new ObjectMapper().writeValueAsString(Map.of(
                    "type", EXITPRISON_KEY,
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
    }

    public int[] rollDice(WebSocketSession session) throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        Map<String, List<WebSocketSession>> gameSessions = gameHandler.getGameSessions();
        int diceR1 = secureRandom.nextInt(6) + 1; // Genera un numero casuale tra 1 e 6
        int diceR2 = secureRandom.nextInt(6) + 1;

        String diceRolled = new ObjectMapper().writeValueAsString(Map.of(
                "type", "diceRolled",
                "dice1", diceR1,
                "dice2", diceR2
        ));
        session.sendMessage(new TextMessage(diceRolled));//invia il risultato dei dati al giocatore che li ha tirati
        messageHandler.sendSystemMessage(gameId, playerName + " ha tirato i dati: dado1 " + diceR1 + ", dado2 " + diceR2, gameSessions, session);
        return new int[]{diceR1, diceR2}; // Restituisce entrambi i valori
    }

}
