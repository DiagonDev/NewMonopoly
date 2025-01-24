package unimib.daBancherz.NewMonopoly.Handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;
import unimib.daBancherz.NewMonopoly.database.Repository.*;
import unimib.daBancherz.NewMonopoly.database.Entity.Partita;
import unimib.daBancherz.NewMonopoly.model.PlayerProperties;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TurnHandlerTest {

    private TurnHandler turnHandler;

    @Mock
    private GameHandler mockGameHandler;

    @Mock
    private MessageHandler mockMessageHandler;

    @Mock
    private GiocatoreRepository mockGiocatoreRepository;

    @Mock
    private PartitaOpportunitaRepository mockPartitaOpportunitaRepository;

    @Mock
    private PartitaRepository mockPartitaRepository;

    @Mock
    private PartitaCasellaPrezzoproprietaRepository mockPCPPRepository;

    @Mock
    private WebSocketSession mockSession;

    private final GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();

    @Test
    void testPayPrisonExit_PlayerPays() throws Exception {
        String gameId = "game123";
        String playerName = "player1";

        when(mockGameHandler.getGameIdBySession(mockSession)).thenReturn(gameId);
        when(mockGameHandler.getPlayerNameBySession(mockSession)).thenReturn(playerName);
        when(mockGiocatoreRepository.saldoGiocatore(playerName, gameId)).thenReturn(100);

        turnHandler.payPrisonExit(mockSession);

        verify(mockGiocatoreRepository).setSaldoGiocatore(playerName, gameId, 50);
        verify(mockSession).sendMessage(argThat(message -> {
            try {
                String payload = ((TextMessage) message).getPayload();
                return payload.contains("\"type\":\"exitPrison\"") && payload.contains("\"flag\":true");
            } catch (Exception e) {
                return false;
            }
        }));
    }


    @Test
    void testRollDice() throws Exception {
        String gameId = "game123";
        String playerName = "player1";

        when(mockGameHandler.getGameIdBySession(mockSession)).thenReturn(gameId);
        when(mockGameHandler.getPlayerNameBySession(mockSession)).thenReturn(playerName);

        int[] diceResults = turnHandler.rollDice(mockSession);

        assertTrue(diceResults[0] >= 1 && diceResults[0] <= 6);
        assertTrue(diceResults[1] >= 1 && diceResults[1] <= 6);

        verify(mockSession).sendMessage(any(TextMessage.class));
        verify(mockMessageHandler).sendSystemMessage(eq(gameId), contains("ha tirato i dati"), any(), eq(mockSession));
    }


}
