package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Manager.OpportunitaManager;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;
import unimib.daBancherz.NewMonopoly.database.Entity.Opportunita;
import unimib.daBancherz.NewMonopoly.database.Repository.*;
import unimib.daBancherz.NewMonopoly.database.Service.GameService;
import unimib.daBancherz.NewMonopoly.model.PlayerProperties;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class MessageHandler {

    private final GameService gameService;
    private final PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    private final GiocatoreRepository giocatoreRepository;
    private final PartitaOpportunitaRepository partitaOpportunitaRepository;
    private final OpportunitaRepository opportunitaRepository;
    private final OpportunitaManager opportunitaManager;
    GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();
    private static final String CONTENT_KEY = "content";
    private static final String TYPE_KEY = "type";
    private static final String PLAYERNAME_KEY = "playerName";
    private static final String BALANCE_KEY = "balance";
    private static final String THREADINTERRIPT_KEY = "Thread interrotto: ";
    private static final String DESCRIPTION_KEY = "description";
    private final String ESCIPRIGIONE_KEY = "esci_prigione";
    private final String IMPREVISTO_KEY = "Imprevisto";
    private final String PROBABILITA_KEY = "Probabilità";

    public MessageHandler(GameService gameService, PartitaCasellaPrezzoproprietaRepository pCPPRepository, GiocatoreRepository giocatoreRepository, PartitaOpportunitaRepository partitaOpportunitaRepository, OpportunitaRepository opportunitaRepository, OpportunitaManager opportunitaManager) {
        this.gameService = gameService;
        this.pCPPRepository = pCPPRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.partitaOpportunitaRepository = partitaOpportunitaRepository;
        this.opportunitaRepository = opportunitaRepository;
        this.opportunitaManager = opportunitaManager;
    }
    //crea il messaggio Json con il parameto che gli viene inviato
    private String createMessage(Map<String, Object> data) throws IOException {
        return new ObjectMapper().writeValueAsString(data);
    }

    //invia i messaggi a tutti
    private void sendToGame(Map<String, Object> data, Map<String, List<WebSocketSession>> gameSessions, String gameId) throws IOException {
        String message = createMessage(data);
        List<WebSocketSession> playersInGame = gameSessions.get(gameId);
        for (WebSocketSession playerSession : playersInGame) {
            playerSession.sendMessage(new TextMessage(message));
        }
    }

    //serve per creare un messaggio di sistema in Json così che il forntend lo metta nella game console
    public void sendSystemMessage(String gameId, String content, Map<String, List<WebSocketSession>> gameSessions, WebSocketSession session) throws Exception {
        List<WebSocketSession> playersInGame = gameSessions.get(gameId);
        if (playersInGame == null) return;

        //l'if serve perchè i due messaggi che iniziano con... devono essere inviati solo al giocatore che crea la partita
        if (!(content.startsWith("Ti sei unito alla partita con ID: ") || content.startsWith("#"))) {
            sendToGame(Map.of(TYPE_KEY, "system", CONTENT_KEY, content), gameSessions, gameId);
        } else {
            //sendToGame(Map.of(TYPE_KEY, "system", CONTENT_KEY, content), gameSessions, gameId, session, false);
            session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "system", CONTENT_KEY, content))));
        }
    }

    public void sendUnusedPedine(List<Integer> pedineNonUsate, Map<String, List<WebSocketSession>> gameSessions, String gameId) throws Exception {
        sendToGame(Map.of(TYPE_KEY, "pawnsAvailable", CONTENT_KEY, pedineNonUsate), gameSessions, gameId);
    }

    public void sendPawnMove(Integer pawnId, String playerName, Integer offset, Map<String, List<WebSocketSession>> gameSessions, String gameId) throws IOException {
        sendToGame(Map.of(TYPE_KEY, "pawnMove", "pawnId", pawnId, PLAYERNAME_KEY, playerName, "offset", offset), gameSessions, gameId);
    }

    public void sendJoinMessage(String playerName, Map<String, List<WebSocketSession>> gameSessions, String role, String gameId) throws IOException {
        int balance = giocatoreRepository.saldoGiocatore(playerName, gameId);
        sendToGame(Map.of(TYPE_KEY, "join", PLAYERNAME_KEY, playerName, "userRole", role), gameSessions, gameId);
        sendToGame(Map.of(TYPE_KEY, "playersList", PLAYERNAME_KEY, playerName, BALANCE_KEY, balance), gameSessions, gameId);
    }

    public void sendGameId(String gameId, WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "gameId", CONTENT_KEY, gameId))));
    }

    public void sendTypePlayer(String paleyerType, WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "user", CONTENT_KEY, paleyerType))));
    }

    public void notifyPlayerJoin(String gameId, String playerName, WebSocketSession session, Map<String, List<WebSocketSession>> gameSessions, String role) throws Exception {
        // Messaggi per il giocatore che si è unito e per tutti i partecipanti
        sendSystemMessage(gameId, "Ti sei unito alla partita con ID: " + gameId + " con successo!", gameSessions, session);
        sendSystemMessage(gameId, playerName + " si è unito alla partita!", gameSessions, session);
        sendPlayerAndBalance(gameId, playerName, session, role);
        sendJoinMessage(playerName, gameSessions, role, gameId);
    }

    public void sendPlayerAndBalance(String gameId, String playerName, WebSocketSession session, String role) throws Exception {
        List<String> playerJoined = gameService.getPlayersWithIdLowerThan(gameId, playerName);
        for (String player : playerJoined) {
            int balance = giocatoreRepository.saldoGiocatore(player, gameId);
            session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "playersList", PLAYERNAME_KEY, player, BALANCE_KEY, balance))));
            session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "join", PLAYERNAME_KEY, playerName, "userRole", role))));
            Thread.sleep(100); //serve per far si che il frontend riesca a ricevere i messaggi e a visualizzarli in tempo
        }
    }

    public void updateBalance(Map<String, List<WebSocketSession>> gameSessions, String gameId, String playerName) throws IOException {
        int balance = giocatoreRepository.saldoGiocatore(playerName, gameId);
        sendToGame(Map.of(TYPE_KEY, "playerBalance", PLAYERNAME_KEY, playerName, BALANCE_KEY, balance), gameSessions, gameId);
    }

    //posizione => il codice della cella dove il giocatore finisce dopo il lancio dadi
    public void sendBoxUsage(String playerName, WebSocketSession session, int posizione, String gameId, Map<String, List<WebSocketSession>> gameSessions ,Integer pawnId, boolean viaPay) throws Exception{
        Opportunita opportunita;
        Object parametri;
        String typeBox = pCPPRepository.findTipoByPosizione(posizione, gameId);
        String nomeCasella = pCPPRepository.findNomeCasellaByPosizioneAndGameId(posizione, gameId);
        String proprietario, descrizione, tipoAzione;
        int prezzoCasella, prezzoAffitto;

        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "nameBox", "name", nomeCasella))));

        //aggiorna i soldi quando passi dal via anche senza fermarti sopra
        if(viaPay){
            giocatoreRepository.setSaldoGiocatore(playerName, gameId, -200);
            updateBalance(gameSessions, gameId, playerName);
        }

        switch (typeBox){
            case "Via", "Posteggio", "Prigione":
                break;
            case "Tassa":
                giocatoreRepository.setSaldoGiocatore(playerName, gameId, 200);
                updateBalance(gameSessions, gameId, playerName);
                break;
            case "Proprietà", "Stazione", "Società":
                proprietario = pCPPRepository.findNomeGiocatoreByPosizioneAndGameId(posizione, gameId);
                prezzoCasella = pCPPRepository.prezzoCasella(posizione, gameId);
                if(proprietario == null)
                    session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "buy", "price", prezzoCasella, "nameBox", nomeCasella))));
                else if(!playerName.equals(proprietario)){
                    Integer idProprietario = giocatoreRepository.findIdByNomeAndPartitaCodiceInvito(proprietario, gameId);
                    Integer count = pCPPRepository.countProprieta(proprietario, typeBox, gameId);
                    prezzoAffitto = pCPPRepository.calcolaAffitto(gameId, posizione, idProprietario, count);
                    giocatoreRepository.setSaldoGiocatore(playerName, gameId, prezzoAffitto);
                    giocatoreRepository.setSaldoGiocatore(proprietario, gameId, -prezzoAffitto);

                    session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "payment", DESCRIPTION_KEY, "affitto","destination", proprietario, "payment", prezzoAffitto))));
                    updateBalance(gameSessions, gameId, playerName);
                    updateBalance(gameSessions, gameId, proprietario);
                    sendSystemMessage(gameId, playerName + " ha pagato l'affitto a " + proprietario + " di " + prezzoAffitto, gameSessions,session);
                }
                break;
            case"InPrigione":
                gameBoard.setPlayerPosition(gameId, playerName, 11);//aggiorna la posizione del giocatore
                Thread.sleep(1000);
                sendPawnMove(pawnId, playerName, 11, gameSessions, gameId);
                gameBoard.setPlayerPrison(gameId, playerName, true);
                break;
            case IMPREVISTO_KEY, PROBABILITA_KEY:
                descrizione = partitaOpportunitaRepository.findDescrizione(gameId, typeBox);
                if (descrizione==null) {
                    partitaOpportunitaRepository.setUtilizzatoFalse(gameId, typeBox);
                    descrizione = partitaOpportunitaRepository.findDescrizione(gameId, typeBox);
                }
                session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "draw", "card", typeBox,DESCRIPTION_KEY, descrizione))));
                opportunita = opportunitaRepository.findByDescrizioneAndTipo(descrizione, typeBox);
                tipoAzione = opportunita.getTipoAzione();
                parametri = opportunita.getParametroDeserializzato();
                gestisciAzione(tipoAzione, parametri, gameId, playerName, posizione, gameSessions, typeBox,session);
                partitaOpportunitaRepository.setUtilizzatoTrue(gameId, descrizione, typeBox);
                break;
            default:
                throw new IllegalArgumentException("Tipo di messaggio non supportato: " + typeBox);
        }
    }

    public void gestisciAzione(String tipoAzione, Object parametri, String idPartita, String nomeGiocatore, Integer posizione, Map<String, List<WebSocketSession>> gameSessions, String typeBox, WebSocketSession session) throws Exception {
        //WebSocketSession session = gameHandler.getSessionByPlayerName(nomeGiocatore, idPartita);
        Integer pawnId = giocatoreRepository.findPedinaFromGiocatore(nomeGiocatore, idPartita);
        int idCasella;
        switch (tipoAzione) {
            case "ricevi_importo", "paga_importo":
                opportunitaManager.gestisciImporto(parametri, idPartita, nomeGiocatore, gameSessions);
                updateBalance(gameSessions, idPartita, nomeGiocatore);
                break;
            case "paga_importo_giocatore", "ricevi_importo_giocatore":
                opportunitaManager.gestisciPagamentoGiocatori(tipoAzione, parametri, idPartita, nomeGiocatore, gameSessions);
                List<String> giocatoriPartita = giocatoreRepository.findGiocatori(idPartita);
                for (String nome : giocatoriPartita) {
                    updateBalance(gameSessions, idPartita, nome);
                }
                break;
            case "paga_possedimenti":
                opportunitaManager.gestisciPagamentoPossedimenti(parametri, idPartita, nomeGiocatore, gameSessions);
                updateBalance(gameSessions, idPartita, nomeGiocatore);
                break;
            case "sposta_avanti":
                idCasella = opportunitaManager.gestisciSpostamento(parametri, posizione, idPartita, nomeGiocatore, pawnId, gameSessions, session);
                updateBalance(gameSessions, idPartita, nomeGiocatore);
                sendPawnMove(pawnId, nomeGiocatore, idCasella, gameSessions, idPartita);
                Thread.sleep(2000);
                sendBoxUsage(nomeGiocatore, session, idCasella, idPartita, gameSessions, pawnId, false);
                break;
            case "vai_in_prigione":
                idCasella = opportunitaManager.gestisciPrigione(parametri, idPartita, nomeGiocatore, pawnId, gameSessions);
                sendPawnMove(pawnId, nomeGiocatore, idCasella, gameSessions, idPartita);
                break;
            case ESCIPRIGIONE_KEY:
                opportunitaManager.gestisciUscitaPrigione(idPartita, nomeGiocatore, typeBox);
                break;
            default:
                throw new IllegalArgumentException("Tipo di messaggio non supportato: " + tipoAzione);
        }
    }

    public void rispostaGestisciProprieta(String messaggioRisposta, WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "rispostaGestisciProprieta", CONTENT_KEY, messaggioRisposta))));
    }

    public void rispostaAggiornaProprieta(String gameId, String playerName, WebSocketSession session) throws IOException {
        List<PlayerProperties> playerPropertiesList = pCPPRepository.findPlayerProperties(gameId,playerName);
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "rispostaAggiornaProprieta", "properties", playerPropertiesList))));
    }

    public void sendErrorMessage(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "errorName"))));
    }

    public void sendErrorGameIdMessage(WebSocketSession session, String gameId) throws IOException {
        session.sendMessage(new TextMessage(createMessage(Map.of(TYPE_KEY, "errorGameId", "gameId", gameId))));
    }

    public void notifyPlayerDisconnected(String gameId, String playerName, Map<String, List<WebSocketSession>> gameSessions) throws Exception {
        List<WebSocketSession> playersInGame = gameSessions.get(gameId);
        if (playersInGame == null) return;
        sendToGame(Map.of(TYPE_KEY, "system", CONTENT_KEY, playerName + " si è disconnesso dalla partita."), gameSessions, gameId);
    }

    public String createTurnMessage (boolean turn, String playerName) throws JsonProcessingException {
        String yourTurnMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "turn",
                "content", turn,
                "playername", playerName
        ));
        return yourTurnMessage;
    }

    public void exitPrisonMessage(boolean flag, WebSocketSession session) throws IOException {
        String exitPrisonMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "exitPrison",
                "flag", flag
        ));
        session.sendMessage(new TextMessage(exitPrisonMessage));
    }

}