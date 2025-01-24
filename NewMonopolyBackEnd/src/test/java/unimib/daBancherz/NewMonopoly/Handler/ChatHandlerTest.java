package unimib.daBancherz.NewMonopoly.Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.MessageService;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

class ChatHandlerTest {

    @Mock
    private GameHandler gameHandler;

    @Mock
    private MessageService messageService;

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private ChatHandler chatHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void chatHandler_ShouldSendMessage_WhenMessagePartsAreValid() throws Exception {
        String[] messageParts = {"chat", "Hello World"};
        String gameId = "game-1";
        String playerName = "Player1";
        List<WebSocketSession> sessions = List.of(session);


        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(gameHandler.getGameSessions()).thenReturn(Map.of("game123", sessions));

        chatHandler.chatHandler(messageParts, session);

        verify(messageService).sendChatMessage(gameId, "Player1: Hello World", Map.of("game123", sessions));
    }

    @Test
    void chatHandler_ShouldNotSendMessage_WhenGameIdIsNull() throws Exception {
        String[] messageParts = {"chat", "Hello World"};

        when(gameHandler.getGameIdBySession(session)).thenReturn(null);

        chatHandler.chatHandler(messageParts, session);

        verifyNoInteractions(messageService);
    }

    @Test
    void chatHandler_ShouldNotSendMessage_WhenMessagePartsAreInvalid() throws Exception {
        String[] messageParts = {"chat"};

        chatHandler.chatHandler(messageParts, session);

        verifyNoInteractions(messageService);
    }
}
