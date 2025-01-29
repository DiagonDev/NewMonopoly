package unimib.dabancherz.newmonopoly.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.database.entity.classiparametri.*;
import unimib.dabancherz.newmonopoly.database.repository.*;
import unimib.dabancherz.newmonopoly.singleton.GameBoardSingleton;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OpportunitaManagerTest {
    @Mock
    private PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    @Mock
    private GiocatoreRepository giocatoreRepository;
    @Mock
    private PartitaOpportunitaRepository partitaOpportunitaRepository;
    @Mock
    private PrisonManager prisonManager;
    @Mock
    private WebSocketSession session;
    @Mock
    private GameBoardSingleton gameBoard;
    @InjectMocks
    private OpportunitaManager opportunitaManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        opportunitaManager = new OpportunitaManager(pCPPRepository, giocatoreRepository, partitaOpportunitaRepository, prisonManager);
        opportunitaManager.gameBoard = gameBoard;
    }

    @Test
    void gestisciImporto() {
        Importo importo = new Importo();
        importo.setImporto(100);
        String idPartita = "game-1";
        String nomeGiocatore = "giocatore1";
        opportunitaManager.gestisciImporto(importo, idPartita, nomeGiocatore);
        verify(giocatoreRepository, times(1)).setSaldoGiocatore(nomeGiocatore, idPartita, 100);
    }

    @Test
    void gestisciPagamentoGiocatori_PagaImportoGiocatore() {
        Importo importo = new Importo();
        importo.setImporto(50);
        String idPartita = "game-1";
        String nomeGiocatore = "giocatore1";
        when(giocatoreRepository.contaGiocatoriInPartita(idPartita)).thenReturn(4);
        opportunitaManager.gestisciPagamentoGiocatori("paga_importo_giocatore", importo, idPartita, nomeGiocatore);
        verify(giocatoreRepository, times(1)).pagaImportoGiocatori(50, idPartita, nomeGiocatore);
        verify(giocatoreRepository, times(1)).setSaldoGiocatore(nomeGiocatore, idPartita, 150);
    }

    @Test
    void gestisciPagamentoGiocatori_Altro() {
        Importo importo = new Importo();
        importo.setImporto(50);
        String idPartita = "game-1";
        String nomeGiocatore = "giocatore1";
        when(giocatoreRepository.contaGiocatoriInPartita(idPartita)).thenReturn(4);
        opportunitaManager.gestisciPagamentoGiocatori("altro", importo, idPartita, nomeGiocatore);
        verify(giocatoreRepository, times(1)).setSaldoGiocatore(nomeGiocatore, idPartita, -150);
        verify(giocatoreRepository, times(1)).pagaImportoGiocatori(-50, idPartita, nomeGiocatore);
    }

    @Test
    void gestisciPagamentoPossedimenti() {
        PagaPossedimenti pagaPossedimenti = new PagaPossedimenti();
        pagaPossedimenti.setCosto_casa(50);
        pagaPossedimenti.setCosto_albergo(100);
        String idPartita = "game-1";
        String nomeGiocatore = "giocatore1";
        when(pCPPRepository.contaCaseTot(nomeGiocatore, idPartita)).thenReturn(2);
        when(pCPPRepository.contaAlberghiTot(nomeGiocatore, idPartita)).thenReturn(1);
        opportunitaManager.gestisciPagamentoPossedimenti(pagaPossedimenti, idPartita, nomeGiocatore);
        verify(giocatoreRepository, times(1)).setSaldoGiocatore(nomeGiocatore, idPartita, 200);
    }

    @Test
    void gestisciSpostamento_IdCasella() {
        IdCasella idCasella = new IdCasella();
        idCasella.setId_casella(5);
        String idPartita = "game-1";
        String nomeGiocatore = "giocatore1";
        int posizione = 10;
        int result = opportunitaManager.gestisciSpostamento(idCasella, posizione, idPartita, nomeGiocatore);
        verify(giocatoreRepository, times(1)).setSaldoGiocatore(nomeGiocatore, idPartita, -200);
        assertEquals(5, result);
    }

    @Test
    void gestisciSpostamento_TipoCasella() {
        TipoCasella tipoCasella = new TipoCasella();
        tipoCasella.setTipo_casella("Stazione");
        String idPartita = "game-1";
        String nomeGiocatore = "giocatore1";
        int posizione = 5;
        when(pCPPRepository.findNextCasellaByTipo("Stazione", posizione, idPartita)).thenReturn(8);
        int result = opportunitaManager.gestisciSpostamento(tipoCasella, posizione, idPartita, nomeGiocatore);
        assertEquals(8, result);
    }

    @Test
    void gestisciSpostamento_InvalidParametri() {
        String idPartita = "game-1";
        String nomeGiocatore = "giocatore1";
        int posizione = 5;
        int result = opportunitaManager.gestisciSpostamento(new Object(), posizione, idPartita, nomeGiocatore);
        assertEquals(0, result);
    }

    @Test
    void gestisciPrigione() throws Exception {
        IdCasella idCasella = new IdCasella();
        idCasella.setId_casella(10);
        String idPartita = "game-1";
        String nomeGiocatore = "giocatore1";
        int result = opportunitaManager.gestisciPrigione(idCasella, idPartita, nomeGiocatore, session);
        verify(gameBoard, times(1)).setPlayerPosition(idPartita, nomeGiocatore, 10);
        verify(gameBoard, times(1)).setPlayerPrison(idPartita, nomeGiocatore, true);
        verify(prisonManager, times(1)).sendPrisonMessage(session);
        assertEquals(10, result);
    }

    @Test
    void gestisciUscitaPrigione_Probabilita() {
        String idPartita = "game-1";
        String nomeGiocatore = "giocatore1";
        opportunitaManager.gestisciUscitaPrigione(idPartita, nomeGiocatore, "Probabilità");
        verify(partitaOpportunitaRepository, times(1)).setGiocatore(idPartita, nomeGiocatore, "esci_prigione", "Probabilità");
    }

    @Test
    void gestisciUscitaPrigione_Imprevisto() {
        String idPartita = "game-1";
        String nomeGiocatore = "giocatore1";
        opportunitaManager.gestisciUscitaPrigione(idPartita, nomeGiocatore, "Imprevisto");
        verify(partitaOpportunitaRepository, times(1)).setGiocatore(idPartita, nomeGiocatore, "esci_prigione", "Imprevisto");
    }
}