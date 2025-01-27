/*
package unimib.dabancherz.newmonopoly.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.database.repository.CasellaRepository;
import unimib.dabancherz.newmonopoly.database.repository.GiocatoreRepository;
import unimib.dabancherz.newmonopoly.database.repository.PartitaCasellaPrezzoproprietaRepository;
import unimib.dabancherz.newmonopoly.model.PlayerProperties;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PropertyHandlerTest {

    private PropertyHandler propertyHandler;

    @Mock
    private PartitaCasellaPrezzoproprietaRepository mockPCPPRepository;
    @Mock
    private GiocatoreRepository mockGiocatoreRepository;
    @Mock
    private GameHandler mockGameHandler;
    @Mock
    private MessageService mockMessageService;
    @Mock
    private CasellaRepository mockCasellaRepository;
    @Mock
    private WebSocketSession mockSession;

*/
/*    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        propertyHandler = new PropertyHandler(
                mockPCPPRepository,
                mockGiocatoreRepository,
                mockGameHandler,
                mockMessageService,
                mockCasellaRepository
        );
    }*//*


    @Test
    void testIpotecaProprieta() throws Exception {
        PlayerProperties property = new PlayerProperties();
        property.setNome("Proprieta1");
        property.setPrezzoCorrente(200);

        String gameId = "game123";
        String playerName = "player1";

        when(mockGameHandler.getGameIdBySession(mockSession)).thenReturn(gameId);
        when(mockGameHandler.getPlayerNameBySession(mockSession)).thenReturn(playerName);
        when(mockPCPPRepository.findPosizioneByNomeCasellaAndIdpartita("Proprieta1", gameId)).thenReturn(1);

        propertyHandler.ipotecaProprieta(property, mockSession);

        verify(mockPCPPRepository).setPrezzoCorrente(eq(100), eq(gameId), eq(1));
        verify(mockPCPPRepository).setProprietario(null, gameId, "Proprieta1");
        verify(mockMessageService).sendSystemMessage(eq(gameId), contains("ha ipotecato"), any(), eq(mockSession));
    }

    @Test
    void testAcquistaProprieta() throws Exception {
        String[] messageParts = {"acquista", "Proprieta1"};
        String gameId = "game123";
        String playerName = "player1";

        when(mockGameHandler.getGameIdBySession(mockSession)).thenReturn(gameId);
        when(mockGameHandler.getPlayerNameBySession(mockSession)).thenReturn(playerName);
        when(mockPCPPRepository.prezzoCasella2("Proprieta1", gameId)).thenReturn(200);
        when(mockGiocatoreRepository.saldoGiocatore(playerName, gameId)).thenReturn(300);

        propertyHandler.acquistaProprieta(messageParts, mockSession);

        verify(mockPCPPRepository).setProprietario(playerName, gameId, "Proprieta1");
        verify(mockGiocatoreRepository).setSaldoGiocatore(playerName, gameId, 200);
        verify(mockMessageService).inviaMessaggio(mockSession, "acquistoRiuscito");
    }

   */
