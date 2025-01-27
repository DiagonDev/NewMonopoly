package unimib.dabancherz.newmonopoly.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.handler.PropertyHandler;
import unimib.dabancherz.newmonopoly.model.PlayerProperties;
import static org.junit.jupiter.api.Assertions.assertThrows;


import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialMessageDispatcherTest {

    @Mock
    private PropertyHandler propertyHandler;

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private SpecialMessageDispatcher dispatcher;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, Object> createBasePayload(String type) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", type);
        return data;
    }

    @Test
    void testDispatchSpecialMessage_EffettuaScambio() throws Exception {
        Map<String, Object> data = createBasePayload("!EffettuaScambio");
        String payload = objectMapper.writeValueAsString(data);

        dispatcher.dispatchSpecialMessage(session, payload);

        verify(propertyHandler, times(1)).effettuaScambio(eq(data), eq(session));
    }

    @Test
    void testDispatchSpecialMessage_RispostaScambio() throws Exception {
        Map<String, Object> data = createBasePayload("!RispostaScambio");
        data.put("exchangeAccepted", true);
        String payload = objectMapper.writeValueAsString(data);

        dispatcher.dispatchSpecialMessage(session, payload);

        verify(propertyHandler, times(1)).rispostaScambio(eq(data), eq(true), eq(session));
    }

    @Test
    void testDispatchSpecialMessage_CostruisciCasa() throws Exception {
        Map<String, Object> data = createBasePayload("!CostruisciCasa");
        PlayerProperties property = new PlayerProperties();
        data.put("property", property);
        data.put("casine", 2);
        String payload = objectMapper.writeValueAsString(data);

        dispatcher.dispatchSpecialMessage(session, payload);

        verify(propertyHandler, times(1)).gestisciCase(any(PlayerProperties.class), eq(2), eq(session));
    }

    @Test
    void testDispatchSpecialMessage_IpotecaProprieta() throws Exception {
        Map<String, Object> data = createBasePayload("!IpotecaProprieta");
        PlayerProperties property = new PlayerProperties();
        data.put("property", property);
        String payload = objectMapper.writeValueAsString(data);

        dispatcher.dispatchSpecialMessage(session, payload);

        verify(propertyHandler, times(1)).ipotecaProprieta(any(PlayerProperties.class), eq(session));
    }

    @Test
    void testDispatchSpecialMessage_UnknownType() {
        Map<String, Object> data = createBasePayload("!TipoSconosciuto");
        String payload;
        try {
            payload = objectMapper.writeValueAsString(data);

            Exception exception = assertThrows(IllegalArgumentException.class, () ->
                    dispatcher.dispatchSpecialMessage(session, payload));

            assert(exception.getMessage()).contains("Tipo di messaggio non supportato");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}