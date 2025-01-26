package unimib.dabancherz.newmonopoly.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unimib.dabancherz.newmonopoly.singleton.GameBoardSingleton;
import unimib.dabancherz.newmonopoly.database.entity.classiparametri.*;
import unimib.dabancherz.newmonopoly.database.repository.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OpportunitaManagerTest {

    private OpportunitaManager opportunitaManager;
    private PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    private GiocatoreRepository giocatoreRepository;
    private PartitaOpportunitaRepository partitaOpportunitaRepository;
    private PrisonManager prisonManager;
    private GameBoardSingleton gameBoard;

    @BeforeEach
    void setUp() {
        pCPPRepository = mock(PartitaCasellaPrezzoproprietaRepository.class);
        giocatoreRepository = mock(GiocatoreRepository.class);
        partitaOpportunitaRepository = mock(PartitaOpportunitaRepository.class);
        gameBoard = mock(GameBoardSingleton.class);

        opportunitaManager = new OpportunitaManager(pCPPRepository, giocatoreRepository, partitaOpportunitaRepository, prisonManager);
        opportunitaManager.gameBoard = gameBoard;  // Injecting the mock game board
    }

    @Test
    void testGestisciImporto() {
        Importo importo = new Importo();
        importo.setImporto(100);

        opportunitaManager.gestisciImporto(importo, "partita1", "giocatore1");

        verify(giocatoreRepository, times(1)).setSaldoGiocatore("giocatore1", "partita1", 100);
    }

    @Test
    void testGestisciPagamentoGiocatori() {
        Importo importo = new Importo();
        importo.setImporto(50);
        when(giocatoreRepository.contaGiocatoriInPartita("partita1")).thenReturn(4);

        opportunitaManager.gestisciPagamentoGiocatori("paga_importo_giocatore", importo, "partita1", "giocatore1");

        verify(giocatoreRepository, times(1)).pagaImportoGiocatori(50, "partita1", "giocatore1");
        verify(giocatoreRepository, times(1)).setSaldoGiocatore("giocatore1", "partita1", 150);
    }

    @Test
    void testGestisciPagamentoPossedimenti() {
        PagaPossedimenti pagaPossedimenti = new PagaPossedimenti();
        pagaPossedimenti.setCosto_casa(40);
        pagaPossedimenti.setCosto_albergo(100);

        when(pCPPRepository.contaCaseTot("giocatore1", "partita1")).thenReturn(3);
        when(pCPPRepository.contaAlberghiTot("giocatore1", "partita1")).thenReturn(2);

        opportunitaManager.gestisciPagamentoPossedimenti(pagaPossedimenti, "partita1", "giocatore1");

        verify(giocatoreRepository, times(1)).setSaldoGiocatore("giocatore1", "partita1", 320);
    }

    @Test
    void testGestisciSpostamento() {
        IdCasella idCasella = new IdCasella();
        idCasella.setId_casella(10);

        int result = opportunitaManager.gestisciSpostamento(idCasella, 5, "partita1", "giocatore1");

        verify(gameBoard, times(1)).setPlayerPosition("partita1", "giocatore1", 10);
        assertEquals(10, result);
    }

    /*@Test
    void testGestisciPrigione() {
        IdCasella idCasella = new IdCasella();
        idCasella.setId_casella(30);

        int result = opportunitaManager.gestisciPrigione(idCasella, "partita1", "giocatore1");

        verify(gameBoard, times(1)).setPlayerPosition("partita1", "giocatore1", 30);
        verify(gameBoard, times(1)).setPlayerPrison("partita1", "giocatore1", true);
        assertEquals(30, result);
    }*/

    @Test
    void testGestisciUscitaPrigione() {
        opportunitaManager.gestisciUscitaPrigione("partita1", "giocatore1", "Probabilità");

        verify(partitaOpportunitaRepository, times(1)).setGiocatore("partita1", "giocatore1", "esci_prigione", "Probabilità");

        opportunitaManager.gestisciUscitaPrigione("partita1", "giocatore1", "Imprevisto");

        verify(partitaOpportunitaRepository, times(1)).setGiocatore("partita1", "giocatore1", "esci_prigione", "Imprevisto");
    }
}