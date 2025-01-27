package unimib.dabancherz.newmonopoly.dispatcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.handler.GameHandler;

import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketConnectionDispatcherTest {

    @Mock
    private MessageDispatcher messageDispatcher;

    @Mock
    private SpecialMessageDispatcher specialMessageDispatcher;

    @Mock
    private GameHandler gameHandler;

    @Mock
    private MessageService messageService;

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private WebSocketConnectionDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        lenient().when(session.getId()).thenReturn("session1");
    }

    @Test
    void testHandleMessage_NormalMessage() throws Exception {
        TextMessage message = new TextMessage("Hello World");
        dispatcher.handleMessage(session, message);
        verify(messageDispatcher, times(1)).dispatchMessage(session, "Hello World");
    }

    @Test
    void testHandleMessage_SpecialMessage() throws Exception {
        TextMessage message = new TextMessage("!SpecialCommand");
        dispatcher.handleMessage(session, message);
        verify(specialMessageDispatcher, times(1)).dispatchSpecialMessage(session, "!SpecialCommand");
    }

    @Test
    void testHandleTransportError() {
        Throwable exception = new RuntimeException("Test Error");
        dispatcher.handleTransportError(session, exception);
        verify(session, atLeastOnce()).getId();
    }

    @Test
    void testAfterConnectionClosed_WithGame() throws Exception {
        CloseStatus status = CloseStatus.NORMAL;
        when(gameHandler.getPlayerNameBySession(session)).thenReturn("Player1");
        when(gameHandler.getGameIdBySession(session)).thenReturn("game1");
        when(gameHandler.getGameSessions()).thenReturn(Map.of("game1", List.of(session)));
        dispatcher.afterConnectionClosed(session, status);
        verify(gameHandler, times(1)).removePlayerFromGame("game1", session);
        verify(messageService, times(1)).notifyPlayerDisconnected(eq("game1"), eq("Player1"), any());
    }

    @Test
    void testAfterConnectionClosed_WithoutGame() throws Exception {
        CloseStatus status = CloseStatus.NORMAL;
        when(gameHandler.getGameIdBySession(session)).thenReturn(null);
        dispatcher.afterConnectionClosed(session, status);
        verify(gameHandler, never()).removePlayerFromGame(anyString(), any());
        verify(messageService, never()).notifyPlayerDisconnected(anyString(), anyString(), any());
    }
}