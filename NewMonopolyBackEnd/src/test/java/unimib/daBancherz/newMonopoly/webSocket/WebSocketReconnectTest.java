package unimib.daBancherz.newMonopoly.webSocket;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class WebSocketReconnectTest {

    @Autowired
    private WebSocketReconnect webSocketReconnect;

    @Test
    public void contextLoads() {
        // Verifica che il contesto dell'applicazione si carichi correttamente
        assertThat(webSocketReconnect).isNotNull();
    }
}