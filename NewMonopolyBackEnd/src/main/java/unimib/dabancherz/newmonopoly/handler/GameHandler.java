package unimib.dabancherz.newmonopoly.handler;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.database.entity.Casella;
import unimib.dabancherz.newmonopoly.database.entity.Giocatore;
import unimib.dabancherz.newmonopoly.database.repository.CasellaRepository;
import unimib.dabancherz.newmonopoly.singleton.GameBoardWrapper;
import unimib.dabancherz.newmonopoly.database.repository.GiocatoreRepository;
import unimib.dabancherz.newmonopoly.database.repository.PartitaRepository;
import unimib.dabancherz.newmonopoly.database.repository.PedinaRepository;
import unimib.dabancherz.newmonopoly.database.service.GameService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameHandler {

    private final MessageService messageService;
    private final GameService gameService;
    private final GiocatoreRepository giocatoreRepository;
    private final PartitaRepository partitaRepository;
    private final PedinaRepository pedinaRepository;
    private final CasellaRepository casellaRepository;

    private final Map<String, List<WebSocketSession>> gameSessions = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> playerNameList = new ConcurrentHashMap<>();

    private final GameBoardWrapper gameBoardWrapper;
    private List<Integer> pedineNonUsate = new ArrayList<>();

    // **Constructor Injection**
    public GameHandler(MessageService messageService,
                       GameService gameService,
                       GiocatoreRepository giocatoreRepository,
                       PartitaRepository partitaRepository,
                       PedinaRepository pedinaRepository, CasellaRepository casellaRepository,
                       GameBoardWrapper gameBoardWrapper) {
        this.messageService = messageService;
        this.gameService = gameService;
        this.giocatoreRepository = giocatoreRepository;
        this.partitaRepository = partitaRepository;
        this.pedinaRepository = pedinaRepository;
        this.casellaRepository = casellaRepository;
        this.gameBoardWrapper = gameBoardWrapper;
    }

    public void createGame(String[] messageParts, WebSocketSession session) throws Exception {
        String playerName = messageParts[1];
        String difficulty = messageParts[2];
        String randomization = messageParts[3];
        playerNameList.put(playerName, session);
        String gameId = generateGameId();//crea l'ID del game

        gameSessions.putIfAbsent(gameId, new ArrayList<>()); // aggiunge il gameId alla lista delle partite
        List<WebSocketSession> playersInGame = gameSessions.get(gameId); //prende la lista delle sessioni dei giocatori di una determinata partita

        if (!playersInGame.contains(session)) { // controlla che la sessione non sia già in quella partita
            playersInGame.add(0, session); // Aggiungi la sessione all'inizio della lista,
            //per fa si che la prima sessione sia quella dell'ADMIN
        }

        gameBoardWrapper.createGame(gameId);//crea il singleton per la partita con codicePartita = gameId
        gameBoardWrapper.setPlayerPosition(gameId, playerName, 1);//imposta nel singleton che il giocatore parte dalla casella 1
        gameService.createGameAndPlayer(playerName, difficulty, randomization, gameId);//crea la partita nel database, più informazioni in GameService
        List<Casella> boxOrder = casellaRepository.findByOrder(gameId);

        //GESTIONE MESSAGGI
        messageService.sendBoxOrderMessage(boxOrder, gameSessions, gameId); //serve per mandare le caselle ordinate
        messageService.sendGameId(gameId, session);//serve per mostrare all'admin il gameId da passare agli altri giocatori per connettersi
        messageService.sendSystemMessage(gameId, "#" + gameId, gameSessions, session); //serve per inviare i messaggi da mostrare nella game console
        messageService.notifyPlayerJoin(gameId, playerName, session, gameSessions, "ADMIN");//invia a tutti i giocatori i messaggi di partecipazione alla partita
        messageService.sendTypePlayer("ADMIN", session);//invia all'admin il tipo di giocatore che è

        pedineNonUsate = pedinaRepository.findUnusedPedineByPartita(gameId);
        messageService.sendUnusedPedine(pedineNonUsate, gameSessions, gameId); //invia al giocatore la lista delle pedine disponibili
    }

    public void joinGame(String[] messageParts, WebSocketSession session) throws Exception {
        String playerName = messageParts[1];
        String gameId = messageParts[2];

        if (giocatoreRepository.existsByNomeAndIdpartita_CodiceInvito(playerName, gameId)) {
            messageService.sendErrorMessage(session);
            return; // Esce dalla funzione senza aggiungere il giocatore
        }

        if (!gameSessions.containsKey(gameId)) {
            messageService.sendErrorGameIdMessage(session, gameId);
            return;
        }

        playerNameList.put(playerName, session);
        gameBoardWrapper.setPlayerPosition(gameId, playerName, 1);

        List<WebSocketSession> playersInGame = gameSessions.get(gameId);
        playersInGame.add(session);//aggiunge la sessione del giocatore alla lista di sessioni della partita a cuoi vuole partecipare
        gameService.addPlayer(playerName, gameId);//aggiunge il giocatore nel database alla partita assegnata
        messageService.notifyPlayerJoin(gameId, playerName, session, gameSessions, "giocatore");//invia a tutti i giocatori i messaggi di partecipazione alla partita

        pedineNonUsate = gameService.getUnusedPedineByPartita(gameId);
        messageService.sendUnusedPedine(pedineNonUsate, gameSessions, gameId);   //invia al giocatore la lista delle pedine disponibili

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

    public WebSocketSession getSessionByPlayerName(String playerName, String gameId) {
        // Scorre la mappa playerNameList per trovare il nome corrispondente e l'ID della partita
        for (Map.Entry<String, WebSocketSession> entry : playerNameList.entrySet()) {
            String key = entry.getKey();  // Nome del giocatore, potenzialmente connesso a un ID di partita
            WebSocketSession session = entry.getValue();  // Sessione WebSocket

            // Verifica che il nome e l'ID della partita siano corretti
            if (key.equals(playerName) && getGameIdBySession(session).equals(gameId))
                return session; // Restituisce la sessione corrispondente
        }

        // Se non trova la sessione, restituisce null
        return null;
    }

    public Map<String, List<WebSocketSession>> getGameSessions() {
        return gameSessions;
    }

    //genera il codice gameID in modo incrementale partendo da game-0
    private String generateGameId() {
        int counter = 0;

        if (partitaRepository.findLastCodiceInvito() != null) {
            String[] lastGame = (partitaRepository.findLastCodiceInvito()).split("-");
            counter = Integer.parseInt(lastGame[1]);
        }
        return "game-" + ++counter;
    }

    @Transactional
    public void removePlayerFromGame(String gameId, WebSocketSession session) {
        List<WebSocketSession> playersInGame = gameSessions.get(gameId);

        if (playersInGame != null) {
            String player = getPlayerNameBySession(session);
            gameService.deletePlayer(gameId, player);
            playersInGame.remove(session); // Rimuove la sessione dalla lista dei giocatori
            gameBoardWrapper.removePlayerFromGame(gameId, player);
            // Se non ci sono più giocatori nella partita, rimuovi completamente la partita
            if (playersInGame.isEmpty()) {
                partitaRepository.deleteByCodiceInvito(gameId);
                gameSessions.remove(gameId);
                gameBoardWrapper.removePlayerFromGame(gameId, player);
            }
        }

        // Rimuove il giocatore dalla mappa dei nomi
        playerNameList.values().removeIf(existingSession -> existingSession.equals(session));
    }

    public void choosePedina(String[] messageParts, WebSocketSession session) throws Exception {
        String idPedina = messageParts[1];
        String gameId = getGameIdBySession(session);
        String playerName = getPlayerNameBySession(session);    //tropo il giocatore associato alla sessione

        giocatoreRepository.updatePedinaForGiocatore(playerName, Integer.parseInt(idPedina), gameId);  //Assegna la pedina al giocatore nel database
        pedineNonUsate = gameService.getUnusedPedineByPartita(gameId);
        messageService.sendUnusedPedine(pedineNonUsate, gameSessions, gameId);
        sendPositionPawn(gameId, session);
        messageService.sendPawnMove(Integer.parseInt(idPedina), playerName, 1, gameSessions, gameId);   // Invia un messaggio per spostare la pedina sul via
    }


    public void sendPositionPawn(String gameId, WebSocketSession session) throws IOException, InterruptedException {
        List<Giocatore> giocatori = giocatoreRepository.findGiocatoreWithPedina(gameId);

        for (Giocatore giocatore : giocatori) {
            String playerName = giocatore.getNome();
            if(!(playerName.equals(getPlayerNameBySession(session)))){
                int pawnId = giocatore.getIdpedina().getIdPedina();
                int position = gameBoardWrapper.getPlayerPosition(gameId, playerName);
                messageService.sendPlayerPawnPosition(pawnId,playerName, position, session);
            }
            Thread.sleep(20);
        }
    }
}