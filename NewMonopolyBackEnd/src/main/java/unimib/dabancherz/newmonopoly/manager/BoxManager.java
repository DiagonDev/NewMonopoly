package unimib.dabancherz.newmonopoly.manager;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.database.entity.Giocatore;
import unimib.dabancherz.newmonopoly.database.entity.Opportunita;
import unimib.dabancherz.newmonopoly.database.repository.GiocatoreRepository;
import unimib.dabancherz.newmonopoly.database.repository.OpportunitaRepository;
import unimib.dabancherz.newmonopoly.database.repository.PartitaCasellaPrezzoproprietaRepository;
import unimib.dabancherz.newmonopoly.database.repository.PartitaOpportunitaRepository;
import unimib.dabancherz.newmonopoly.handler.PropertyHandler;
import unimib.dabancherz.newmonopoly.singleton.GameBoardSingleton;

import java.util.List;
import java.util.Map;

@Component
public class BoxManager {
    private final MessageService messageService;
    private final PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    private final GiocatoreRepository giocatoreRepository;
    private final PartitaOpportunitaRepository partitaOpportunitaRepository;
    private final OpportunitaRepository opportunitaRepository;
    private final OpportunitaManager opportunitaManager;
    private final PropertyHandler propertyHandler;
    private final PrisonManager prisonManager;
    GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();
    private static final String TYPEKEY = "type";
    private static final String DESCRIPTIONKEY = "description";
    private static final String ESCIPRIGIONEKEY = "esci_prigione";
    private static final String IMPREVISTOKEY = "Imprevisto";
    private static final String PROBABILITAKEY = "Probabilità";

    public BoxManager(MessageService messageService, PartitaCasellaPrezzoproprietaRepository pCPPRepository, GiocatoreRepository giocatoreRepository, PartitaOpportunitaRepository partitaOpportunitaRepository, OpportunitaRepository opportunitaRepository, OpportunitaManager opportunitaManager, PropertyHandler propertyHandler, PrisonManager prisonManager) {
        this.messageService = messageService;
        this.pCPPRepository = pCPPRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.partitaOpportunitaRepository = partitaOpportunitaRepository;
        this.opportunitaRepository = opportunitaRepository;
        this.opportunitaManager = opportunitaManager;
        this.propertyHandler = propertyHandler;
        this.prisonManager = prisonManager;
    }

