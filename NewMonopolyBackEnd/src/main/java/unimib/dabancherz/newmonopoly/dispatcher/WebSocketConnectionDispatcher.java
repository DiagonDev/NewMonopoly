package unimib.dabancherz.newmonopoly.dispatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WebSocketConnectionDispatcher implements WebSocketHandler {
    private final MessageDispatcher messageDispatcher;
    private final SpecialMessageDispatcher specialMessageDispatcher;
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConnectionDispatcher.class);

    @Autowired
    public WebSocketConnectionDispatcher(MessageDispatcher messageDispatcher, SpecialMessageDispatcher specialMessageDispatcher) {
        this.messageDispatcher = messageDispatcher;
        this.specialMessageDispatcher = specialMessageDispatcher;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        logger.info("Nuova connessione stabilita. ID sessione: {}", session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = message.getPayload().toString();
        if (!payload.contains("!")) {
            messageDispatcher.dispatchMessage(session, payload);
        } else {
            specialMessageDispatcher.dispatchSpecialMessage(session, payload);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        logger.info("Errore nella connessione. ID sessione: {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        messageDispatcher.afterConnectionClosed(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}