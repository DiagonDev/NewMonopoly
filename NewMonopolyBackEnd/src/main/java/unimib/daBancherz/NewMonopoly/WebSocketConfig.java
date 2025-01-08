package unimib.daBancherz.NewMonopoly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // Iniezione dei WebSocketHandler tramite Spring
    //@Autowired
    //private WebSocketChatHandler webSocketChatHandler;

    @Autowired
    private WebSocketConnectionHandler webSocketConnectionHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketConnectionHandler, "/ws/connection")
                .setAllowedOrigins("http://localhost:5173");
    }
}
