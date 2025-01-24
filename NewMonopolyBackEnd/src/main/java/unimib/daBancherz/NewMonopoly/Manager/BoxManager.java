package unimib.daBancherz.NewMonopoly.Manager;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Handler.MessageHandler;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;
import unimib.daBancherz.NewMonopoly.database.Entity.Opportunita;
import unimib.daBancherz.NewMonopoly.database.Repository.GiocatoreRepository;
import unimib.daBancherz.NewMonopoly.database.Repository.OpportunitaRepository;
import unimib.daBancherz.NewMonopoly.database.Repository.PartitaCasellaPrezzoproprietaRepository;
import unimib.daBancherz.NewMonopoly.database.Repository.PartitaOpportunitaRepository;


import java.util.List;
import java.util.Map;

@Component
public class BoxManager {
    private final MessageHandler messageHandler;
    private final PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    private final GiocatoreRepository giocatoreRepository;
    private final PartitaOpportunitaRepository partitaOpportunitaRepository;
    private final OpportunitaRepository opportunitaRepository;
    private final OpportunitaManager opportunitaManager;
    GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();
    private static final String TYPE_KEY = "type";
    private static final String DESCRIPTION_KEY = "description";
    private final String ESCIPRIGIONE_KEY = "esci_prigione";
    private final String IMPREVISTO_KEY = "Imprevisto";
    private final String PROBABILITA_KEY = "Probabilità";

    public BoxManager(MessageHandler messageHandler, PartitaCasellaPrezzoproprietaRepository pCPPRepository, GiocatoreRepository giocatoreRepository, PartitaOpportunitaRepository partitaOpportunitaRepository, OpportunitaRepository opportunitaRepository, OpportunitaManager opportunitaManager) {
        this.messageHandler = messageHandler;
        this.pCPPRepository = pCPPRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.partitaOpportunitaRepository = partitaOpportunitaRepository;
        this.opportunitaRepository = opportunitaRepository;
        this.opportunitaManager = opportunitaManager;
    }

