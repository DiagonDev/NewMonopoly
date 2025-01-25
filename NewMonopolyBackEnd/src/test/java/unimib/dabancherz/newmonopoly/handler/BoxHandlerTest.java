package unimib.dabancherz.newmonopoly.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.manager.OpportunitaManager;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.database.repository.*;

import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.*;

class BoxHandlerTest {

    private BoxHandler boxHandler;
    private MessageService messageService;
    private PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    private GiocatoreRepository giocatoreRepository;
    private PartitaOpportunitaRepository partitaOpportunitaRepository;
    private OpportunitaRepository opportunitaRepository;
    private OpportunitaManager opportunitaManager;
    private WebSocketSession session;

    @BeforeEach
    void setUp() {
        messageService = mock(MessageService.class);
        pCPPRepository = mock(PartitaCasellaPrezzoproprietaRepository.class);
        giocatoreRepository = mock(GiocatoreRepository.class);
        partitaOpportunitaRepository = mock(PartitaOpportunitaRepository.class);
        opportunitaRepository = mock(OpportunitaRepository.class);
        opportunitaManager = mock(OpportunitaManager.class);
        session = mock(WebSocketSession.class);
        boxHandler = new BoxHandler(messageService, pCPPRepository, giocatoreRepository, partitaOpportunitaRepository, opportunitaRepository, opportunitaManager);
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

        boxHandler.sendBoxUsage(playerName, session, posizione, gameId, gameSessions, pawnId, true);

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

        boxHandler.sendBoxUsage(playerName, session, posizione, gameId, gameSessions, pawnId, false);

        verify(giocatoreRepository, times(1)).setSaldoGiocatore(playerName, gameId, 200);
        verify(messageService, times(1)).updateBalance(gameSessions, gameId, playerName);
    }

    @Test
    void testGestisciAzione_RiceviImporto() throws Exception {
        String tipoAzione = "ricevi_importo";
        String gameId = "game1";
        String playerName = "player1";
        Integer posizione = 0;
        Map<String, List<WebSocketSession>> gameSessions = Map.of(gameId, List.of(session));
        Object parametri = new Object();

        boxHandler.gestisciAzione(tipoAzione, parametri, gameId, playerName, posizione, gameSessions, "Imprevisto", session);

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

        boxHandler.gestisciAzione(tipoAzione, parametri, gameId, playerName, posizione, gameSessions, "Imprevisto", session);

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

        boxHandler.gestisciAzione(tipoAzione, parametri, gameId, playerName, posizione, gameSessions, "Imprevisto", session);

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

        when(opportunitaManager.gestisciPrigione(parametri, gameId, playerName)).thenReturn(newPosizione);

        boxHandler.gestisciAzione(tipoAzione, parametri, gameId, playerName, posizione, gameSessions, "Imprevisto", session);

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

        boxHandler.gestisciAzione(tipoAzione, parametri, gameId, playerName, posizione, gameSessions, "Imprevisto", session);

        verify(opportunitaManager, times(1)).gestisciUscitaPrigione(gameId, playerName, "Imprevisto");
    }
}