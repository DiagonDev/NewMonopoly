package unimib.dabancherz.newmonopoly.dispatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.handler.ChatHandler;
import unimib.dabancherz.newmonopoly.handler.GameHandler;
import unimib.dabancherz.newmonopoly.handler.PropertyHandler;
import unimib.dabancherz.newmonopoly.manager.TurnManager;
import org.springframework.web.socket.WebSocketSession;

@Component
public class MessageDispatcher {

    private final GameHandler gameHandler;
    private final ChatHandler chatHandler;
    private final TurnManager turnManager;
    private final PropertyHandler propertyHandler;
    private final MessageService messageService;

    @Autowired
    public MessageDispatcher(GameHandler gameHandler, ChatHandler chatHandler, TurnManager turnManager,
                             PropertyHandler propertyHandler, MessageService messageService) {
        this.gameHandler = gameHandler;
        this.chatHandler = chatHandler;
        this.turnManager = turnManager;
        this.propertyHandler = propertyHandler;
        this.messageService = messageService;
    }

    public void dispatchMessage(WebSocketSession session, String payload) throws Exception {
        String[] parts = payload.split(":");
        switch (parts[0]) {
            case "Ping":
                messageService.sendPongMessage(session);
                break;
            case "LanciaDadi":
                turnManager.spostaPedina(session);
                break;
            case "FineTurno":
                turnManager.endTurn(session);
                break;
            case "InizioPartita":
                turnManager.startTurn(session);
                break;
            case "Create":
                gameHandler.createGame(parts, session);
                break;
            case "Partecipa":
                gameHandler.joinGame(parts, session);
                break;
            case "MessaggioUtente":
                chatHandler.chatHandler(parts, session);
                break;
            case "SceltaPedina":
                gameHandler.choosePedina(parts, session);
                break;
            case "AcquistaProprieta":
                propertyHandler.acquistaProprieta(parts, session);
                break;
            case "AcquistaProprietaPunti":
                propertyHandler.acquistaProprietaPunti(parts, session);
                break;
            case "PingScambiaProprieta":
                propertyHandler.gestisciProprieta(session);
                break;
            case "PagaUscitaPrigione":
                turnManager.payPrisonExit(session);
                break;
            case "RichiestaUpdateProperties":
                propertyHandler.updateProperties(session);
                break;
            case "AbbandonaPartita":
                gameHandler.removePlayerFromGame(gameHandler.getGameIdBySession(session), session);
                break;
            default:
                throw new IllegalArgumentException("Tipo di messaggio non supportato: " + parts[0]);
        }
    }
}
