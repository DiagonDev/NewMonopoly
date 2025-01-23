package unimib.daBancherz.NewMonopoly.Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.*;
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
        // Arrange
        String[] messageParts = {"acquista", "proprieta1"};
        when(mockGameHandler.getGameIdBySession(mockSession)).thenReturn("game123");
        when(mockGameHandler.getPlayerNameBySession(mockSession)).thenReturn("player1");
        when(mockPCPPRepository.prezzoCasella2("proprieta1", "game123")).thenReturn(100);
        when(mockGiocatoreRepository.saldoGiocatore("player1", "game123")).thenReturn(200);

        // Act
        propertyHandler.acquistaProprieta(messageParts, mockSession);

        // Assert
        verify(mockPCPPRepository).setProprietario("player1", "game123", "proprieta1");
        verify(mockGiocatoreRepository).setSaldoGiocatore("player1", "game123", 100);
        verify(mockSession).sendMessage(any());
    }
    @Test
    void testGestisciProprieta() throws Exception {
        // Arrange
        String gameId = "game123";
        String playerName = "player1";
        WebSocketSession mockSession = mock(WebSocketSession.class);
        when(mockGameHandler.getGameIdBySession(mockSession)).thenReturn(gameId);
        when(mockGameHandler.getPlayerNameBySession(mockSession)).thenReturn(playerName);

        List<PlayerProperties> mockProperties = List.of(new PlayerProperties(/* Mock Property */));
        when(mockPCPPRepository.findOtherPlayerProperties(gameId, playerName)).thenReturn(mockProperties);

        // Act
        propertyHandler.gestisciProprieta(mockSession);

        // Assert
        verify(mockSession).sendMessage(argThat(message -> {
            try {
                String payload = ((TextMessage) message).getPayload();
                return payload.contains("allProperties") && payload.contains("properties");
            } catch (Exception e) {
                return false;
            }
        }));
    }

}
