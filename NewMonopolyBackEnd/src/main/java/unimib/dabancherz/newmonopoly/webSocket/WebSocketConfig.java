package unimib.dabancherz.newmonopoly.webSocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketConnectionDispatcher connectionHandler;

    // Constructor Injection
    public WebSocketConfig(WebSocketConnectionDispatcher connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(connectionHandler, "/ws/gameNewMonopoly")
                .setAllowedOrigins("*");
        // Aggiungere un altro handler per i messaggi di gioco se necessario
    }
}

