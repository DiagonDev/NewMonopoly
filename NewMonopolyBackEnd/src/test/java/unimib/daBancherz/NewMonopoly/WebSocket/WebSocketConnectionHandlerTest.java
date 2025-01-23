package unimib.daBancherz.NewMonopoly.WebSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Handler.ChatHandler;
import unimib.daBancherz.NewMonopoly.Handler.GameHandler;
import unimib.daBancherz.NewMonopoly.Handler.PropertyHandler;
import unimib.daBancherz.NewMonopoly.Handler.TurnHandler;
import unimib.daBancherz.NewMonopoly.model.PlayerProperties;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WebSocketConnectionHandlerTest {

    @Mock
    private GameHandler gameHandler;

    @Mock
    private ChatHandler chatHandler;

    @Mock
    private TurnHandler turnHandler;

    @Mock
    private WebSocketReconnect reconnect;

    @Mock
    private PropertyHandler propertyHandler;

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private WebSocketConnectionHandler webSocketConnectionHandler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(session.getId()).thenReturn("testSessionId"); // Imposta un valore di ritorno per session.getId()
    }

    @Test
    public void testAfterConnectionEstablished() {
        webSocketConnectionHandler.afterConnectionEstablished(session);
        assertTrue(webSocketConnectionHandler.playerSessions.containsKey("testSessionId"));
    }

    @Test
    public void testHandleMessagePing() throws Exception {
        String messagePayload = "Ping:";
        when(session.getId()).thenReturn("testSessionId");

        webSocketConnectionHandler.handleMessage(session, new TextMessage(messagePayload));

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(messageCaptor.capture());

        Map<String, String> pongResponse = new ObjectMapper().readValue(messageCaptor.getValue().getPayload(), Map.class);
        assertEquals("pong", pongResponse.get("type"));
        assertEquals("pong", pongResponse.get("content"));
    }

    @Test
    public void testHandleMessageLanciaDadi() throws Exception {
        String messagePayload = "LanciaDadi:";
        webSocketConnectionHandler.handleMessage(session, new TextMessage(messagePayload));
        verify(turnHandler, times(1)).spostaPedina(session);
    }

    @Test
    public void testHandleMessageFineTurno() throws Exception {
        String messagePayload = "FineTurno:";
        webSocketConnectionHandler.handleMessage(session, new TextMessage(messagePayload));
        verify(turnHandler, times(1)).endTurn(session);
    }

    @Test
    public void testHandleMessageInizioPartita() throws Exception {
        String messagePayload = "InizioPartita:";
        webSocketConnectionHandler.handleMessage(session, new TextMessage(messagePayload));
        verify(turnHandler, times(1)).startTurn(session);
    }

    @Test
    public void testHandleMessageCreate() throws Exception {
        String messagePayload = "Create:";
        webSocketConnectionHandler.handleMessage(session, new TextMessage(messagePayload));
        verify(gameHandler, times(1)).handleGameMessage(any(String[].class), eq(session));
    }

    @Test
    public void testHandleMessageMessaggioUtente() throws Exception {
        String messagePayload = "MessaggioUtente:";
        webSocketConnectionHandler.handleMessage(session, new TextMessage(messagePayload));
        verify(chatHandler, times(1)).chatHandler(any(String[].class), eq(session));
    }

    @Test
    public void testHandleMessageAcquistaProprieta() throws Exception {
        String messagePayload = "AcquistaProprieta:";
        webSocketConnectionHandler.handleMessage(session, new TextMessage(messagePayload));
        verify(propertyHandler, times(1)).acquistaProprieta(any(String[].class), eq(session));
    }

    @Test
    public void testHandleMessagePingScambiaProprieta() throws Exception {
        String messagePayload = "PingScambiaProprieta:";
        webSocketConnectionHandler.handleMessage(session, new TextMessage(messagePayload));
        verify(propertyHandler, times(1)).gestisciProprieta(eq(session));
    }

    @Test
    public void testHandleMessagePagaUscitaPrigione() throws Exception {
        String messagePayload = "PagaUscitaPrigione:";
        webSocketConnectionHandler.handleMessage(session, new TextMessage(messagePayload));
        verify(turnHandler, times(1)).payPrisonExit(eq(session));
    }

    @Test
    public void testHandleMessageRichiestaUpdateProperties() throws Exception {
        String messagePayload = "RichiestaUpdateProperties:";
        webSocketConnectionHandler.handleMessage(session, new TextMessage(messagePayload));
        verify(propertyHandler, times(1)).updateProperties(eq(session));
    }

    @Test
    public void testHandleTransportError() {
        when(session.getId()).thenReturn("testSessionId"); // Imposta un valore di ritorno per session.getId()
        webSocketConnectionHandler.handleTransportError(session, new Exception("Errore"));

        assertFalse(webSocketConnectionHandler.playerSessions.containsKey("testSessionId"));
    }

    @Test
    public void testAfterConnectionClosed() throws Exception {
        when(session.getId()).thenReturn("testSessionId");
        when(gameHandler.getPlayerNameBySession(session)).thenReturn("player1");
        when(gameHandler.getGameIdBySession(session)).thenReturn("game1");

        webSocketConnectionHandler.afterConnectionClosed(session, null);

        assertFalse(webSocketConnectionHandler.playerSessions.containsKey("testSessionId"));
        verify(gameHandler, times(1)).removePlayerFromGame("game1", session);
        verify(gameHandler, times(1)).notifyPlayerDisconnected("game1", "player1");
    }

    @Test
    public void testHandleMessageEffettuaScambio() throws Exception {
        String messagePayload = "{\"type\":\"!EffettuaScambio\", \"property1\":{}, \"property2\":{}, \"offertaMonetaria\":\"100\"}";
        webSocketConnectionHandler.handleMessage(session, new TextMessage(messagePayload));
        verify(propertyHandler, times(1)).effettuaScambio(any(PlayerProperties.class), any(PlayerProperties.class), eq(100), eq(session));
    }

    @Test
    public void testHandleMessageRispostaScambio() throws Exception {
        String messagePayload = "{\"type\":\"!RispostaScambio\", \"property1\":{}, \"property2\":{}, \"offertaMonetaria\":\"100\", \"exchangeAccepted\":true}";
        webSocketConnectionHandler.handleMessage(session, new TextMessage(messagePayload));
        verify(propertyHandler, times(1)).rispostaScambio(any(PlayerProperties.class), any(PlayerProperties.class), eq(100), eq(true), eq(session));
    }

    @Test
    public void testHandleMessageCostruisciCasa() throws Exception {
        String messagePayload = "{\"type\":\"!CostruisciCasa\", \"property\":{}, \"casine\":2}";
        webSocketConnectionHandler.handleMessage(session, new TextMessage(messagePayload));
        verify(propertyHandler, times(1)).gestisciCase(any(PlayerProperties.class), eq(2), eq(session));
    }

    @Test
    public void testHandleMessageIpotecaProprieta() throws Exception {
        String messagePayload = "{\"type\":\"!IpotecaProprieta\", \"property\":{}}";
        webSocketConnectionHandler.handleMessage(session, new TextMessage(messagePayload));
        verify(propertyHandler, times(1)).ipotecaProprieta(any(PlayerProperties.class), eq(session));
    }

    @Test
    public void testHandleMessageSceltaPedina() throws Exception {
        String messagePayload = "SceltaPedina:";
        webSocketConnectionHandler.handleMessage(session, new TextMessage(messagePayload));
        verify(gameHandler, times(1)).choosePedina(any(String[].class), eq(session));
    }

    @Test
    public void testHandleMessageDefaultCase() {
        String messagePayload = "UnknownMessage:";
        assertThrows(IllegalArgumentException.class, () -> {
            webSocketConnectionHandler.handleMessage(session, new TextMessage(messagePayload));
        });
    }

    @Test
    public void testHandleMessageEffettuaScambioInvalidOffertaMonetaria() throws Exception {
        String messagePayload = "{\"type\":\"!EffettuaScambio\", \"property1\":{}, \"property2\":{}, \"offertaMonetaria\":true}";
        assertThrows(IllegalArgumentException.class, () -> {
            webSocketConnectionHandler.handleMessage(session, new TextMessage(messagePayload));
        });
    }

    @Test
    public void testHandleMessageRispostaScambioInvalidOffertaMonetaria() throws Exception {
        String messagePayload = "{\"type\":\"!RispostaScambio\", \"property1\":{}, \"property2\":{}, \"offertaMonetaria\":true, \"exchangeAccepted\":true}";
        assertThrows(IllegalArgumentException.class, () -> {
            webSocketConnectionHandler.handleMessage(session, new TextMessage(messagePayload));
        });
    }

    @Test
    public void testHandleMessageDefaultCaseWithPrefix() {
        String messagePayload = "{\"type\":\"!UnknownType\"}";
        assertThrows(IllegalArgumentException.class, () -> {
            webSocketConnectionHandler.handleMessage(session, new TextMessage(messagePayload));
        });
    }

    @Test
    public void testErrorDuringPlayerDisconnectionNotification() throws Exception {
        when(session.getId()).thenReturn("testSessionId");
        when(gameHandler.getPlayerNameBySession(session)).thenReturn("player1");
        when(gameHandler.getGameIdBySession(session)).thenReturn("game1");

        doThrow(new RuntimeException("Errore")).when(gameHandler).notifyPlayerDisconnected("game1", "player1");

        webSocketConnectionHandler.afterConnectionClosed(session, null);

        assertFalse(webSocketConnectionHandler.playerSessions.containsKey("testSessionId"));
        verify(gameHandler, times(1)).removePlayerFromGame("game1", session);
        verify(gameHandler, times(1)).notifyPlayerDisconnected("game1", "player1");
    }

    @Test
    public void testSupportsPartialMessages() {
        assertFalse(webSocketConnectionHandler.supportsPartialMessages());
    }
}