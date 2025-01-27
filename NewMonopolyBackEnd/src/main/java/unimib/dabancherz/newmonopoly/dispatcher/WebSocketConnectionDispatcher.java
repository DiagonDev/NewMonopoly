package unimib.dabancherz.newmonopoly.dispatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.database.repository.PartitaRepository;
import unimib.dabancherz.newmonopoly.handler.GameHandler;
import unimib.dabancherz.newmonopoly.manager.BalanceManager;
import unimib.dabancherz.newmonopoly.manager.TurnManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketConnectionDispatcher implements WebSocketHandler {
    private final MessageDispatcher messageDispatcher;
    private final SpecialMessageDispatcher specialMessageDispatcher;
    private final GameHandler gameHandler;
    private final MessageService messageService;
    private final TurnManager turnManager;
    private final BalanceManager balanceManager;

    @Autowired
    public WebSocketConnectionDispatcher(MessageDispatcher messageDispatcher, SpecialMessageDispatcher specialMessageDispatcher, GameHandler gameHandler, MessageService messageService, TurnManager turnManager, BalanceManager balanceManager) {
        this.messageDispatcher = messageDispatcher;
        this.specialMessageDispatcher = specialMessageDispatcher;
        this.gameHandler = gameHandler;
        this.messageService = messageService;
        this.turnManager = turnManager;
        this.balanceManager = balanceManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Nuova connessione stabilita. ID sessione: " + session.getId());
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
        System.out.println("Errore nella connessione. ID sessione: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        messageDispatcher.afterConnectionClosed(session, status);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}