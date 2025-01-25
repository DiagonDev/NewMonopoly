package unimib.dabancherz.newmonopoly.webSocket;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class WebSocketConfigTest {

    @Test
    void testWebSocketHandlerRegistration() {
        // Mock del WebSocketConnectionHandler
        WebSocketConnectionDispatcher connectionHandler = mock(WebSocketConnectionDispatcher.class);

        // Mock del WebSocketHandlerRegistry
        WebSocketHandlerRegistry registry = mock(WebSocketHandlerRegistry.class);

        // Mock del WebSocketHandlerRegistration
        WebSocketHandlerRegistration registration = mock(WebSocketHandlerRegistration.class);

        // Simuliamo il comportamento di addHandler() per restituire il mock della registrazione
        when(registry.addHandler(any(), anyString())).thenReturn(registration);

        // Creiamo l'oggetto di configurazione WebSocket
        WebSocketConfig webSocketConfig = new WebSocketConfig(connectionHandler);

        // Chiamiamo il metodo da testare
        webSocketConfig.registerWebSocketHandlers(registry);

        // Verifica che addHandler sia stato chiamato con i parametri corretti
        verify(registry).addHandler(connectionHandler, "/ws/gameNewMonopoly");

        // Verifica che setAllowedOrigins("*") sia stato chiamato
        verify(registration).setAllowedOrigins("*");
    }
}