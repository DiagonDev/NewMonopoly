package unimib.dabancherz.newmonopoly.dispatcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.handler.ChatHandler;
import unimib.dabancherz.newmonopoly.handler.GameHandler;
import unimib.dabancherz.newmonopoly.handler.PropertyHandler;
import unimib.dabancherz.newmonopoly.manager.TurnManager;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MessageDispatcherTest {

    @Mock
    private GameHandler gameHandler;
    @Mock
    private ChatHandler chatHandler;
    @Mock
    private TurnManager turnManager;
    @Mock
    private PropertyHandler propertyHandler;
    @Mock
    private MessageService messageService;
    @Mock
    private WebSocketSession session;

    @InjectMocks
    private MessageDispatcher messageDispatcher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void dispatchMessage_Ping() throws Exception {
        String payload = "Ping:";
        messageDispatcher.dispatchMessage(session, payload);
        verify(messageService, times(1)).sendPongMessage(session);
    }

    @Test
    void dispatchMessage_LanciaDadi() throws Exception {
        String payload = "LanciaDadi:";
        messageDispatcher.dispatchMessage(session, payload);
        verify(turnManager, times(1)).spostaPedina(session);
    }

    @Test
    void dispatchMessage_FineTurno() throws Exception {
        String payload = "FineTurno:";
        messageDispatcher.dispatchMessage(session, payload);
        verify(turnManager, times(1)).endTurn(session);
    }

    @Test
    void dispatchMessage_InizioPartita() throws Exception {
        String payload = "InizioPartita:";
        messageDispatcher.dispatchMessage(session, payload);
        verify(turnManager, times(1)).startTurn(session);
    }

    @Test
    void dispatchMessage_Create() throws Exception {
        String payload = "Create:game1";
        messageDispatcher.dispatchMessage(session, payload);
        verify(gameHandler, times(1)).createGame(any(), eq(session));
    }

    @Test
    void dispatchMessage_Partecipa() throws Exception {
        String payload = "Partecipa:game1";
        messageDispatcher.dispatchMessage(session, payload);
        verify(gameHandler, times(1)).joinGame(any(), eq(session));
    }

    @Test
    void dispatchMessage_MessaggioUtente() throws Exception {
        String payload = "MessaggioUtente:hello";
        messageDispatcher.dispatchMessage(session, payload);
        verify(chatHandler, times(1)).chatHandler(any(), eq(session));
    }

    @Test
    void dispatchMessage_SceltaPedina() throws Exception {
        String payload = "SceltaPedina:pedina1";
        messageDispatcher.dispatchMessage(session, payload);
        verify(gameHandler, times(1)).choosePedina(any(), eq(session));
    }

    @Test
    void dispatchMessage_AcquistaProprieta() throws Exception {
        String payload = "AcquistaProprieta:prop1";
        messageDispatcher.dispatchMessage(session, payload);
        verify(propertyHandler, times(1)).acquistaProprieta(any(), eq(session));
    }

    @Test
    void dispatchMessage_AcquistaProprietaPunti() throws Exception {
        String payload = "AcquistaProprietaPunti:prop1";
        messageDispatcher.dispatchMessage(session, payload);
        verify(propertyHandler, times(1)).acquistaProprietaPunti(any(), eq(session));
    }

    @Test
    void dispatchMessage_PingScambiaProprieta() throws Exception {
        String payload = "PingScambiaProprieta:";
        messageDispatcher.dispatchMessage(session, payload);
        verify(propertyHandler, times(1)).gestisciProprieta(session);
    }

    @Test
    void dispatchMessage_PagaUscitaPrigione() throws Exception {
        String payload = "PagaUscitaPrigione:";
        messageDispatcher.dispatchMessage(session, payload);
        verify(turnManager, times(1)).payPrisonExit(session);
    }

    @Test
    void dispatchMessage_RichiestaUpdateProperties() throws Exception {
        String payload = "RichiestaUpdateProperties:";
        messageDispatcher.dispatchMessage(session, payload);
        verify(propertyHandler, times(1)).updateProperties(session);
    }

    @Test
    void dispatchMessage_UnsupportedMessage() {
        String payload = "Unsupported:message";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> messageDispatcher.dispatchMessage(session, payload));
        assertEquals("Tipo di messaggio non supportato: Unsupported", exception.getMessage());
    }
}