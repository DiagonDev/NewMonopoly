package unimib.daBancherz.NewMonopoly.Handler;


import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ChatHandlerTest {

    @Mock
    private GameHandler gameHandler; // Mock della dipendenza GameHandler

    @Mock
    private WebSocketSession session; // Mock della sessione WebSocket

    @Autowired
    private ChatHandler chatHandler; // L'oggetto da testare, con il mock di GameHandler iniettato

    @Test
    void testChatHandler_messageSentSuccessfully() throws Exception {
        // Arrange
        String[] messageParts = {"chat", "ciao"};
        String gameId = "Game-123";
        String playerName = "teo";

        // Comportamento del GameHandler mockato
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);

        List<WebSocketSession> playersInGame = Arrays.asList(session);
        when(gameHandler.getGameSessions()).thenReturn(Map.of(gameId, playersInGame));

        // Act
        chatHandler.chatHandler(messageParts, session); // Esegui il metodo che vogliamo testare

        // Assert
        // Verifica che sendMessage sia stato chiamato con il messaggio corretto
        String expectedMessage = "{\"type\":\"chat\",\"content\":\"teo: ciao\"}";

        // Verifica il messaggio JSON ignorando l'ordine delle chiavi
        verify(session).sendMessage(argThat(message -> {
            try {
                String payload = (String) message.getPayload();
                // Usa JSONassert per confrontare il messaggio
                JSONAssert.assertEquals(expectedMessage, payload, true);  // true indica di ignorare l'ordine delle chiavi
                return true;
            } catch (Exception e) {
                return false;
            }
        }));
    }

    @Test
    void testChatHandler_noMessageSent_whenMessagePartsLengthIsNotTwo() throws Exception {
        // Arrange
        String[] messageParts = {"chat"}; // Messaggio con meno di due parti

        // Act
        chatHandler.chatHandler(messageParts, session); // Esegui il metodo

        // Assert
        // Verifica che sendMessage non venga chiamato
        verify(session, times(0)).sendMessage(any(TextMessage.class)); // Non deve inviare messaggi
    }

    @Test
    void testSendChatMessage_playersInGame() throws Exception {
        // Arrange
        String gameId = "game-1";
        String message = "teo: ciao";
        List<WebSocketSession> playersInGame = Arrays.asList(session);

        // Comportamento del GameHandler mockato
        when(gameHandler.getGameSessions()).thenReturn(Map.of(gameId, playersInGame)); // Un giocatore nella partita

        // Act
        chatHandler.sendChatMessage(gameId, message); // Chiama il metodo per inviare il messaggio

        // Assert
        // Verifica che sendMessage sia stato chiamato con il messaggio corretto
        String expectedMessage = "{\"type\":\"chat\",\"content\":\"teo: ciao\"}";
        verify(session).sendMessage(argThat(messaggio -> {
            try {
                String payload = (String) messaggio.getPayload();
                // Usa JSONassert per confrontare il messaggio
                JSONAssert.assertEquals(expectedMessage, payload, true);  // true indica di ignorare l'ordine delle chiavi
                return true;
            } catch (Exception e) {
                return false;
            }
        }));
    }

    @Test
    void test_noGameIdAssociatedToSession() throws Exception {
        String[] messageParts = {"chat", "ciao"};
        String gameId = null;
        String playerName = "teo";

        // Comportamento del GameHandler mockato
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);  // gameId = null
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);  // "teo"

        List<WebSocketSession> playersInGame = Arrays.asList(session);
        when(gameHandler.getGameSessions()).thenReturn(Collections.singletonMap(null, playersInGame)); // Usa una mappa con chiave null

        // Act
        chatHandler.chatHandler(messageParts, session);
        verify(session, times(0)).sendMessage(any(TextMessage.class)); // Non deve inviare messaggi
    }

}



