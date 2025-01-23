package unimib.daBancherz.NewMonopoly.Handler;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;


class ChatHandlerTest {

    @Mock
    private GameHandler gameHandler;

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private ChatHandler chatHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testChatHandler_ValidMessage() throws Exception {
        String[] messageParts = {"chat", "Hello, world!"};
        when(gameHandler.getGameIdBySession(session)).thenReturn("game123");
        when(gameHandler.getPlayerNameBySession(session)).thenReturn("player1");

        chatHandler.chatHandler(messageParts, session);

        verify(gameHandler, times(1)).getGameIdBySession(session);
        verify(gameHandler, times(1)).getPlayerNameBySession(session);
    }

    @Test
    void testChatHandler_InvalidMessage() throws Exception {
        String[] messageParts = {"invalid"};
        chatHandler.chatHandler(messageParts, session);
        verifyNoInteractions(gameHandler);
    }

    @Test
    void testSendChatMessage_ValidGameId() throws Exception {
        WebSocketSession mockSession = mock(WebSocketSession.class);
        List<WebSocketSession> sessions = List.of(mockSession);
        when(gameHandler.getGameSessions()).thenReturn(Map.of("game123", sessions));

        chatHandler.sendChatMessage("game123", "Test message");

        verify(mockSession, times(1)).sendMessage(any(TextMessage.class));
    }

    @Test
    void testSendChatMessage_InvalidGameId() throws Exception {
        when(gameHandler.getGameSessions()).thenReturn(Map.of());

        chatHandler.sendChatMessage("invalidGameId", "Test message");

        verifyNoInteractions(session);
    }
}



