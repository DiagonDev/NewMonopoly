package unimib.dabancherz.newmonopoly.dispatcher;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.handler.PropertyHandler;
import unimib.dabancherz.newmonopoly.model.PlayerProperties;
import java.util.Map;

@Component
public class SpecialMessageDispatcher {

    private final PropertyHandler propertyHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public SpecialMessageDispatcher(PropertyHandler propertyHandler) {
        this.propertyHandler = propertyHandler;
    }

    public void dispatchSpecialMessage(WebSocketSession session, String payload) throws Exception {
        Map<String, Object> data = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
        String type = (String) data.get("type");

        switch (type) {
            case "!EffettuaScambio":
                propertyHandler.effettuaScambio(data, session);
                break;
            case "!RispostaScambio":
                boolean flag = (boolean) data.get("exchangeAccepted");
                propertyHandler.rispostaScambio(data, flag, session);
                break;
            case "!CostruisciCasa":
                PlayerProperties property = objectMapper.convertValue(data.get("property"), PlayerProperties.class);
                Integer casine = (Integer) data.get("casine");
                propertyHandler.gestisciCase(property, casine, session);
                break;
            case "!IpotecaProprieta":
                PlayerProperties propertyIpotecata = objectMapper.convertValue(data.get("property"), PlayerProperties.class);
                propertyHandler.ipotecaProprieta(propertyIpotecata, session);
                break;
            default:
                throw new IllegalArgumentException("Tipo di messaggio non supportato: " + type);
        }
    }
}