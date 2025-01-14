package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Repository.GiocatoreRepository;
import unimib.daBancherz.NewMonopoly.Repository.PartitaRepository;
import unimib.daBancherz.NewMonopoly.Service.GameService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class GameHandler {

    @Autowired
    private MessageHandler messageHandler;  // Iniezione del bean MessageSender
    private final Map<String, List<WebSocketSession>> gameSessions = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> playerNameList = new ConcurrentHashMap<>();


    @Autowired
    private GameService gameService;
    @Autowired
    private GiocatoreRepository giocatoreRepository;
    @Autowired
    private PartitaRepository partitaRepository;

    public void handleGameMessage(String[] messageParts, WebSocketSession session) throws Exception {

        String playerName = messageParts[1];
        switch (messageParts[0]){
            case "Create":
                String difficulty = messageParts[2];
                String randomization = messageParts[3];

                playerNameList.put(playerName, session);
                createGame(playerName, difficulty, randomization, session);
                break;
            case "Partecipa":
                // Decodifica i dati per partecipare a una partita
                String gameId = messageParts[2];

                if (giocatoreRepository.existsByNomeAndIdpartita_CodiceInvito(playerName, gameId)) {
                    // TODO: Gestire il messaggio frontend per non mandarlo all'altra pagina
                    session.sendMessage(new TextMessage("Errore: Il nome del giocatore è già presente in questa partita."));
                    return; // Esce dalla funzione senza aggiungere il giocatore
                }
                playerNameList.put(playerName, session);
                joinGame(playerName, gameId, session);
                break;
        }
    }

    private void createGame(String playerName, String difficulty, String randomization, WebSocketSession session) throws Exception {

        String gameId = generateGameId();//crea l'ID del game

        gameSessions.putIfAbsent(gameId, new ArrayList<>()); // aggiunge il gameId alla lista delle partite
        List<WebSocketSession> playersInGame = gameSessions.get(gameId); //prende la lista delle sessioni dei giocatori di una determinata partita

        if (!playersInGame.contains(session)) { // controlla che la sessione non sia già in quella partita
            playersInGame.add(0, session); // Aggiungi la sessione all'inizio della lista,
                                                 //per fa si che la prima sessione sia quella dell'ADMIN
        }

        //GESTIONE MESSAGGI
        messageHandler.sendSystemMessage(gameId, "#" + gameId, gameSessions, session); //serve per inviare i messaggi da mostrare nella gameconsole
        messageHandler.notifyPlayerJoin(gameId, playerName, session, gameSessions, "ADMIN");//invia a tutti i giocatori i messaggi di partecipazione alla partita
        messageHandler.sendGameId(gameId, session);//serve per mostrare all'admin il gameId da passare agli altri giocatori per connettersi
        messageHandler.sendTypePlayer("ADMIN", session);//invia all'admin il tipo di giocatore che è

        gameService.createGameAndPlayer(playerName,difficulty, gameId);//crea la parita nel database, più informazioni in GameService

        List<Integer> pedineNonUsate = gameService.getUnusedPedineByPartita(gameId);
        messageHandler.sendUnusedPedine(pedineNonUsate, session); //invia al giocatore la lista delle pedine disponibili
    }


    private void joinGame(String playerName, String gameId, WebSocketSession session) throws Exception {
        //TODO: aggiornare il messaggio in formato Json, e gestire il messaggio in frontEnd
        if (!gameSessions.containsKey(gameId)) {
            session.sendMessage(new TextMessage("Errore: La partita con ID " + gameId + " non esiste."));
            return;
        }

        List<WebSocketSession> playersInGame = gameSessions.get(gameId);
        playersInGame.add(session);//aggiunge la sessione del giocatore alla lista di sessioni della partita a cuoi vuole partecipare
        gameService.addPlayer(playerName, gameId);//aggiunge il giocatore nel databesa alla partita assegnata
        messageHandler.notifyPlayerJoin(gameId, playerName, session, gameSessions, "giocatore");//invia a tutti i giocatori i messaggi di partecipazione alla partita
        List<Integer> pedineNonUsate = gameService.getUnusedPedineByPartita(gameId);
        messageHandler.sendUnusedPedine(pedineNonUsate, session);   //invia al giocatore la lista delle pedine disponibili
    }

    public String getGameIdBySession(WebSocketSession session) {
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

    public String getPlayerNameBySession(WebSocketSession session) {
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

    public Map<String, List<WebSocketSession>> getGameSessions() {
        return gameSessions;
    }

    //genera il codice gameID in modo incrementale partendo da game-0
    //TODO: devo controllare dal databese quale game ci sono già e restituire l'ID successivo a l'ultimo presente
    private String generateGameId() {
        int idCounter = 0;

        if(partitaRepository.findLastCodiceInvito() != null) {
            String[] lastGame = (partitaRepository.findLastCodiceInvito()).split("-");
            idCounter = Integer.parseInt(lastGame[1]);
        }
        return "game-" + ++idCounter;
    }

    @Transactional
    public void removePlayerFromGame(String gameId, WebSocketSession session) {
        List<WebSocketSession> playersInGame = gameSessions.get(gameId);

        if (playersInGame != null) {
            String player = getPlayerNameBySession(session);
            gameService.deletePlayer(gameId, player);
            playersInGame.remove(session); // Rimuove la sessione dalla lista dei giocatori

            // Se non ci sono più giocatori nella partita, rimuovi completamente la partita
            if (playersInGame.isEmpty()) {
                partitaRepository.deleteByCodiceInvito(gameId);
                gameSessions.remove(gameId);
                System.out.println("Partita con ID " + gameId + " rimossa poiché non ci sono più giocatori.");
            }
        }

        // Rimuove il giocatore dalla mappa dei nomi
        playerNameList.values().removeIf(existingSession -> existingSession.equals(session));
    }


    public void notifyPlayerDisconnected(String gameId, String playerName) throws Exception {
        List<WebSocketSession> playersInGame = gameSessions.get(gameId);

        if (playersInGame == null) return;

        // Crea un messaggio di notifica come JSON
        String disconnectMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "system",
                "content", playerName + " si è disconnesso dalla partita."
        ));

        // Invia il messaggio a tutti i giocatori rimanenti nella partita
        for (WebSocketSession session : playersInGame) {
            session.sendMessage(new TextMessage(disconnectMessage));
        }

        System.out.println("Giocatore " + playerName + " disconnesso dalla partita con ID " + gameId + ".");
    }

    public void choosePedina(String[] messageParts, WebSocketSession session) {
        String idPedina = messageParts[1];
        String idPartita = messageParts[2];

        String playerName = getPlayerNameBySession(session);    //tropo il giocatore associato alla sessione

        giocatoreRepository.updatePedinaForGiocatore(playerName, Integer.parseInt(idPedina), Integer.parseInt(idPartita));  //Assegna la pedina al giocatore nel database

    }
}
