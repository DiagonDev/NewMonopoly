package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Repository.GiocatoreRepository;
import unimib.daBancherz.NewMonopoly.Service.GameService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class GameHandler {

    private final Map<String, List<WebSocketSession>> gameSessions = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> playerNameList = new ConcurrentHashMap<>();
    private static AtomicLong idCounter = new AtomicLong();
    @Autowired
    private GameService gameService;

    @Autowired
    private GiocatoreRepository giocatoreRepository;

    public void handleGameMessage(String payload, WebSocketSession session) throws Exception {
        if (payload.startsWith("Create:")) {
            // Decodifica i dati per creare una partita
            String[] parts = payload.split(":");
            String playerName = parts[1];
            String difficulty = parts[2];
            String randomization = parts[3];        //deve essere un boolean

            createGame(playerName, difficulty, randomization, session);
            playerNameList.put(playerName, session);

        } else if (payload.startsWith("Partecipa:")) {
            // Decodifica i dati per partecipare a una partita
            String[] parts = payload.split(":");
            String playerName = parts[1];
            String gameId = parts[2];

            if (giocatoreRepository.existsByNomeAndIdpartita_CodiceInvito(playerName, gameId)) {
                // TODO: Gestire il messaggio frontend per non mandarlo all'altra pagina
                session.sendMessage(new TextMessage("Errore: Il nome del giocatore è già presente in questa partita."));
                return; // Esce dalla funzione senza aggiungere il giocatore
            }
            playerNameList.put(playerName, session);
            joinGame(playerName, gameId, session);
        }
    }

    private void createGame(String playerName, String difficulty, String randomization, WebSocketSession session) throws Exception {
        String gameId = generateGameId();
        System.out.println("Partita creata. ID: " + gameId);

        gameSessions.putIfAbsent(gameId, new ArrayList<>());
        gameSessions.get(gameId).add(session);

        String message = "#" + gameId;      //codice invito
        sendSystemMessage(gameId, message, session);

        gameService.createGameAndPlayer(playerName,difficulty, gameId); //aggiungi randomizzazione
        //session.sendMessage(new TextMessage(gameId));
    }

    private void joinGame(String playerName, String gameId, WebSocketSession session) throws Exception {
        if (!gameSessions.containsKey(gameId)) {
            session.sendMessage(new TextMessage("Errore: La partita con ID " + gameId + " non esiste."));
            return;
        }

        List<WebSocketSession> playersInGame = gameSessions.get(gameId);
        playersInGame.add(session);
        gameService.addPlayer(playerName, gameId);
        String content1 = "Ti sei unito alla partita con ID " + gameId + " con successo!";
        sendSystemMessage(gameId, content1, session);
        //session.sendMessage(new TextMessage("Ti sei unito alla partita con ID " + gameId + " con successo!"));
        String content2 = playerName + " si è unito alla partita!";
        sendSystemMessage(gameId, content2, session);

        // Stampa tutti i partecipanti della partita
        System.out.println("Partecipanti della partita con ID " + gameId + ":");
        for (WebSocketSession playerSession : playersInGame) {
            String nomeGiocatore = getPlayerNameBySession(playerSession); // Stampiamo il nome del giocatore della sessione
            System.out.println(" - ID sessione: " + nomeGiocatore);
        }

    }

    public void chatHandler (String payload, WebSocketSession session) throws Exception {
        String[] parts = payload.split(":");
        String chatMessage = parts[1];
        String gameId = getGameIdBySession(session);
        String nameChat = getPlayerNameBySession(session);

        //invia il messaggio a tutti gli utenti collegati allo stesso gameID sotto forma di messaggioChat
        sendChatMessage(gameId,nameChat+ ": " + chatMessage);
        //broadcastChatMessage(gameId, session, chatMessage);
    }

    private String getGameIdBySession(WebSocketSession session) {
        // Scorre tutte le partite nella mappa
        for (Map.Entry<String, List<WebSocketSession>> entry : gameSessions.entrySet()) {
            String gameId = entry.getKey();  // gameId
            List<WebSocketSession> playersInGame = entry.getValue();  // Lista di sessioni

            // Scorre la lista delle sessioni associate al gameId
            for (WebSocketSession playerSession : playersInGame) {
                // Se la sessione corrisponde a quella passata, restituisci il gameId
                if (playerSession.equals(session)) {
                    return gameId;
                }
            }
        }

        // Se la sessione non è trovata in nessuna partita, ritorna null
        return null;
    }

    private String getPlayerNameBySession(WebSocketSession session) {
        // Scorre la mappa playerNameList per trovare la sessione corrispondente
        for (Map.Entry<String, WebSocketSession> entry : playerNameList.entrySet()) {
            String playerName = entry.getKey();  // Nome del giocatore
            WebSocketSession playerSession = entry.getValue();  // Sessione del giocatore

            // Se la sessione corrisponde a quella passata, restituisci il nome del giocatore
            if (playerSession.equals(session)) {
                return playerName;
            }
        }

        // Se la sessione non è trovata, restituisci null
        return null;
    }

    //serve per creare un messaggio di sistema in Json così che il forntend lo metta nella game console
    private void sendSystemMessage(String gameId, String content, WebSocketSession session) throws Exception {
        List<WebSocketSession> playersInGame = gameSessions.get(gameId);
        if (playersInGame == null) return;

        // Crea un messaggio di sistema come JSON
        String systemMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "system",
                "content", content
                //"timestamp", Instant.now().toString()
        ));

        //l'if serve perchè i due messaggi che inziano con... devono essere inviati solo al giocatore che crea la parita
        if(!(content.startsWith("Ti sei unito alla partita con ID ") || content.startsWith("#"))) {// Invia il messaggio a tutti i giocatori della partita
            for (WebSocketSession sessions : playersInGame) {
                sessions.sendMessage(new TextMessage(systemMessage));
            }
        }else{
            session.sendMessage(new TextMessage(systemMessage));
        }
    }

    //serve a creare un messaggio in Json per far si che il forntend riesca a capire chè per la game chat
    private void sendChatMessage(String gameId, String content) throws Exception {
        List<WebSocketSession> playersInGame = gameSessions.get(gameId);
        if (playersInGame == null) return;

        // Crea un messaggio di chat come JSON
        String chatMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "chat",
                "content", content
                //"timestamp", Instant.now().toString()
        ));

        // Invia il messaggio a tutti i giocatori della partita
        for (WebSocketSession session : playersInGame) {
            session.sendMessage(new TextMessage(chatMessage));
        }
    }

    private String generateGameId() {
        return "game-" + String.valueOf(idCounter.getAndIncrement());
    }


}
