package unimib.daBancherz.NewMonopoly.Manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Handler.MessageHandler;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;
import unimib.daBancherz.NewMonopoly.database.Entity.ClassiParametri.IdCasella;
import unimib.daBancherz.NewMonopoly.database.Entity.ClassiParametri.Importo;
import unimib.daBancherz.NewMonopoly.database.Entity.ClassiParametri.PagaPossedimenti;
import unimib.daBancherz.NewMonopoly.database.Entity.ClassiParametri.TipoCasella;
import unimib.daBancherz.NewMonopoly.database.Repository.GiocatoreRepository;
import unimib.daBancherz.NewMonopoly.database.Repository.OpportunitaRepository;
import unimib.daBancherz.NewMonopoly.database.Repository.PartitaCasellaPrezzoproprietaRepository;
import unimib.daBancherz.NewMonopoly.database.Repository.PartitaOpportunitaRepository;
import unimib.daBancherz.NewMonopoly.database.Service.GameService;
import unimib.daBancherz.NewMonopoly.model.PlayerProperties;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class OpportunitaManager {

    private final PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    private final GiocatoreRepository giocatoreRepository;
    private final PartitaOpportunitaRepository partitaOpportunitaRepository;
    GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();
    private static final String CONTENT_KEY = "content";
    private static final String TYPE_KEY = "type";
    private final String ESCIPRIGIONE_KEY = "esci_prigione";
    private final String IMPREVISTO_KEY = "Imprevisto";
    private final String PROBABILITA_KEY = "Probabilità";

    public OpportunitaManager(PartitaCasellaPrezzoproprietaRepository pCPPRepository, GiocatoreRepository giocatoreRepository, PartitaOpportunitaRepository partitaOpportunitaRepository) {
        this.pCPPRepository = pCPPRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.partitaOpportunitaRepository = partitaOpportunitaRepository;
    }

    public void gestisciImporto(Object parametri, String idPartita, String nomeGiocatore, Map<String, List<WebSocketSession>> gameSessions) throws IOException {
        Importo importoDeserializzato = (Importo) parametri;
        int importo = importoDeserializzato.getImporto();
        giocatoreRepository.setSaldoGiocatore(nomeGiocatore, idPartita, importo);
    }

    public void gestisciPagamentoGiocatori(String tipoAzione, Object parametri, String idPartita, String nomeGiocatore, Map<String, List<WebSocketSession>> gameSessions) throws IOException {
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
    }

    public void gestisciPagamentoPossedimenti(Object parametri, String idPartita, String nomeGiocatore, Map<String, List<WebSocketSession>> gameSessions) throws IOException {
        PagaPossedimenti pagaPossedimentiDeserializzato = (PagaPossedimenti) parametri;
        int importoCasa = pagaPossedimentiDeserializzato.getCosto_casa();
        int importoAlbergo = pagaPossedimentiDeserializzato.getCosto_abergo();

        int numCase = pCPPRepository.contaCaseTot(nomeGiocatore, idPartita);
        int numAlberghi = pCPPRepository.contaAlberghiTot(nomeGiocatore, idPartita);

        int totaleDaPagare = (numCase * importoCasa) + (numAlberghi * importoAlbergo);
        giocatoreRepository.setSaldoGiocatore(nomeGiocatore, idPartita, totaleDaPagare);
    }

    public int gestisciSpostamento(Object parametri, int posizione, String idPartita, String nomeGiocatore, int pawnId, Map<String, List<WebSocketSession>> gameSessions, WebSocketSession session) throws Exception {
        int idCasella;

        if (parametri instanceof IdCasella idCasellaDeserializzato) {
            idCasella = idCasellaDeserializzato.getId_casella();

            if (posizione > idCasella) {
                giocatoreRepository.setSaldoGiocatore(nomeGiocatore, idPartita, -200);
            }
        } else if (parametri instanceof TipoCasella tipoCasellaDeserializzato) {
            idCasella = pCPPRepository.findNextCasellaByTipo(tipoCasellaDeserializzato.getTipo_casella(), posizione, idPartita);
        } else {
            return 0;
        }
        gameBoard.setPlayerPosition(idPartita, nomeGiocatore, idCasella);
        return idCasella;
    }

    public int gestisciPrigione(Object parametri, String idPartita, String nomeGiocatore, int pawnId, Map<String, List<WebSocketSession>> gameSessions) throws IOException {
        IdCasella idCasellaDeserializzato = (IdCasella) parametri;
        int idCasella = idCasellaDeserializzato.getId_casella();

        gameBoard.setPlayerPosition(idPartita, nomeGiocatore, idCasella);
        gameBoard.setPlayerPrison(idPartita, nomeGiocatore, true);
        return idCasella;
    }

    public void gestisciUscitaPrigione(String idPartita, String nomeGiocatore, String typeBox) {
        if (typeBox.equals(PROBABILITA_KEY)) {
            partitaOpportunitaRepository.setGiocatore(idPartita, nomeGiocatore, ESCIPRIGIONE_KEY, PROBABILITA_KEY);
        } else {
            partitaOpportunitaRepository.setGiocatore(idPartita, nomeGiocatore, ESCIPRIGIONE_KEY, IMPREVISTO_KEY);
        }
    }
}