    //posizione => il codice della cella dove il giocatore finisce dopo il lancio dadi
    public void sendBoxUsage(String playerName, WebSocketSession session, int posizione, String gameId, Map<String, List<WebSocketSession>> gameSessions , Integer pawnId, boolean viaPay) throws Exception{
        Opportunita opportunita;
        Object parametri;
        String typeBox = pCPPRepository.findTipoByPosizione(posizione, gameId);
        String nomeCasella = pCPPRepository.findNomeCasellaByPosizioneAndGameId(posizione, gameId);
        String proprietario;
        String descrizione;
        String tipoAzione;
        int prezzoCasella, prezzoAffitto;

        session.sendMessage(new TextMessage(messageService.createMessage(Map.of(TYPEKEY, "nameBox", "name", nomeCasella))));

        //aggiorna i soldi quando passi dal via anche senza fermarti sopra
        if(viaPay){
            giocatoreRepository.setSaldoGiocatore(playerName, gameId, -200);
            messageService.updateBalance(gameSessions, gameId, playerName);
        }

        switch (typeBox){
            case "Via", "Posteggio", "Prigione":
                break;
            case "Tassa":
                giocatoreRepository.setSaldoGiocatore(playerName, gameId, 200);
                messageService.updateBalance(gameSessions, gameId, playerName);
                break;
            case "Proprietà", "Stazione", "Società":
                proprietario = pCPPRepository.findNomeGiocatoreByPosizioneAndGameId(posizione, gameId);
                prezzoCasella = pCPPRepository.prezzoCasella(posizione, gameId);

                if(proprietario == null){
                    int puntiFPrezzo = prezzoCasella*propertyHandler.numPuntiFedelta(playerName,gameId);
                    session.sendMessage(new TextMessage(messageService.createMessage(Map.of(TYPEKEY, "buy", "price", prezzoCasella,"points",puntiFPrezzo, "nameBox", nomeCasella))));
                }else if(!playerName.equals(proprietario)){
                    Integer idProprietario = giocatoreRepository.findIdByNomeAndPartitaCodiceInvito(proprietario, gameId);
                    Integer count = pCPPRepository.countProprieta(proprietario, typeBox, gameId);
                    prezzoAffitto = pCPPRepository.calcolaAffitto(gameId, posizione, idProprietario, count);
                    int corrispondenzaPunti = propertyHandler.numPuntiFedelta(playerName, gameId);
                    int puntiFAffittoPrezzo = prezzoAffitto*propertyHandler.numPuntiFedelta(playerName,gameId);
                    Giocatore giocatore = giocatoreRepository.findGiocatoreByIdpartita_CodiceInvitoAndNome(gameId, playerName);
                    int puntiFedelta = giocatore.getPuntiFedelta();

                    if(puntiFedelta >= puntiFAffittoPrezzo){
                        giocatoreRepository.setPuntiGiocatore(playerName, gameId, puntiFAffittoPrezzo);
                        giocatoreRepository.setPuntiGiocatore(proprietario, gameId, -puntiFAffittoPrezzo);
                        giocatoreRepository.setSaldoGiocatore(proprietario, gameId, -prezzoAffitto);
                    }else{
                        int prezzoAffitto2 = prezzoAffitto;
                        if(puntiFedelta != 0)
                            prezzoAffitto2 = prezzoAffitto - puntiFedelta / corrispondenzaPunti;
                        giocatoreRepository.setPuntiGiocatore(playerName, gameId, puntiFedelta);
                        giocatoreRepository.setPuntiGiocatore(proprietario, gameId, -puntiFAffittoPrezzo);
                        //sia che il saldo è sufficiente o meno gli scaliamo i soldi
                        //così può andare in negativo e nel caso perdere
                        giocatoreRepository.setSaldoGiocatore(playerName, gameId, prezzoAffitto2);
                        giocatoreRepository.setSaldoGiocatore(proprietario, gameId, -prezzoAffitto);
                    }
                    session.sendMessage(new TextMessage(messageService.createMessage(Map.of(TYPEKEY, "payment", DESCRIPTIONKEY, "affitto","destination", proprietario, "payment", prezzoAffitto))));
                    messageService.updateBalance(gameSessions, gameId, playerName);
                    messageService.updateBalance(gameSessions, gameId, proprietario);
                    messageService.sendSystemMessage(gameId, playerName + " ha pagato l'affitto a " + proprietario + " di " + prezzoAffitto, gameSessions,session);
                }
                break;
            case"InPrigione":
                gameBoard.setPlayerPosition(gameId, playerName, 11);//aggiorna la posizione del giocatore
                Thread.sleep(1000);
                messageService.sendPawnMove(pawnId, playerName, 11, gameSessions, gameId);
                gameBoard.setPlayerPrison(gameId, playerName, true);
                prisonManager.sendPrisonMessage(session);
                break;
            case IMPREVISTOKEY, PROBABILITAKEY:
                descrizione = partitaOpportunitaRepository.findDescrizione(gameId, typeBox);
                if (descrizione==null) {
                    partitaOpportunitaRepository.setUtilizzatoFalse(gameId, typeBox);
                    descrizione = partitaOpportunitaRepository.findDescrizione(gameId, typeBox);
                }
                session.sendMessage(new TextMessage(messageService.createMessage(Map.of(TYPEKEY, "draw", "card", typeBox, DESCRIPTIONKEY, descrizione))));
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
                messageService.updateBalance(gameSessions, idPartita, nomeGiocatore);
                break;
            case "paga_importo_giocatore", "ricevi_importo_giocatore":
                opportunitaManager.gestisciPagamentoGiocatori(tipoAzione, parametri, idPartita, nomeGiocatore);
                List<String> giocatoriPartita = giocatoreRepository.findGiocatori(idPartita);
                for (String nome : giocatoriPartita) {
                    messageService.updateBalance(gameSessions, idPartita, nome);
                }
                break;
            case "paga_possedimenti":
                opportunitaManager.gestisciPagamentoPossedimenti(parametri, idPartita, nomeGiocatore);
                messageService.updateBalance(gameSessions, idPartita, nomeGiocatore);
                break;
            case "sposta_avanti":
                idCasella = opportunitaManager.gestisciSpostamento(parametri, posizione, idPartita, nomeGiocatore);
                messageService.updateBalance(gameSessions, idPartita, nomeGiocatore);
                messageService.sendPawnMove(pawnId, nomeGiocatore, idCasella, gameSessions, idPartita);
                Thread.sleep(2000);
                sendBoxUsage(nomeGiocatore, session, idCasella, idPartita, gameSessions, pawnId, false);
                break;
            case "vai_in_prigione":
                idCasella = opportunitaManager.gestisciPrigione(parametri, idPartita, nomeGiocatore, session);
                messageService.sendPawnMove(pawnId, nomeGiocatore, idCasella, gameSessions, idPartita);
                gameBoard.setPlayerPrison(idPartita, nomeGiocatore, true);
                prisonManager.sendPrisonMessage(session);
                break;
            case ESCIPRIGIONEKEY:
                opportunitaManager.gestisciUscitaPrigione(idPartita, nomeGiocatore, typeBox);
                break;
            default:
                throw new IllegalArgumentException("Tipo di messaggio non supportato: " + tipoAzione);
        }
    }
}