/* @Test
    void testCompletaAcquistoProprieta() throws Exception {
        String gameId = "game123";
        String playerName = "player1";

        propertyHandler.completaAcquistoProprieta(gameId, playerName, "Proprieta1", 200, mockSession);

        verify(mockPCPPRepository).setProprietario(playerName, gameId, "Proprieta1");
        verify(mockGiocatoreRepository).setSaldoGiocatore(playerName, gameId, 200);
        verify(mockMessageService).updateBalance(anyMap(), eq(gameId), eq(playerName));
        verify(mockMessageService).inviaMessaggio(eq(mockSession), eq("acquistoRiuscito"));
        verify(mockMessageService).sendSystemMessage(eq(gameId), contains("ha acquistato: Proprieta1"), anyMap(), eq(mockSession));
    } *//*


    @Test
    void testGestisciProprieta() throws IOException {
        String gameId = "game123";
        String playerName = "player1";
        List<PlayerProperties> playerPropertiesList = List.of(new PlayerProperties());

        when(mockGameHandler.getGameIdBySession(mockSession)).thenReturn(gameId);
        when(mockGameHandler.getPlayerNameBySession(mockSession)).thenReturn(playerName);
        when(mockPCPPRepository.findOtherPlayerProperties(gameId, playerName)).thenReturn(playerPropertiesList);

        propertyHandler.gestisciProprieta(mockSession);

        verify(mockMessageService).inviaMessaggio(mockSession, "allProperties", "properties", playerPropertiesList);
    }

    @Test
    void testEffettuaScambio() throws Exception {
        PlayerProperties property1 = new PlayerProperties();
        property1.setIdGiocatore(1);
        property1.setNome("Proprieta1");

        PlayerProperties property2 = new PlayerProperties();
        property2.setIdGiocatore(2);
        property2.setNome("Proprieta2");

        Map<String, Object> data = Map.of(
                "property1", property1,
                "property2", property2,
                "offertaMonetaria", 100
        );

        String gameId = "game123";
        String nomeRichiedente = "player1";
        String nomeProprietario = "player2";

        when(mockGiocatoreRepository.findNomeByidGiocatore(1)).thenReturn(nomeRichiedente);
        when(mockGiocatoreRepository.findNomeByidGiocatore(2)).thenReturn(nomeProprietario);
        when(mockGameHandler.getGameIdBySession(mockSession)).thenReturn(gameId);
        when(mockGameHandler.getSessionByPlayerName(nomeProprietario, gameId)).thenReturn(mockSession);

        propertyHandler.effettuaScambio(data, mockSession);

        ArgumentCaptor<PlayerProperties> propertyCaptor1 = ArgumentCaptor.forClass(PlayerProperties.class);
        ArgumentCaptor<PlayerProperties> propertyCaptor2 = ArgumentCaptor.forClass(PlayerProperties.class);
        verify(mockMessageService).exchangeRequestMessage(eq(nomeRichiedente), propertyCaptor1.capture(), propertyCaptor2.capture(), eq(100), eq(mockSession));

        assertEquals(property1.getNome(), propertyCaptor1.getValue().getNome());
        assertEquals(property2.getNome(), propertyCaptor2.getValue().getNome());
        verify(mockMessageService).sendSystemMessage(eq(gameId), contains("ha chiesto uno scambio a"), anyMap(), eq(mockSession));
    }

    @Test
    void testRispostaScambio() throws Exception {
        PlayerProperties property1 = new PlayerProperties();
        property1.setIdGiocatore(1);
        property1.setNome("Proprieta1");

        PlayerProperties property2 = new PlayerProperties();
        property2.setIdGiocatore(2);
        property2.setNome("Proprieta2");

        Map<String, Object> data = Map.of(
                "property1", property1,
                "property2", property2,
                "offertaMonetaria", 100
        );

        String gameId = "game123";
        String nomeProprietario = "player2";
        String nomeRichiedente = "player1";
        int saldoRichiedente = 200;

        when(mockGiocatoreRepository.findNomeByidGiocatore(2)).thenReturn(nomeProprietario);
        when(mockGiocatoreRepository.findNomeByidGiocatore(1)).thenReturn(nomeRichiedente);
        when(mockGameHandler.getGameIdBySession(mockSession)).thenReturn(gameId);
        when(mockGameHandler.getSessionByPlayerName(nomeRichiedente, gameId)).thenReturn(mockSession);
        when(mockGiocatoreRepository.saldoGiocatore(nomeRichiedente, gameId)).thenReturn(saldoRichiedente);

        propertyHandler.rispostaScambio(data, true, mockSession);

        verify(mockGiocatoreRepository).setSaldoGiocatore(nomeRichiedente, gameId, 100);
        verify(mockGiocatoreRepository).setSaldoGiocatore(nomeProprietario, gameId, -100);
        verify(mockMessageService, times(2)).updateBalance(anyMap(), eq(gameId), anyString());
        verify(mockPCPPRepository).setProprietario(nomeRichiedente, gameId, "Proprieta2");
        verify(mockPCPPRepository).setProprietario(nomeProprietario, gameId, "Proprieta1");
        verify(mockMessageService).rispostaGestisciProprieta("Scambio accettato", mockSession);
        verify(mockMessageService).sendSystemMessage(eq(gameId), contains("ha accettato lo scambio di"), anyMap(), eq(mockSession));
    }

    @Test
    void testUpdateProperties() throws IOException {
        String gameId = "game123";
        String playerName = "player1";
        List<PlayerProperties> playerPropertiesList = List.of(new PlayerProperties());

        when(mockGameHandler.getGameIdBySession(mockSession)).thenReturn(gameId);
        when(mockGameHandler.getPlayerNameBySession(mockSession)).thenReturn(playerName);
        when(mockPCPPRepository.findPlayerProperties(gameId, playerName)).thenReturn(playerPropertiesList);

        propertyHandler.updateProperties(mockSession);

        verify(mockMessageService).inviaMessaggio(mockSession, "updateProperties", "properties", playerPropertiesList);
    }

    @Test
    void testProperty() {
        Map<String, Object> data = Map.of(
                "property1", Map.of("nome", "Proprieta1", "idGiocatore", 1)
        );

        PlayerProperties result = propertyHandler.property(data, "property1");

        assertEquals("Proprieta1", result.getNome());
        assertEquals(1, result.getIdGiocatore());
    }

    @Test
    void testOffertaMonetaria() {
        Map<String, Object> data = Map.of("offertaMonetaria", 100);

        Integer result = propertyHandler.offertaMonetaria(data);

        assertEquals(100, result);
    }

    @Test
    void testOffertaMonetariaString() {
        Map<String, Object> data = Map.of("offertaMonetaria", "100");

        Integer result = propertyHandler.offertaMonetaria(data);

        assertEquals(100, result);
    }
}*/
