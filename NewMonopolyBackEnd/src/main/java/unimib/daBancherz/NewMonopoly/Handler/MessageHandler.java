package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.IdCasella;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.Importo;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.PagaPossedimenti;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Imprevisto;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Probabilita;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.*;
import unimib.daBancherz.NewMonopoly.dataBase.Service.GameService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MessageHandler {

    private final GameService gameService;
    private final PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    private final GiocatoreRepository giocatoreRepository;
    private final PartitaProbabilitaRepository partitaProbabilitaRepository;
    private final ProbabilitaRepository probabilitaRepository;
    private final PartitaImprevistoRepository partitaImprevistoRepository;
    private final ImprevistoRepository imprevistoRepository;
    GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();

    public MessageHandler(GameService gameService, PartitaCasellaPrezzoproprietaRepository pCPPRepository, GiocatoreRepository giocatoreRepository, PartitaProbabilitaRepository partitaProbabilitaRepository, ProbabilitaRepository probabilitaRepository, PartitaImprevistoRepository partitaImprevistoRepository, ProbabilitaRepository probabilitaRepository1, PartitaImprevistoRepository partitaImprevistoRepository1, ImprevistoRepository imprevistoRepository) {
        this.gameService = gameService;
        this.pCPPRepository = pCPPRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.partitaProbabilitaRepository = partitaProbabilitaRepository;
        this.probabilitaRepository = probabilitaRepository;
        this.partitaImprevistoRepository = partitaImprevistoRepository;
        this.imprevistoRepository = imprevistoRepository;
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
    public void sendBoxUsage(String playerName, WebSocketSession session, int posizione, String gameId, Map<String, List<WebSocketSession>> gameSessions ,Integer pawnId) throws Exception{

        String typeBox = pCPPRepository.findTipoByPosizione(posizione, gameId);
        String nomeCasella = pCPPRepository.findNomeCasellaByPosizioneAndGameId(posizione, gameId);
        String proprietario;
        String descrizione;
        String tipoAzione;
        Object parametri;
        int prezzoCasella, prezzoAffitto, nStazione, nSocietà;

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
            case"Stazione", "Società":
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
                    if(typeBox.equals("Stazione")){
                        nStazione = pCPPRepository.countProprieta(playerName, typeBox, gameId);
                        prezzoAffitto = 25 * nStazione;
                        giocatoreRepository.aggiornamentoSaldo(playerName, gameId, prezzoAffitto);
                        giocatoreRepository.aggiornamentoSaldo(proprietario, gameId, -prezzoAffitto);
                    }else{
                        nSocietà = pCPPRepository.countProprieta(playerName, typeBox, gameId);
                        prezzoAffitto = 100 * nSocietà;
                        giocatoreRepository.aggiornamentoSaldo(playerName, gameId, prezzoAffitto);
                        giocatoreRepository.aggiornamentoSaldo(proprietario, gameId, -prezzoAffitto);
                    }
                }
                break;
            case"InPrigione":
                gameBoard.setPlayerPosition(gameId, playerName, 11);//aggiorna la posizione del giocatore
                sendPawnMove(pawnId, playerName, 11, gameSessions, gameId);
                gameBoard.setPlayerPrison(gameId, playerName, true);
                break;
            case"Imprevisto":
                descrizione = partitaImprevistoRepository.findDescrizioneImprevsto(gameId);
                if (descrizione==null) {
                    partitaImprevistoRepository.setUtilizzatoFalse(gameId);
                    descrizione = partitaImprevistoRepository.findDescrizioneImprevsto(gameId);
                }
                Imprevisto imprevisto = imprevistoRepository.findByDescrizione(descrizione);
                tipoAzione = imprevisto.getTipoAzione();
                parametri = imprevisto.getParametroDeserializzato();

                gestisciAzione(tipoAzione, parametri, gameId, playerName, posizione, pawnId, gameSessions);
                partitaProbabilitaRepository.setUtilizzatoTrue(gameId, descrizione);

                String imprevistoMessage = new ObjectMapper().writeValueAsString(Map.of(
                        "type", "draw",
                        "card", "imprevisto",
                        "description", descrizione
                ));
                session.sendMessage(new TextMessage(imprevistoMessage));

                break;
            case"Probabilità":
                descrizione = partitaProbabilitaRepository.findDescrizioneProbabilita(gameId);
                if (descrizione==null) {
                    partitaProbabilitaRepository.setUtilizzatoFalse(gameId);
                    descrizione = partitaProbabilitaRepository.findDescrizioneProbabilita(gameId);
                }
                //Messaggio descrizione
                Probabilita probabilita = probabilitaRepository.findByDescrizione(descrizione);
                tipoAzione = probabilita.getTipoAzione();
                parametri = probabilita.getParametroDeserializzato();

                gestisciAzione(tipoAzione, parametri, gameId, playerName, posizione, pawnId, gameSessions);
                partitaProbabilitaRepository.setUtilizzatoTrue(gameId, descrizione);
                String probabilitaMessage = new ObjectMapper().writeValueAsString(Map.of(
                        "type", "draw",
                        "card", "probabilità",
                        "description", descrizione
                ));
                session.sendMessage(new TextMessage(probabilitaMessage));
                break;
        }
    }

    public void gestisciAzione(String tipoAzione, Object parametri, String idPartita, String nomeGiocatore, Integer posizione, int pawnId, Map<String, List<WebSocketSession>> gameSession ) throws IOException {
        Importo importo_deserializzato;
        int importo;
        int soldi;
        IdCasella id_casellaDeserializzato;
        int id_casella;

        switch (tipoAzione) {
            case "paga_importo":
                importo_deserializzato = (Importo) parametri;
                importo = importo_deserializzato.getImporto();
                giocatoreRepository.aggiornamentoSaldo(nomeGiocatore, idPartita, importo);
                break;

            case "ricevi_importo":
                importo_deserializzato = (Importo) parametri;
                importo = importo_deserializzato.getImporto();
                giocatoreRepository.aggiornamentoSaldo(nomeGiocatore, idPartita, -importo);
                break;

            case "paga_importo_giocatore":
                importo_deserializzato = (Importo) parametri;
                importo = importo_deserializzato.getImporto();
                soldi = (giocatoreRepository.contaGiocatoriInPartita(idPartita)-1) * importo;
                giocatoreRepository.pagaImportoGiocatori(importo, idPartita, nomeGiocatore);
                giocatoreRepository.aggiornamentoSaldo(nomeGiocatore, idPartita, soldi);
                break;

            case "ricevi_importo_giocatore":
                importo_deserializzato = (Importo) parametri;
                importo = importo_deserializzato.getImporto();
                soldi = -((giocatoreRepository.contaGiocatoriInPartita(idPartita)-1) * importo);
                giocatoreRepository.aggiornamentoSaldo(nomeGiocatore, idPartita, soldi);
                giocatoreRepository.pagaImportoGiocatori(-importo, idPartita, nomeGiocatore);
                break;

            case "paga_possedimenti":
                PagaPossedimenti pagaPossedimentiDeserializzato = (PagaPossedimenti) parametri;
                int importoCasa =  pagaPossedimentiDeserializzato.getCosto_casa();
                int importoAlbergo= pagaPossedimentiDeserializzato.getCosto_abergo();
                int numCase = pCPPRepository.contaCase(nomeGiocatore, idPartita);
                int numAlberghi = pCPPRepository.contaAlberghi(nomeGiocatore, idPartita);
                int totaleDaPagare = (numCase * importoCasa) + (numAlberghi * importoAlbergo);
                giocatoreRepository.aggiornamentoSaldo(nomeGiocatore, idPartita, totaleDaPagare);
                break;

            case "sposta_avanti":
                id_casellaDeserializzato =(IdCasella) parametri;
                id_casella = id_casellaDeserializzato.getId_casella();
                if(posizione > id_casella)
                    giocatoreRepository.aggiornamentoSaldo(nomeGiocatore, idPartita, -200);
                sendPawnMove(pawnId, nomeGiocatore, id_casella, gameSession, idPartita);
                break;

            case "vai_in_prigione":
                id_casellaDeserializzato =(IdCasella) parametri;
                id_casella = id_casellaDeserializzato.getId_casella();
                sendPawnMove(pawnId, nomeGiocatore, id_casella, gameSession, idPartita);
                break;

            case "esci_prigione":
                partitaProbabilitaRepository.setGiocatore(idPartita, nomeGiocatore);
                break;
        }
    }
}
