package unimib.daBancherz.NewMonopoly.WebSocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import unimib.daBancherz.NewMonopoly.Handler.GameHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketConnectionHandler connectionHandler;

    // Constructor Injection
    public WebSocketConfig(WebSocketConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(connectionHandler, "/ws/gameNewMonopoly")
                .setAllowedOrigins("*");
        // Aggiungere un altro handler per i messaggi di gioco se necessario
    }
}

