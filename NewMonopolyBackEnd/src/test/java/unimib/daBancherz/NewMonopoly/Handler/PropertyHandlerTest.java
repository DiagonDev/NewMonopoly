package unimib.daBancherz.NewMonopoly.Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.MessageService;
import unimib.daBancherz.NewMonopoly.database.Repository.*;
import unimib.daBancherz.NewMonopoly.model.PlayerProperties;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        propertyHandler = new PropertyHandler(
                mockPCPPRepository,
                mockGiocatoreRepository,
                mockGameHandler,
                mockMessageService,
                mockCasellaRepository
        );
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
        verify(mockMessageService).sendSystemMessage(eq(gameId), contains("ha ipotecato"), any(), eq(mockSession));
    }

}
