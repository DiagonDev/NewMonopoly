package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.GiocatoreRepository;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.PartitaImprevistoRepository;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.PartitaProbabilitaRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TurnHandlerTest {

    @Autowired
    private TurnHandler turnHandler;

    @MockBean
    private GameHandler gameHandler;

    @MockBean
    private MessageHandler messageHandler;

    @MockBean
    private GiocatoreRepository giocatoreRepository;

    @MockBean
    private PartitaProbabilitaRepository partitaProbabilitaRepository;

    @MockBean
    private PartitaImprevistoRepository partitaImprevistoRepository;

    @MockBean
    private WebSocketSession session;

    private GameBoardSingleton gameBoard;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GAME_ID = "testGame";
    private static final String PLAYER_NAME = "teo";

    @BeforeEach
    void setUp() {
        when(gameHandler.getGameIdBySession(session)).thenReturn(GAME_ID);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(PLAYER_NAME);
        when(gameHandler.getGameSessions()).thenReturn(Collections.singletonMap(GAME_ID, List.of(session)));

        // Mockiamo il Singleton
        gameBoard = mock(GameBoardSingleton.class);
        // Injectiamo il mock nella classe TurnHandler
        turnHandler = new TurnHandler(gameHandler, messageHandler, giocatoreRepository, partitaProbabilitaRepository, partitaImprevistoRepository);
        turnHandler.gameBoard = gameBoard;
    }

    @Test
    void testStartTurn_SendsCorrectMessages() throws Exception {
        // Mock: il giocatore NON è in prigione
        when(gameBoard.isPlayerInPrison(GAME_ID, PLAYER_NAME)).thenReturn(false);

        // Act
        turnHandler.startTurn(session);

        // Verifica che il messaggio di turno sia inviato al giocatore attuale
        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(captor.capture());

        TextMessage sentMessage = captor.getValue();
        Map<String, Object> sentData = objectMapper.readValue(sentMessage.getPayload(), Map.class);

        assertEquals("turn", sentData.get("type"));
        assertEquals(true, sentData.get("content"));
        assertEquals(PLAYER_NAME, sentData.get("playername"));

        // Verifica che venga inviato un messaggio di sistema
        verify(messageHandler, times(1))
                .sendSystemMessage(eq(GAME_ID), contains("È il turno di: " + PLAYER_NAME), anyMap(), eq(session));
    }

    @Test
    void testStartTurn_PlayerInPrison_HasProbabilitaCard() throws Exception {
        // Mock: il giocatore è in prigione e ha la carta probabilità
        when(gameBoard.isPlayerInPrison(GAME_ID, PLAYER_NAME)).thenReturn(true);
        when(partitaProbabilitaRepository.possiedeCarta(GAME_ID, PLAYER_NAME)).thenReturn(true);
        when(partitaImprevistoRepository.possiedeCarta(GAME_ID, PLAYER_NAME)).thenReturn(false);

        // Act
        turnHandler.startTurn(session);

        // Verifica che il giocatore venga liberato
        verify(gameBoard, times(1)).setPlayerPrison(GAME_ID, PLAYER_NAME, false);

        // Verifica che la carta sia rimossa
        verify(partitaProbabilitaRepository, times(1))
                .setGiocatore(eq(GAME_ID), isNull(), eq("esci_prigione"));

        // Verifica che venga inviato un messaggio di sistema
        verify(messageHandler, times(1))
                .sendSystemMessage(eq(GAME_ID), contains(PLAYER_NAME + " è uscito di prigione"), anyMap(), eq(session));
    }

    @Test
    void testStartTurn_PlayerInPrison_HasImprevistoCard() throws Exception {
        // Mock: il giocatore è in prigione e ha la carta imprevisto
        when(gameBoard.isPlayerInPrison(GAME_ID, PLAYER_NAME)).thenReturn(true);
        when(partitaProbabilitaRepository.possiedeCarta(GAME_ID, PLAYER_NAME)).thenReturn(false);
        when(partitaImprevistoRepository.possiedeCarta(GAME_ID, PLAYER_NAME)).thenReturn(true);

        // Act
        turnHandler.startTurn(session);

        // Verifica che il giocatore venga liberato
        verify(gameBoard, times(1)).setPlayerPrison(GAME_ID, PLAYER_NAME, false);

        // Verifica che la carta sia rimossa
        verify(partitaImprevistoRepository, times(1))
                .setGiocatore(eq(GAME_ID), isNull(), eq("esci_prigione"));

        // Verifica che venga inviato un messaggio di sistema
        verify(messageHandler, times(1))
                .sendSystemMessage(eq(GAME_ID), contains(PLAYER_NAME + " è uscito di prigione"), anyMap(), eq(session));
    }

    @Test
    void testStartTurn_PlayerInPrison_NoEscapeCards() throws Exception {
        // Mock: il giocatore è in prigione e NON ha carte per uscire
        when(gameBoard.isPlayerInPrison(GAME_ID, PLAYER_NAME)).thenReturn(true);
        when(partitaProbabilitaRepository.possiedeCarta(GAME_ID, PLAYER_NAME)).thenReturn(false);
        when(partitaImprevistoRepository.possiedeCarta(GAME_ID, PLAYER_NAME)).thenReturn(false);

        // Act
        turnHandler.startTurn(session);

        // Verifica che venga inviato il messaggio di prigione
        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(2)).sendMessage(captor.capture());

        TextMessage sentMessage = captor.getValue();
        Map<String, Object> sentData = objectMapper.readValue(sentMessage.getPayload(), Map.class);

        assertEquals("prison", sentData.get("type"));
    }

}