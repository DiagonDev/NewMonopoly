package unimib.dabancherz.newmonopoly.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.database.repository.*;
import unimib.dabancherz.newmonopoly.handler.PropertyHandler;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.*;

class BoxManagerTest {
    @InjectMocks
    private BoxManager boxManager;
    @Mock
    private MessageService messageService;
    @Mock
    private PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    @Mock
    private GiocatoreRepository giocatoreRepository;
    @Mock
    private PartitaOpportunitaRepository partitaOpportunitaRepository;
    @Mock
    private OpportunitaRepository opportunitaRepository;
    @Mock
    private OpportunitaManager opportunitaManager;
    @Mock
    private WebSocketSession session;
    @Mock
    private PropertyHandler propertyHandler;
    @Mock
    private PrisonManager prisonManager;

@BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        boxManager = spy(new BoxManager(messageService, pCPPRepository, giocatoreRepository, partitaOpportunitaRepository, opportunitaRepository, opportunitaManager, propertyHandler, prisonManager));
    }

    @Test
    void testSendBoxUsage_ViaPay() throws Exception {
        String playerName = "player1";
        String gameId = "game1";
        int posizione = 0;
        Integer pawnId = 1;
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        when(pCPPRepository.findTipoByPosizione(posizione, gameId)).thenReturn("Via");
        when(pCPPRepository.findNomeCasellaByPosizioneAndGameId(posizione, gameId)).thenReturn("Start");
        when(messageService.createMessage(Map.of("type", "nameBox", "name", "Start"))).thenReturn("{\"type\":\"nameBox\",\"name\":\"Start\"}");
        boxManager.sendBoxUsage(playerName, session, posizione, gameId, gameSessions, pawnId, true);
        verify(giocatoreRepository, times(1)).setSaldoGiocatore(playerName, gameId, -200);
        verify(messageService, times(1)).updateBalance(gameSessions, gameId, playerName);
    }

    @Test
    void testSendBoxUsage_Tassa() throws Exception {
        String playerName = "player1";
        String gameId = "game1";
        int posizione = 0;
        Integer pawnId = 1;
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        when(pCPPRepository.findTipoByPosizione(posizione, gameId)).thenReturn("Tassa");
        when(pCPPRepository.findNomeCasellaByPosizioneAndGameId(posizione, gameId)).thenReturn("Tax");
        when(messageService.createMessage(Map.of("type", "nameBox", "name", "Tax"))).thenReturn("{\"type\":\"nameBox\",\"name\":\"Tax\"}");
        boxManager.sendBoxUsage(playerName, session, posizione, gameId, gameSessions, pawnId, false);
        verify(giocatoreRepository, times(1)).setSaldoGiocatore(playerName, gameId, 200);
        verify(messageService, times(1)).updateBalance(gameSessions, gameId, playerName);
    }

    @Test
    void testSendBoxUsage_ProprietaNonAcquistata() throws Exception {
        String playerName = "player1";
        String gameId = "game-1";
        int posizione = 3;
        Integer pawnId = 1;
        Map<String, List<WebSocketSession>> gameSessions = mock(Map.class);
        when(pCPPRepository.findTipoByPosizione(posizione, gameId)).thenReturn("Proprietà");
        when(pCPPRepository.findNomeCasellaByPosizioneAndGameId(posizione, gameId)).thenReturn("Vicolo Corto");
        when(messageService.createMessage(Map.of("type", "nameBox", "name", "Vicolo Corto"))).thenReturn("{\"type\":\"nameBox\",\"name\":\"Vicolo Corto\"}");
        when(pCPPRepository.findNomeGiocatoreByPosizioneAndGameId(posizione, gameId)).thenReturn("player1");
        when(pCPPRepository.prezzoCasella(posizione, gameId)).thenReturn(100);
        when(propertyHandler.numPuntiFedelta(playerName, gameId)).thenReturn(1);
        boxManager.sendBoxUsage(playerName, session, posizione, gameId, gameSessions, pawnId, false);
        verify(session).sendMessage(any(TextMessage.class));
    }

    @Test
    void testSendBoxUsage_InPrigione() throws Exception {
        String playerName = "player1";
        String gameId = "game-1";
        int posizione = 10;
        Integer pawnId = 1;
        Map<String, List<WebSocketSession>> gameSessions = mock(Map.class);
        when(pCPPRepository.findTipoByPosizione(posizione, gameId)).thenReturn("InPrigione");
        when(pCPPRepository.findNomeCasellaByPosizioneAndGameId(posizione, gameId)).thenReturn("Prigione");
        when(messageService.createMessage(Map.of("type", "nameBox", "name", "Prigione"))).thenReturn("{\"type\":\"nameBox\",\"name\":\"Prigione\"}");
        boxManager.sendBoxUsage(playerName, session, posizione, gameId, gameSessions, pawnId, false);
        verify(prisonManager).sendPrisonMessage(session);
    }

    @Test
    void testSendBoxUsage_PagaOrRiceviImportoGiocatore() throws Exception {
        String gameId = "game1";
        String playerName = "player1";
        Integer posizione = 0;
        String tipoAzione = "paga_importo_giocatore";
        Object parametri = new Object();
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        List<String> giocatoriPartita = List.of("player1", "player2", "player3");
        when(giocatoreRepository.findGiocatori(gameId)).thenReturn(giocatoriPartita);
        boxManager.gestisciAzione(tipoAzione, parametri, gameId, playerName, posizione, gameSessions, "Imprevisto", session);
        verify(opportunitaManager, times(1)).gestisciPagamentoGiocatori(eq(tipoAzione), eq(parametri), eq(gameId), eq(playerName));
        for (String nome : giocatoriPartita) {
            verify(messageService, times(1)).updateBalance(gameSessions, gameId, nome);
        }
    }

    @Test
    void testSendBoxUsage_SpostaAvanti() throws Exception {
        String tipoAzione = "sposta_avanti";
        String gameId = "game1";
        String playerName = "player1";
        Integer posizione = 0;
        Integer pawnId = 0;
        Object parametri = new Object();
        Integer idCasella = 5;
        Integer posizioneAggiornata = 10;
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));


        when(opportunitaManager.gestisciSpostamento(parametri, posizione, gameId, playerName)).thenReturn(idCasella);
        when(pCPPRepository.findPosizioneByIdcasella_IdCasella(gameId, idCasella)).thenReturn(posizioneAggiornata);
        doNothing().when(boxManager).sendBoxUsage(anyString(),any(WebSocketSession.class), anyInt(), anyString(), anyMap(), anyInt(), anyBoolean());
        boxManager.gestisciAzione(tipoAzione, parametri, gameId, playerName, posizione, gameSessions, "Imprevisto", session);
        verify(opportunitaManager, times(1)).gestisciSpostamento(eq(parametri), eq(posizione), eq(gameId), eq(playerName));
        verify(messageService, times(1)).updateBalance(gameSessions, gameId, playerName);
        verify(messageService, times(1)).sendPawnMove(eq(pawnId), eq(playerName), eq(posizioneAggiornata), eq(gameSessions), eq(gameId));
    }

    @Test
    void testGestisciAzione_RiceviImporto() throws Exception {
        String tipoAzione = "ricevi_importo";
        String gameId = "game1";
        String playerName = "player1";
        Integer posizione = 0;
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        Object parametri = new Object();
        boxManager.gestisciAzione(tipoAzione, parametri, gameId, playerName, posizione, gameSessions, "Imprevisto", session);
        verify(opportunitaManager, times(1)).gestisciImporto(parametri, gameId, playerName);
        verify(messageService, times(1)).updateBalance(gameSessions, gameId, playerName);
    }

    @Test
    void testGestisciAzione_PagaImporto() throws Exception {
        String tipoAzione = "paga_importo";
        String gameId = "game1";
        String playerName = "player1";
        Integer posizione = 0;
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        Object parametri = new Object();
        boxManager.gestisciAzione(tipoAzione, parametri, gameId, playerName, posizione, gameSessions, "Imprevisto", session);
        verify(opportunitaManager, times(1)).gestisciImporto(parametri, gameId, playerName);
        verify(messageService, times(1)).updateBalance(gameSessions, gameId, playerName);
    }

    @Test
    void testGestisciAzione_PagaPossedimenti() throws Exception {
        String tipoAzione = "paga_possedimenti";
        String gameId = "game1";
        String playerName = "player1";
        Integer posizione = 0;
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        Object parametri = new Object();
        boxManager.gestisciAzione(tipoAzione, parametri, gameId, playerName, posizione, gameSessions, "Imprevisto", session);
        verify(opportunitaManager, times(1)).gestisciPagamentoPossedimenti(parametri, gameId, playerName);
        verify(messageService, times(1)).updateBalance(gameSessions, gameId, playerName);
    }

    @Test
    void testGestisciAzione_VaiInPrigione() throws Exception {
        String tipoAzione = "vai_in_prigione";
        String gameId = "game1";
        String playerName = "player1";
        Integer posizione = 0;
        Integer newPosizione = 11;
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        Object parametri = new Object();
        when(opportunitaManager.gestisciPrigione(parametri, gameId, playerName, session)).thenReturn(newPosizione);
        boxManager.gestisciAzione(tipoAzione, parametri, gameId, playerName, posizione, gameSessions, "Imprevisto", session);
        verify(messageService, times(1)).sendPawnMove(anyInt(), eq(playerName), eq(newPosizione), eq(gameSessions), eq(gameId));
    }

    @Test
    void testGestisciAzione_EsciPrigione() throws Exception {
        String tipoAzione = "esci_prigione";
        String gameId = "game1";
        String playerName = "player1";
        Integer posizione = 0;
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        Object parametri = new Object();
        boxManager.gestisciAzione(tipoAzione, parametri, gameId, playerName, posizione, gameSessions, "Imprevisto", session);
        verify(opportunitaManager, times(1)).gestisciUscitaPrigione(gameId, playerName, "Imprevisto");
    }


}