    //posizione => il codice della cella dove il giocatore finisce dopo il lancio dadi
    public void sendBoxUsage(String playerName, WebSocketSession session, int posizione, String gameId, Map<String, List<WebSocketSession>> gameSessions , Integer pawnId, boolean viaPay) throws Exception{
        Opportunita opportunita;
        Object parametri;
        String typeBox = pCPPRepository.findTipoByPosizione(posizione, gameId);
        String nomeCasella = pCPPRepository.findNomeCasellaByPosizioneAndGameId(posizione, gameId);
        String proprietario, descrizione, tipoAzione;
        int prezzoCasella, prezzoAffitto;

        session.sendMessage(new TextMessage(messageHandler.createMessage(Map.of(TYPE_KEY, "nameBox", "name", nomeCasella))));

        //aggiorna i soldi quando passi dal via anche senza fermarti sopra
        if(viaPay){
            giocatoreRepository.setSaldoGiocatore(playerName, gameId, -200);
            messageHandler.updateBalance(gameSessions, gameId, playerName);
        }

        switch (typeBox){
            case "Via", "Posteggio", "Prigione":
                break;
            case "Tassa":
                giocatoreRepository.setSaldoGiocatore(playerName, gameId, 200);
                messageHandler.updateBalance(gameSessions, gameId, playerName);
                break;
            case "Proprietà", "Stazione", "Società":
                proprietario = pCPPRepository.findNomeGiocatoreByPosizioneAndGameId(posizione, gameId);
                prezzoCasella = pCPPRepository.prezzoCasella(posizione, gameId);
                if(proprietario == null)
                    session.sendMessage(new TextMessage(messageHandler.createMessage(Map.of(TYPE_KEY, "buy", "price", prezzoCasella, "nameBox", nomeCasella))));
                else if(!playerName.equals(proprietario)){
                    Integer idProprietario = giocatoreRepository.findIdByNomeAndPartitaCodiceInvito(proprietario, gameId);
                    Integer count = pCPPRepository.countProprieta(proprietario, typeBox, gameId);
                    prezzoAffitto = pCPPRepository.calcolaAffitto(gameId, posizione, idProprietario, count);
                    giocatoreRepository.setSaldoGiocatore(playerName, gameId, prezzoAffitto);
                    giocatoreRepository.setSaldoGiocatore(proprietario, gameId, -prezzoAffitto);

                    session.sendMessage(new TextMessage(messageHandler.createMessage(Map.of(TYPE_KEY, "payment", DESCRIPTION_KEY, "affitto","destination", proprietario, "payment", prezzoAffitto))));
                    messageHandler.updateBalance(gameSessions, gameId, playerName);
                    messageHandler.updateBalance(gameSessions, gameId, proprietario);
                    messageHandler.sendSystemMessage(gameId, playerName + " ha pagato l'affitto a " + proprietario + " di " + prezzoAffitto, gameSessions,session);
                }
                break;
            case"InPrigione":
                gameBoard.setPlayerPosition(gameId, playerName, 11);//aggiorna la posizione del giocatore
                Thread.sleep(1000);
                messageHandler.sendPawnMove(pawnId, playerName, 11, gameSessions, gameId);
                gameBoard.setPlayerPrison(gameId, playerName, true);
                break;
            case IMPREVISTO_KEY, PROBABILITA_KEY:
                descrizione = partitaOpportunitaRepository.findDescrizione(gameId, typeBox);
                if (descrizione==null) {
                    partitaOpportunitaRepository.setUtilizzatoFalse(gameId, typeBox);
                    descrizione = partitaOpportunitaRepository.findDescrizione(gameId, typeBox);
                }
                session.sendMessage(new TextMessage(messageHandler.createMessage(Map.of(TYPE_KEY, "draw", "card", typeBox,DESCRIPTION_KEY, descrizione))));
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
                opportunitaManager.gestisciImporto(parametri, idPartita, nomeGiocatore);
                messageHandler.updateBalance(gameSessions, idPartita, nomeGiocatore);
                break;
            case "paga_importo_giocatore", "ricevi_importo_giocatore":
                opportunitaManager.gestisciPagamentoGiocatori(tipoAzione, parametri, idPartita, nomeGiocatore);
                List<String> giocatoriPartita = giocatoreRepository.findGiocatori(idPartita);
                for (String nome : giocatoriPartita) {
                    messageHandler.updateBalance(gameSessions, idPartita, nome);
                }
                break;
            case "paga_possedimenti":
                opportunitaManager.gestisciPagamentoPossedimenti(parametri, idPartita, nomeGiocatore);
                messageHandler.updateBalance(gameSessions, idPartita, nomeGiocatore);
                break;
            case "sposta_avanti":
                idCasella = opportunitaManager.gestisciSpostamento(parametri, posizione, idPartita, nomeGiocatore);
                messageHandler.updateBalance(gameSessions, idPartita, nomeGiocatore);
                messageHandler.sendPawnMove(pawnId, nomeGiocatore, idCasella, gameSessions, idPartita);
                Thread.sleep(2000);
                sendBoxUsage(nomeGiocatore, session, idCasella, idPartita, gameSessions, pawnId, false);
                break;
            case "vai_in_prigione":
                idCasella = opportunitaManager.gestisciPrigione(parametri, idPartita, nomeGiocatore);
                messageHandler.sendPawnMove(pawnId, nomeGiocatore, idCasella, gameSessions, idPartita);
                break;
            case ESCIPRIGIONE_KEY:
                opportunitaManager.gestisciUscitaPrigione(idPartita, nomeGiocatore, typeBox);
                break;
            default:
                throw new IllegalArgumentException("Tipo di messaggio non supportato: " + tipoAzione);
        }
    }
}
