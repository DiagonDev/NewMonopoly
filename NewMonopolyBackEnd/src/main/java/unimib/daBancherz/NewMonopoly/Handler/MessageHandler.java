package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.GiocatoreRepository;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.PartitaCasellaPrezzoproprietaRepository;
import unimib.daBancherz.NewMonopoly.dataBase.Service.GameService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class MessageHandler {

    private final GameService gameService;
    private final PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    private final GiocatoreRepository giocatoreRepository;
    private final PartitaCasellaPrezzoproprietaRepository partitaCasellaPrezzoproprietaRepository;

    public MessageHandler(GameService gameService, PartitaCasellaPrezzoproprietaRepository pCPPRepository, GiocatoreRepository giocatoreRepository, PartitaCasellaPrezzoproprietaRepository partitaCasellaPrezzoproprietaRepository) {
        this.gameService = gameService;
        this.pCPPRepository = pCPPRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.partitaCasellaPrezzoproprietaRepository = partitaCasellaPrezzoproprietaRepository;
    }

    //serve per creare un messaggio di sistema in Json così che il forntend lo metta nella game console
    public void sendSystemMessage(String gameId, String content, Map<String, List<WebSocketSession>> gameSessions, WebSocketSession session) throws Exception {
        List<WebSocketSession> playersInGame = gameSessions.get(gameId);
        if (playersInGame == null) return;

        // Crea un messaggio di sistema come JSON
        String systemMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "system",
                "content", content
        ));

        //l'if serve perchè i due messaggi che iniziano con... devono essere inviati solo al giocatore che crea la partita
        if (!(content.startsWith("Ti sei unito alla partita con ID: ") || content.startsWith("#"))) {
            // Invia il messaggio a tutti i giocatori della partita
            for (WebSocketSession sessions : playersInGame) {
                sessions.sendMessage(new TextMessage(systemMessage));
            }
        } else {
            session.sendMessage(new TextMessage(systemMessage));
        }
    }

    public void sendUnusedPedine(List<Integer> pedineNonUsate, Map<String, List<WebSocketSession>> gameSessions, String gameId) throws Exception {

        // Crea il messaggio con le pedine non usate in formato JSON
        String pedineMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "pawnsAvailable",
                "content", pedineNonUsate
        ));

        List<WebSocketSession> playersInGame = gameSessions.get(gameId);
        for (WebSocketSession sessions : playersInGame) {
            sessions.sendMessage(new TextMessage(pedineMessage));
        }
    }

    public void sendPawnMove(Integer pawnId, String playerName, Integer offset, Map<String, List<WebSocketSession>> gameSessions, String gameId) throws IOException {
        List<WebSocketSession> playersInGame = gameSessions.get(gameId);

        String movimentoPedineMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "pawnMove",
                "pawnId", pawnId,
                "playerName", playerName,
                "offset", offset
        ));

        for (WebSocketSession sessions : playersInGame) {
            sessions.sendMessage(new TextMessage(movimentoPedineMessage));
        }
    }

    public void sendJoinMessage(String playerName, Map<String, List<WebSocketSession>> gameSessions, String role, String gameId) throws IOException {

        List<WebSocketSession> playersInGame = gameSessions.get(gameId);

        //mettere il metodo che prende dal database i nomi dei giocatori che sono entrati prima di questa sessione

        //crea il messaggio di tipo join che contiene il nome del giocatore
        String joinMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "join",
                "playerName", playerName,
                "userRole", role
        ));

        for (WebSocketSession sessions : playersInGame) {

            sessions.sendMessage(new TextMessage(joinMessage));
        }
    }

    public void sendGameId(String gameId, WebSocketSession session) throws Exception {

        String gameMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "gameId",
                "content", gameId
        ));

        session.sendMessage(new TextMessage(gameMessage));
    }

    public void sendTypePlayer(String paleyrType, WebSocketSession session) throws Exception {

        String typePlayerMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "user",
                "content", paleyrType
        ));

        session.sendMessage(new TextMessage(typePlayerMessage));
    }

    public void notifyPlayerJoin(String gameId, String playerName, WebSocketSession session, Map<String, List<WebSocketSession>> gameSessions, String role) throws Exception {

        // Messaggi per il giocatore che si è unito e per tutti i partecipanti
        sendSystemMessage(gameId, "Ti sei unito alla partita con ID: " + gameId + " con successo!", gameSessions, session);
        sendSystemMessage(gameId, playerName + " si è unito alla partita!", gameSessions, session);

        List<String> playerJoined = gameService.getPlayersWithIdLowerThan(gameId, playerName);
        if(!playerJoined.isEmpty()){
            for(String player : playerJoined){
                String playerMessage = new ObjectMapper().writeValueAsString(Map.of(
                        "type", "join",
                        "playerName", player,
                        "userRole", ""
                ));
                session.sendMessage(new TextMessage(playerMessage));

                try {
                    Thread.sleep(100); //TODO: vedere se si può diminuire il tempo
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Ripristina lo stato di interruzione del thread
                    System.out.println("Thread interrotto: " + e.getMessage());
                }

            }
        }

        sendJoinMessage(playerName, gameSessions, role, gameId);
        //sendTypePlayer("giocatore", session);
    }

    //posizione => il codice della cella dove il giocatore finisce dopo il lancio dadi
    public void sendBoxUsage(String playerName, WebSocketSession session, int posizione, String gameId) throws Exception{

        String typeBox = pCPPRepository.findTipoByPosizione(posizione, gameId);
        String nomeCasella = pCPPRepository.findNomeCasellaByPosizioneAndGameId(posizione, gameId);
        String proprietario;
        int prezzoCasella;
        int prezzoAffitto;

        String nameBoxMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "nameBox",
                "name", nomeCasella
        ));
        session.sendMessage(new TextMessage(nameBoxMessage));

        switch (typeBox){
            case "Via", "Tassa":
                prezzoCasella = pCPPRepository.prezzoCasella(posizione, gameId);
                giocatoreRepository.aggiornamentoSaldo(playerName, gameId, -prezzoCasella); //è negativo perhcè la funzione è fatta da saldo attuale - (prezzoCasella)
                break;
            case"Proprietà":
                proprietario = pCPPRepository.findNomeGiocatoreByPosizioneAndGameId(posizione, gameId);
                prezzoCasella = pCPPRepository.prezzoCasella(posizione, gameId);
                if(proprietario == null){
                    String buyBoxMessage = new ObjectMapper().writeValueAsString(Map.of(
                            "type", "buy",
                            "price", prezzoCasella,
                            "nameBox", nomeCasella
                    ));
                    session.sendMessage(new TextMessage(buyBoxMessage));

                }else if(!playerName.equals(proprietario)){
                    prezzoAffitto = pCPPRepository.affittoProprieta(gameId, posizione);
                    giocatoreRepository.aggiornamentoSaldo(playerName, gameId, prezzoAffitto);
                    giocatoreRepository.aggiornamentoSaldo(proprietario, gameId, -prezzoAffitto);

                    String payBoxMessage = new ObjectMapper().writeValueAsString(Map.of(
                            "type", "payment",
                            "description", "affitto",
                            "destination", proprietario,
                            "payment", prezzoAffitto
                    ));
                    session.sendMessage(new TextMessage(payBoxMessage));
                }
                break;
            case"Stazione":
                proprietario = pCPPRepository.findNomeGiocatoreByPosizioneAndGameId(posizione, gameId);
                prezzoCasella = pCPPRepository.prezzoCasella(posizione, gameId);
                if(proprietario == null){
                    String buyBoxMessage = new ObjectMapper().writeValueAsString(Map.of(
                            "type", "buy",
                            "price", prezzoCasella,
                            "nameBox", nomeCasella
                    ));
                    session.sendMessage(new TextMessage(buyBoxMessage));

                }else if(!playerName.equals(proprietario)){

                }
                //TODO: query che mi restituisce il numero di stazioni del proprietario
                //calcolo quante stazioni ha
                //TODO: query che mi restituisce il prezzo della casella stazione
                //calcola numero stazioni per calcolare il prezzo di affitto
                break;
            case"Società":
                proprietario = pCPPRepository.findNomeGiocatoreByPosizioneAndGameId(posizione, gameId);
                prezzoCasella = pCPPRepository.prezzoCasella(posizione, gameId);
                if(proprietario == null){
                    String buyBoxMessage = new ObjectMapper().writeValueAsString(Map.of(
                            "type", "buy",
                            "price", prezzoCasella,
                            "nameBox", nomeCasella
                    ));
                    session.sendMessage(new TextMessage(buyBoxMessage));

                }else if(!playerName.equals(proprietario)){

                }
                //TODO: query che mi restituisce il numero delle società del giocatore
                //capire se il prezzo si moltiplica in base al numero delle società
                //TODO: query che mi restituisce il prezzo della casella società
                //calcola ipoteca --> è la meta del prezzo
                //calcola numero società per calcolare il prezzo di affitto
                break;
            case"InPrigione":
                
                //mandarlo alla cella 11
                //non so se si necessario ma potrebbe servirmi in più casi
                //spostare la pedina sulla casella prigione
                break;
            case"Imprevisto":
                //TODO: query per ottenere la descrizione dell'imprevisto
                //TODO: query per aumentare o diminuire il prezzo se c'è bisogno di pagare/ricevere
                //controllare se si può usare la stessa di Probabilità
                break;
            case"Probabilità":
                //TODO: query per ottenere la descrizione della probabilità:
                //descrizione=partitaProbabilitaRepository.findDescrizioneProbabilita(gameId)
                //partitaProbabilitaRepository.setUtilizzatoTrue(gameId, descrizione)

                //TODO: query per vedere se sono state usate tutte le probabilita

                //TODO: query per aumentare o diminuire il prezzo se c'è bisogno di pagare/ricevere:

                break;

        }
    }
}
