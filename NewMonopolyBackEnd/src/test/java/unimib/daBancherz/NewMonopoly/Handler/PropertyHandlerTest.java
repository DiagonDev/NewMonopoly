package unimib.daBancherz.NewMonopoly.Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.database.Repository.*;
import unimib.daBancherz.NewMonopoly.model.PlayerProperties;

import java.util.List;

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
    private MessageHandler mockMessageHandler;
    @Mock
    private CasellaRepository mockCasellaRepository;
    @Mock
    private WebSocketSession mockSession;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        propertyHandler = new PropertyHandler(
                mockPCPPRepository,
                mockGiocatoreRepository,
                mockGameHandler,
                mockMessageHandler,
                mockCasellaRepository
        );
    }

    @Test
    void testAcquistaProprieta_SaldoSufficiente() throws Exception {
        String[] messageParts = {"acquista", "proprieta1"};
        when(mockGameHandler.getGameIdBySession(mockSession)).thenReturn("game123");
        when(mockGameHandler.getPlayerNameBySession(mockSession)).thenReturn("player1");
        when(mockPCPPRepository.prezzoCasella2("proprieta1", "game123")).thenReturn(100);
        when(mockGiocatoreRepository.saldoGiocatore("player1", "game123")).thenReturn(200);

        propertyHandler.acquistaProprieta(messageParts, mockSession);

        verify(mockPCPPRepository).setProprietario("player1", "game123", "proprieta1");
        verify(mockGiocatoreRepository).setSaldoGiocatore("player1", "game123", 100);
        verify(mockSession).sendMessage(any());
    }

    @Test
    void testGestisciProprieta() throws Exception {
        String gameId = "game123";
        String playerName = "player1";
        when(mockGameHandler.getGameIdBySession(mockSession)).thenReturn(gameId);
        when(mockGameHandler.getPlayerNameBySession(mockSession)).thenReturn(playerName);

        List<PlayerProperties> mockProperties = List.of(new PlayerProperties());
        when(mockPCPPRepository.findOtherPlayerProperties(gameId, playerName)).thenReturn(mockProperties);

        propertyHandler.gestisciProprieta(mockSession);

        verify(mockSession).sendMessage(argThat(message -> {
            try {
                String payload = ((TextMessage) message).getPayload();
                return payload.contains("allProperties") && payload.contains("properties");
            } catch (Exception e) {
                return false;
            }
        }));
    }

    @Test
    void testEffettuaScambio() throws Exception {
        PlayerProperties property1 = new PlayerProperties();
        PlayerProperties property2 = new PlayerProperties();

        String gameId = "game-123";
        String nomeRichiedente = "player1";
        String nomeProprietario = "player2";
        WebSocketSession mockSession2 = mock(WebSocketSession.class);

        when(mockGameHandler.getGameIdBySession(mockSession)).thenReturn(gameId);
        when(mockGameHandler.getSessionByPlayerName(nomeProprietario, gameId)).thenReturn(mockSession2);
        when(mockGiocatoreRepository.findNomeByidGiocatore(property1.getIdGiocatore())).thenReturn(nomeRichiedente);
        when(mockGiocatoreRepository.findNomeByidGiocatore(property2.getIdGiocatore())).thenReturn(nomeProprietario);

        propertyHandler.effettuaScambio(property1, property2, 100, mockSession);

        verify(mockSession2).sendMessage(argThat(message -> {
            try {
                String payload = ((TextMessage) message).getPayload();
                return payload.contains("exchangeRequest") && payload.contains("playerName");
            } catch (Exception e) {
                return false;
            }
        }));
    }

    @Test
    void testIpotecaProprieta() throws Exception {
        PlayerProperties property = new PlayerProperties();
        property.setNome("Proprieta1");
        property.setPrezzoCorrente(200);

        String gameId = "game123";
        String playerName = "player1";

        when(mockGameHandler.getGameIdBySession(mockSession)).thenReturn(gameId);
        when(mockGameHandler.getPlayerNameBySession(mockSession)).thenReturn(playerName);

        propertyHandler.ipotecaProprieta(property, mockSession);

        verify(mockPCPPRepository).setPrezzoCorrente(eq(100), eq(gameId), anyInt());
        verify(mockPCPPRepository).setProprietario(null, gameId, "Proprieta1");
        verify(mockMessageHandler).sendSystemMessage(eq(gameId), contains("ha ipotecato"), any(), eq(mockSession));
    }


    @Test
    void testUpdateProperties() throws Exception {
        String gameId = "game-123";
        String playerName = "player1";

        when(mockGameHandler.getGameIdBySession(mockSession)).thenReturn(gameId);
        when(mockGameHandler.getPlayerNameBySession(mockSession)).thenReturn(playerName);

        List<PlayerProperties> mockProperties = List.of(new PlayerProperties());
        when(mockPCPPRepository.findPlayerProperties(gameId, playerName)).thenReturn(mockProperties);

        propertyHandler.updateProperties(mockSession);

        verify(mockSession).sendMessage(argThat(message -> {
            try {
                String payload = ((TextMessage) message).getPayload();
                return payload.contains("updateProperties");
            } catch (Exception e) {
                return false;
            }
        }));
    }
}
