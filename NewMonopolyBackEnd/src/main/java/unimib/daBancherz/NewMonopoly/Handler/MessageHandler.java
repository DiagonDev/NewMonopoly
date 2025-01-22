package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.IdCasella;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.Importo;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.PagaPossedimenti;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.ClassiParametri.TipoCasella;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Imprevisto;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Probabilita;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.*;
import unimib.daBancherz.NewMonopoly.dataBase.Service.GameService;
import unimib.daBancherz.NewMonopoly.model.PlayerProperties;

import java.io.IOException;
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
    private static final String CONTENT_KEY = "content";
    private static final String TYPE_KEY = "type";
    private static final String PLAYERNAME_KEY = "playerName";
    private static final String BALANCE_KEY = "balance";
    private static final String THREADINTERRIPT_KEY = "Thread interrotto: ";
    private static final String DESCRIPTION_KEY = "description";

    private final String esciPrigione = "esci_prigione";

    public MessageHandler(GameService gameService, PartitaCasellaPrezzoproprietaRepository pCPPRepository, GiocatoreRepository giocatoreRepository, PartitaProbabilitaRepository partitaProbabilitaRepository, ProbabilitaRepository probabilitaRepository, PartitaImprevistoRepository partitaImprevistoRepository, ImprevistoRepository imprevistoRepository) {
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
                TYPE_KEY, "system",
                CONTENT_KEY, content
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
                TYPE_KEY, "pawnsAvailable",
                CONTENT_KEY, pedineNonUsate
        ));

        List<WebSocketSession> playersInGame = gameSessions.get(gameId);
        for (WebSocketSession sessions : playersInGame) {
            sessions.sendMessage(new TextMessage(pedineMessage));
        }
    }

    public void sendPawnMove(Integer pawnId, String playerName, Integer offset, Map<String, List<WebSocketSession>> gameSessions, String gameId) throws IOException {
        List<WebSocketSession> playersInGame = gameSessions.get(gameId);

        String movimentoPedineMessage = new ObjectMapper().writeValueAsString(Map.of(
                TYPE_KEY, "pawnMove",
                "pawnId", pawnId,
                PLAYERNAME_KEY, playerName,
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
                TYPE_KEY, "join",
                PLAYERNAME_KEY, playerName,
                "userRole", role
        ));
        int balance = giocatoreRepository.saldoGiocatore(playerName,gameId);
        String playerMessage = new ObjectMapper().writeValueAsString(Map.of(
                TYPE_KEY, "playersList",
                PLAYERNAME_KEY, playerName,
                BALANCE_KEY, balance
        ));

        for (WebSocketSession sessions : playersInGame) {

            sessions.sendMessage(new TextMessage(joinMessage));
            sessions.sendMessage(new TextMessage(playerMessage));
        }



    }

    public void sendGameId(String gameId, WebSocketSession session) throws Exception {

        String gameMessage = new ObjectMapper().writeValueAsString(Map.of(
                TYPE_KEY, "gameId",
                CONTENT_KEY, gameId
        ));

        session.sendMessage(new TextMessage(gameMessage));
    }

    public void sendTypePlayer(String paleyrType, WebSocketSession session) throws Exception {

        String typePlayerMessage = new ObjectMapper().writeValueAsString(Map.of(
                TYPE_KEY, "user",
                CONTENT_KEY, paleyrType
        ));

        session.sendMessage(new TextMessage(typePlayerMessage));
    }

    public void notifyPlayerJoin(String gameId, String playerName, WebSocketSession session, Map<String, List<WebSocketSession>> gameSessions, String role) throws Exception {

        // Messaggi per il giocatore che si è unito e per tutti i partecipanti
        sendSystemMessage(gameId, "Ti sei unito alla partita con ID: " + gameId + " con successo!", gameSessions, session);
        sendSystemMessage(gameId, playerName + " si è unito alla partita!", gameSessions, session);
        sendPlayerAndBalance(gameId, playerName, session);
        sendJoinMessage(playerName, gameSessions, role, gameId);

    }

    public void sendPlayerAndBalance(String gameId, String playerName, WebSocketSession session) throws Exception {
        List<String> playerJoined = gameService.getPlayersWithIdLowerThan(gameId, playerName);
        if(!playerJoined.isEmpty()){
            for(String player : playerJoined){
                int balance = giocatoreRepository.saldoGiocatore(player,gameId);
                String playerMessage = new ObjectMapper().writeValueAsString(Map.of(
                        TYPE_KEY, "playersList",
                        PLAYERNAME_KEY, player,
                        BALANCE_KEY, balance
                ));
                session.sendMessage(new TextMessage(playerMessage));

                try {
                    Thread.sleep(100); //TODO: vedere se si può diminuire il tempo
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Ripristina lo stato di interruzione del thread
                    System.out.println(THREADINTERRIPT_KEY + e.getMessage());
                }

            }
        }
    }

    public void updateBalance(Map<String, List<WebSocketSession>> gameSessions, String gameId, String playerName) throws IOException {
        List<WebSocketSession> playersInGame = gameSessions.get(gameId);

            for (WebSocketSession session : playersInGame) {
                int balance = giocatoreRepository.saldoGiocatore(playerName, gameId);
                String playerMessage = new ObjectMapper().writeValueAsString(Map.of(
                        TYPE_KEY, "playerBalance",
                        PLAYERNAME_KEY, playerName,
                        BALANCE_KEY, balance
                ));
                session.sendMessage(new TextMessage(playerMessage));
            }
    }

    //posizione => il codice della cella dove il giocatore finisce dopo il lancio dadi
    public void sendBoxUsage(String playerName, WebSocketSession session, int posizione, String gameId, Map<String, List<WebSocketSession>> gameSessions ,Integer pawnId, boolean viaPay) throws Exception{

        String typeBox = pCPPRepository.findTipoByPosizione(posizione, gameId);
        String nomeCasella = pCPPRepository.findNomeCasellaByPosizioneAndGameId(posizione, gameId);
        String proprietario;
        String descrizione;
        String tipoAzione;
        Object parametri;
        int prezzoCasella, prezzoAffitto;

        String nameBoxMessage = new ObjectMapper().writeValueAsString(Map.of(
                TYPE_KEY, "nameBox",
                "name", nomeCasella
        ));
        session.sendMessage(new TextMessage(nameBoxMessage));

        //aggiorna i soldi quando passi dal via anche senza fermarti sopra
        if(viaPay){
            giocatoreRepository.setSaldoGiocatore(playerName, gameId, -200);
            updateBalance(gameSessions, gameId, playerName);
        }

        switch (typeBox){
            case "Via":
                break;
            case "Tassa":
                giocatoreRepository.setSaldoGiocatore(playerName, gameId, 200);
                updateBalance(gameSessions, gameId, playerName);
                break;
            case "Proprietà", "Stazione", "Società":
                proprietario = pCPPRepository.findNomeGiocatoreByPosizioneAndGameId(posizione, gameId);
                prezzoCasella = pCPPRepository.prezzoCasella(posizione, gameId);
                if(proprietario == null){
                    String buyBoxMessage = new ObjectMapper().writeValueAsString(Map.of(
                            TYPE_KEY, "buy",
                            "price", prezzoCasella,
                            "nameBox", nomeCasella
                    ));
                    session.sendMessage(new TextMessage(buyBoxMessage));

                }else if(!playerName.equals(proprietario)){
                    Integer idProprietario = giocatoreRepository.findIdByNomeAndPartitaCodiceInvito(proprietario, gameId);
                    Integer count = pCPPRepository.countProprieta(proprietario, typeBox, gameId);
                    prezzoAffitto = pCPPRepository.calcolaAffitto(gameId, posizione, idProprietario, count);
                    giocatoreRepository.setSaldoGiocatore(playerName, gameId, prezzoAffitto);
                    giocatoreRepository.setSaldoGiocatore(proprietario, gameId, -prezzoAffitto);

                    String payBoxMessage = new ObjectMapper().writeValueAsString(Map.of(
                            TYPE_KEY, "payment",
                            DESCRIPTION_KEY, "affitto",
                            "destination", proprietario,
                            "payment", prezzoAffitto
                    ));
                    session.sendMessage(new TextMessage(payBoxMessage));
                    updateBalance(gameSessions, gameId, playerName);
                    updateBalance(gameSessions, gameId, proprietario);
                }
                break;
            case"InPrigione":
                gameBoard.setPlayerPosition(gameId, playerName, 11);//aggiorna la posizione del giocatore
                try {
                    Thread.sleep(1500); //TODO: vedere se si può diminuire il tempo
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Ripristina lo stato di interruzione del thread
                    System.out.println(THREADINTERRIPT_KEY + e.getMessage());
                }
                sendPawnMove(pawnId, playerName, 11, gameSessions, gameId);
                gameBoard.setPlayerPrison(gameId, playerName, true);
                break;
            case"Imprevisto":
                descrizione = partitaImprevistoRepository.findDescrizioneImprevisto(gameId);
                if (descrizione==null) {
                    partitaImprevistoRepository.setUtilizzatoFalse(gameId);
                    descrizione = partitaImprevistoRepository.findDescrizioneImprevisto(gameId);
                }

                String imprevistoMessage = new ObjectMapper().writeValueAsString(Map.of(
                        TYPE_KEY, "draw",
                        "card", "imprevisto",
                        DESCRIPTION_KEY, descrizione
                ));
                session.sendMessage(new TextMessage(imprevistoMessage));

                Imprevisto imprevisto = imprevistoRepository.findByDescrizione(descrizione);
                tipoAzione = imprevisto.getTipoAzione();
                parametri = imprevisto.getParametroDeserializzato();
                gestisciAzione(tipoAzione, parametri, gameId, playerName, posizione, pawnId, gameSessions, typeBox, session);
                partitaImprevistoRepository.setUtilizzatoTrue(gameId, descrizione);
                break;
            case"Probabilità":
                descrizione = partitaProbabilitaRepository.findDescrizioneProbabilita(gameId);
                if (descrizione==null) {
                    partitaProbabilitaRepository.setUtilizzatoFalse(gameId);
                    descrizione = partitaProbabilitaRepository.findDescrizioneProbabilita(gameId);
                }

                String probabilitaMessage = new ObjectMapper().writeValueAsString(Map.of(
                        TYPE_KEY, "draw",
                        "card", "probabilità",
                        DESCRIPTION_KEY, descrizione
                ));
                session.sendMessage(new TextMessage(probabilitaMessage));

                Probabilita probabilita = probabilitaRepository.findByDescrizione(descrizione);
                tipoAzione = probabilita.getTipoAzione();
                parametri = probabilita.getParametroDeserializzato();

                gestisciAzione(tipoAzione, parametri, gameId, playerName, posizione, pawnId, gameSessions, typeBox, session);
                partitaProbabilitaRepository.setUtilizzatoTrue(gameId, descrizione);
                break;
            default:
                throw new IllegalArgumentException("Tipo di messaggio non supportato: " + typeBox);

        }
    }

    public void gestisciAzione(String tipoAzione, Object parametri, String idPartita, String nomeGiocatore, Integer posizione, int pawnId, Map<String, List<WebSocketSession>> gameSessions, String typeBox, WebSocketSession session) throws Exception {
        switch (tipoAzione) {
            case "ricevi_importo", "paga_importo":
                gestisciImporto(parametri, idPartita, nomeGiocatore, gameSessions);
                break;
            case "paga_importo_giocatore", "ricevi_importo_giocatore":
                gestisciPagamentoGiocatori(tipoAzione, parametri, idPartita, nomeGiocatore, gameSessions);
                break;
            case "paga_possedimenti":
                gestisciPagamentoPossedimenti(parametri, idPartita, nomeGiocatore, gameSessions);
                break;
            case "sposta_avanti":
                gestisciSpostamento(parametri, posizione, idPartita, nomeGiocatore, pawnId, gameSessions, session);
                break;
            case "vai_in_prigione":
                gestisciPrigione(parametri, idPartita, nomeGiocatore, pawnId, gameSessions);
                break;
            case esciPrigione:
                gestisciUscitaPrigione(idPartita, nomeGiocatore, typeBox);
                break;
            default:
                throw new IllegalArgumentException("Tipo di messaggio non supportato: " + tipoAzione);
        }
    }

    private void gestisciImporto(Object parametri, String idPartita, String nomeGiocatore, Map<String, List<WebSocketSession>> gameSessions) throws IOException {
        Importo importoDeserializzato = (Importo) parametri;
        int importo = importoDeserializzato.getImporto();

        giocatoreRepository.setSaldoGiocatore(nomeGiocatore, idPartita, importo);
        updateBalance(gameSessions, idPartita, nomeGiocatore);
    }

    private void gestisciPagamentoGiocatori(String tipoAzione, Object parametri, String idPartita, String nomeGiocatore, Map<String, List<WebSocketSession>> gameSessions) throws IOException {
        Importo importoDeserializzato = (Importo) parametri;
        int importo = importoDeserializzato.getImporto();
        int soldi = (giocatoreRepository.contaGiocatoriInPartita(idPartita) - 1) * importo;

        if (tipoAzione.equals("paga_importo_giocatore")) {
            giocatoreRepository.pagaImportoGiocatori(importo, idPartita, nomeGiocatore);
            giocatoreRepository.setSaldoGiocatore(nomeGiocatore, idPartita, soldi);
        } else {
            giocatoreRepository.setSaldoGiocatore(nomeGiocatore, idPartita, -soldi);
            giocatoreRepository.pagaImportoGiocatori(-importo, idPartita, nomeGiocatore);
        }

        List<String> giocatoriPartita = giocatoreRepository.findGiocatori(idPartita);
        for (String nome : giocatoriPartita) {
            updateBalance(gameSessions, idPartita, nome);
        }
    }

    private void gestisciPagamentoPossedimenti(Object parametri, String idPartita, String nomeGiocatore, Map<String, List<WebSocketSession>> gameSessions) throws IOException {
        PagaPossedimenti pagaPossedimentiDeserializzato = (PagaPossedimenti) parametri;
        int importoCasa = pagaPossedimentiDeserializzato.getCosto_casa();
        int importoAlbergo = pagaPossedimentiDeserializzato.getCosto_abergo();

        int numCase = pCPPRepository.contaCaseTot(nomeGiocatore, idPartita);
        int numAlberghi = pCPPRepository.contaAlberghiTot(nomeGiocatore, idPartita);

        int totaleDaPagare = (numCase * importoCasa) + (numAlberghi * importoAlbergo);
        giocatoreRepository.setSaldoGiocatore(nomeGiocatore, idPartita, totaleDaPagare);
        updateBalance(gameSessions, idPartita, nomeGiocatore);
    }

    private void gestisciSpostamento(Object parametri, int posizione, String idPartita, String nomeGiocatore, int pawnId, Map<String, List<WebSocketSession>> gameSessions, WebSocketSession session) throws Exception {
        int idCasella;

        if (parametri instanceof IdCasella idCasellaDeserializzato) {
            idCasella = idCasellaDeserializzato.getId_casella();

            if (posizione > idCasella) {
                giocatoreRepository.setSaldoGiocatore(nomeGiocatore, idPartita, -200);
                updateBalance(gameSessions, idPartita, nomeGiocatore);
            }
        } else if (parametri instanceof TipoCasella tipoCasellaDeserializzato) {
            idCasella = pCPPRepository.findNextCasellaByTipo(tipoCasellaDeserializzato.getTipo_casella(), posizione, idPartita);
        } else {
            return;
        }

        gameBoard.setPlayerPosition(idPartita, nomeGiocatore, idCasella);
        sendPawnMove(pawnId, nomeGiocatore, idCasella, gameSessions, idPartita);
        Thread.sleep(4000); // TODO: valutare riduzione tempo
        sendBoxUsage(nomeGiocatore, session, idCasella, idPartita, gameSessions, pawnId, false);
    }

    private void gestisciPrigione(Object parametri, String idPartita, String nomeGiocatore, int pawnId, Map<String, List<WebSocketSession>> gameSessions) throws IOException {
        IdCasella idCasellaDeserializzato = (IdCasella) parametri;
        int idCasella = idCasellaDeserializzato.getId_casella();

        gameBoard.setPlayerPosition(idPartita, nomeGiocatore, idCasella);
        sendPawnMove(pawnId, nomeGiocatore, idCasella, gameSessions, idPartita);
        gameBoard.setPlayerPrison(idPartita, nomeGiocatore, true);
    }

    private void gestisciUscitaPrigione(String idPartita, String nomeGiocatore, String typeBox) {
        if (typeBox.equals("Probabilità")) {
            partitaProbabilitaRepository.setGiocatore(idPartita, nomeGiocatore, esciPrigione);
        } else {
            partitaImprevistoRepository.setGiocatore(idPartita, nomeGiocatore, esciPrigione);
        }
    }

    public void rispostaGestisciProprieta(String messaggioRisposta, WebSocketSession session) throws IOException {
        String scambioMessage = new ObjectMapper().writeValueAsString(Map.of(
                TYPE_KEY, "rispostaGestisciProprieta",
                CONTENT_KEY, messaggioRisposta
        ));
        session.sendMessage(new TextMessage(scambioMessage));
    }

    public void rispostaAggiornaProprieta(String gameId, String playerName, WebSocketSession session) throws IOException {
        List<PlayerProperties> playerPropertiesList = pCPPRepository.findPlayerProperties(gameId,playerName);
        String playerPropertiesListMessage = new ObjectMapper().writeValueAsString(Map.of(
                TYPE_KEY, "rispostaAggiornaProprieta",
                "properties", playerPropertiesList
        ));
        session.sendMessage(new TextMessage(playerPropertiesListMessage));
    }
}
