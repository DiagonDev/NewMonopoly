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
import unimib.dabancherz.newmonopoly.manager.BalanceManager;
import unimib.dabancherz.newmonopoly.manager.TurnManager;

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

    @Mock
    private TurnManager turnManager;

    @Mock
    private BalanceManager balanceManager;

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
    void testAfterConnectionClosed_MessageDispatcher() throws Exception {
        CloseStatus status = CloseStatus.NORMAL;

        dispatcher.afterConnectionClosed(session, status);

        verify(messageDispatcher, times(1)).afterConnectionClosed(session);
    }
}