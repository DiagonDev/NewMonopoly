package unimib.daBancherz.NewMonopoly;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Component
public class WebSocketConnectionHandler implements WebSocketHandler {

    private final Map<String, WebSocketSession> playerSessions = new ConcurrentHashMap<>();
    private final Map<String, List<WebSocketSession>> gameSessions = new ConcurrentHashMap<>(); // l'insieme delle partite con i suoi giocatori


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Non facciamo nulla qui, perché il nome del giocatore arriva tramite il messaggio "register" dopo che la connessione è stabilita
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // Decodifica il messaggio ricevuto
        String payload = message.getPayload().toString();

        // Se il messaggio è del tipo "Create:playerName:difficulty:randomization", estrai il nome del giocatore
        if (payload.startsWith("Create:")) {

            String[] parts = payload.split(":");
            // Assegna i parametri alle variabili corrispondenti
            String playerName = parts[1];
            System.out.println(playerName);
            String difficulty = parts[2];
            System.out.println(difficulty);
            String randomization = parts[3];
            System.out.println(randomization);
            String gameId = generateGameId(); // genera l'ID della partita
            System.out.println("ID partita generato: " + gameId);

            /*
            if (playerSessions.containsKey(playerName)) {
                WebSocketSession existingSession = playerSessions.get(playerName);
                if (existingSession.isOpen()) {
                    // Se il giocatore è già connesso, invia un messaggio di errore e chiudi la connessione
                    session.sendMessage(new TextMessage("Sei già connesso alla partita!"));
                    session.close();  // Chiudiamo la nuova connessione
                    return;
                } else {
                    // Se la sessione esistente è chiusa, la rimuoviamo
                    playerSessions.remove(playerName);
                }
            }

            playerSessions.put(playerName, session);
            System.out.println("Giocatore connesso: " + playerName);
            */

            // Associa il giocatore alla partita
            gameSessions.putIfAbsent(gameId, new ArrayList<>());
            gameSessions.get(gameId).add(session);


            // Invia il messaggio di ID partita a tutti i giocatori connessi
            broadcastGameId(gameId);
        } else if (payload.startsWith("Partecipa:")) {
            // Decodifica il messaggio
            String[] parts = payload.split(":");
            String playerName = parts[1];  // Nome del giocatore
            String gameId = parts[2];      // Codice partita fornito

            // Controlla se la partita esiste
            if (!gameSessions.containsKey(gameId)) {
                session.sendMessage(new TextMessage("Errore: la partita con ID " + gameId + " non esiste."));
                return;
            }

            List<WebSocketSession> playersInGame = gameSessions.get(gameId);
            /*
            // Verifica se il giocatore è già nella partita
            boolean alreadyInGame = playersInGame.stream()
                    .anyMatch(s -> extractPlayerName(s).equals(playerName));

            if (alreadyInGame) {
                session.sendMessage(new TextMessage("Errore: sei già connesso a questa partita!"));
                return;
            }
            */

            // Aggiungi la sessione del giocatore alla partita
            playersInGame.add(session);
            session.sendMessage(new TextMessage("Ti sei unito alla partita con ID " + gameId + " con successo!"));


            //System.out.println("Giocatore " + playerName + " si è unito alla partita con ID " + gameId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // Rimuovi la sessione in caso di errore
        String playerName = extractPlayerName(session);
        playerSessions.remove(playerName);
        System.out.println("Errore nella connessione del giocatore: " + playerName);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Rimuovi la sessione quando il giocatore si disconnette
        String playerName = extractPlayerName(session);
        playerSessions.remove(playerName);
        System.out.println("Giocatore disconnesso: " + playerName);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false; // Non gestiamo i messaggi parziali
    }

    private String extractPlayerName(WebSocketSession session) {
        // Questo metodo può essere usato per estrarre il nome del giocatore dalla sessione, ma in questo caso il nome viene passato nel messaggio
        return session.getId();  // Una soluzione temporanea, ma può essere migliorata
    }

    private void broadcastGameId(String gameId) {
        // Invia l'ID della partita a tutte le sessioni dei giocatori connessi
        for (WebSocketSession playerSession : playerSessions.values()) {
            try {
                playerSession.sendMessage(new TextMessage("ID partita: " + gameId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastPlayerParticipate(String playerName, List<WebSocketSession> playersInGame, WebSocketSession session) throws IOException {
        for (WebSocketSession playerSession : playersInGame) {
            if (!playerSession.equals(session)) {
                playerSession.sendMessage(new TextMessage(playerName + " si è unito alla partita!"));
            }
        }
    }

    private String generateGameId() {
        // Qui puoi generare un ID partita unico (ad esempio con UUID)
        return "game-" + java.util.UUID.randomUUID().toString();
    }

}