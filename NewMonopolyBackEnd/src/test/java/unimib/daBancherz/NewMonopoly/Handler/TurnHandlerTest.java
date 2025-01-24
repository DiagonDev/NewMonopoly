package unimib.daBancherz.NewMonopoly.Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;
import unimib.daBancherz.NewMonopoly.database.Repository.*;
import unimib.daBancherz.NewMonopoly.database.Entity.Partita;
import unimib.daBancherz.NewMonopoly.model.PlayerProperties;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TurnHandlerTest {

    private TurnHandler turnHandler;

    @Mock
    private GameHandler mockGameHandler;

    @Mock
    private MessageHandler mockMessageHandler;

    @Mock
    private GiocatoreRepository mockGiocatoreRepository;

    @Mock
    private PartitaOpportunitaRepository mockPartitaOpportunitaRepository;

    @Mock
    private PartitaRepository mockPartitaRepository;

    @Mock
    private PartitaCasellaPrezzoproprietaRepository mockPCPPRepository;

    @Mock
    private WebSocketSession mockSession;

    private final GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();

}
