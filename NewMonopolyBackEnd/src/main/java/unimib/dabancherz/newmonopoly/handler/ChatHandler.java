package unimib.dabancherz.newmonopoly.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.beans.factory.annotation.Autowired;
import unimib.dabancherz.newmonopoly.MessageService;

@Component
public class ChatHandler {

    private final GameHandler gameHandler;
    private final MessageService messageService;

    // Inietta GameHandler tramite il costruttore
    @Autowired
    public ChatHandler(GameHandler gameHandler, MessageService messageService) {
        this.gameHandler = gameHandler;
        this.messageService = messageService;
    }

    public void chatHandler (String[] messageParts, WebSocketSession session) throws Exception {

        if(messageParts.length == 2) {
            String chatMessage = messageParts[1];
            String gameId = gameHandler.getGameIdBySession(session);
            String nameChat = gameHandler.getPlayerNameBySession(session);

            if (gameId == null) {
                // Non inviare nessun messaggio se non c'è un gameId associato
                return;
            }
            //invia il messaggio a tutti gli utenti collegati allo stesso gameID sotto forma di messaggioChat
            messageService.sendChatMessage(gameId,nameChat + ": " + chatMessage, gameHandler.getGameSessions());
        }
    }
}
