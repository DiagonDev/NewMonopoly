package unimib.dabancherz.newmonopoly.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.database.entity.Giocatore;
import unimib.dabancherz.newmonopoly.database.entity.Partita;
import unimib.dabancherz.newmonopoly.database.repository.*;
import unimib.dabancherz.newmonopoly.model.PlayerProperties;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class PropertyHandlerTest {
    @Mock
    private PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    @Mock
    private GiocatoreRepository giocatoreRepository;
    @Mock
    private CasellaRepository casellaRepository;
    @Mock
    private PartitaRepository partitaRepository;
    @Mock
    private GameHandler gameHandler;
    @Mock
    private MessageService messageService;
    @Mock
    private WebSocketSession session;
    @InjectMocks
    private PropertyHandler propertyHandler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        propertyHandler = new PropertyHandler(pCPPRepository, giocatoreRepository, gameHandler, messageService, casellaRepository, partitaRepository);
    }

    @Test
    void testAcquistaProprieta_SufficientBalance() throws Exception {
        String[] messageParts = {"acquista", "property1"};
        String gameId = "game-1";
        String playerName = "player1";
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(pCPPRepository.prezzoCasella2(anyString(), anyString())).thenReturn(100);
        when(giocatoreRepository.saldoGiocatore(anyString(), anyString())).thenReturn(200);
        propertyHandler.acquistaProprieta(messageParts, session);
        verify(giocatoreRepository, times(1)).setSaldoGiocatore(playerName, gameId, 100);
        verify(messageService, times(1)).inviaMessaggio(session, "acquistoRiuscito");
    }

    @Test
    void testAcquistaProprieta_InsufficientBalance() throws Exception {
        String[] messageParts = {"acquista", "property1"};
        String gameId = "game-1";
        String playerName = "player1";
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(pCPPRepository.prezzoCasella2(anyString(), anyString())).thenReturn(200);
        when(giocatoreRepository.saldoGiocatore(anyString(), anyString())).thenReturn(100);
        propertyHandler.acquistaProprieta(messageParts, session);
        verify(messageService, times(1)).inviaMessaggio(session, "acquistoFallito");
    }

    @Test
    void testGestisciProprieta() throws IOException {
        String gameId = "game-1";
        String playerName = "player1";
        List<PlayerProperties> playerProperties = Collections.emptyList();
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(pCPPRepository.findOtherPlayerProperties(gameId, playerName)).thenReturn(playerProperties);
        propertyHandler.gestisciProprieta(session);
        verify(messageService, times(1)).inviaMessaggio(session, "allProperties", "properties", playerProperties);
    }

    @Test
    void testGestisciCase_Success() throws Exception {
        String gameId = "game-1";
        String playerName = "player1";
        PlayerProperties property = new PlayerProperties();
        property.setColore("red");
        property.setNumCasa(2);
        property.setPrezzoCasaCorrente(100);
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(giocatoreRepository.saldoGiocatore(anyString(), anyString())).thenReturn(1000);
        when(casellaRepository.countByColore(anyString())).thenReturn(3);
        when(pCPPRepository.countProprietaColore(anyString(), anyString(), anyString())).thenReturn(3);
        propertyHandler.gestisciCase(property, 1, session);
        verify(giocatoreRepository, times(1)).setSaldoGiocatore(playerName, gameId, 300);
        verify(messageService, times(1)).updateBalance(any(), eq(gameId), eq(playerName));
        verify(messageService, times(1)).sendSystemMessage(eq(gameId), anyString(), any(), eq(session));
    }

    @Test
    void testGestisciCase_Failure() throws Exception {
        String gameId = "game-1";
        String playerName = "player1";
        PlayerProperties property = new PlayerProperties();
        property.setColore("red");
        property.setNumCasa(2);
        property.setPrezzoCasaCorrente(100);
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(giocatoreRepository.saldoGiocatore(anyString(), anyString())).thenReturn(100);
        when(casellaRepository.countByColore(anyString())).thenReturn(3);
        when(pCPPRepository.countProprietaColore(anyString(), anyString(), anyString())).thenReturn(1);
        propertyHandler.gestisciCase(property, 1, session);
        verify(messageService, times(1)).inviaMessaggio(session, "costruzioneFallita");
    }

    @Test
    void testEffettuaScambio() throws Exception {
        String gameId = "game-1";
        String playerName1 = "player1";
        String playerName2 = "player2";
        PlayerProperties property1 = new PlayerProperties();
        property1.setIdGiocatore(1); // Assicurati che l'ID del giocatore sia impostato
        PlayerProperties property2 = new PlayerProperties();
        property2.setIdGiocatore(2); // Assicurati che l'ID del giocatore sia impostato
        Map<String, Object> data = new HashMap<>();
        data.put("property1", property1);
        data.put("property2", property2);
        data.put("offertaMonetaria", 50);
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getSessionByPlayerName(anyString(), anyString())).thenReturn(session);
        when(giocatoreRepository.findNomeByidGiocatore(1)).thenReturn(playerName1);
        when(giocatoreRepository.findNomeByidGiocatore(2)).thenReturn(playerName2);
        propertyHandler.effettuaScambio(data, session);
        verify(messageService, times(1)).exchangeRequestMessage(eq(playerName1), any(), any(), eq(50), any());
        verify(messageService, times(1)).sendSystemMessage(eq(gameId), anyString(), any(), eq(session));
    }

    @Test
    void testRispostaScambio_Accepted() throws Exception {
        String gameId = "game-1";
        String playerName1 = "player1";
        String playerName2 = "player2";
        PlayerProperties property1 = new PlayerProperties();
        property1.setIdGiocatore(1); // Imposta l'ID del giocatore
        PlayerProperties property2 = new PlayerProperties();
        property2.setIdGiocatore(2); // Imposta l'ID del giocatore
        Map<String, Object> data = new HashMap<>();
        data.put("property1", property1);
        data.put("property2", property2);
        data.put("offertaMonetaria", 50);
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getSessionByPlayerName(anyString(), eq(gameId))).thenReturn(session);
        when(giocatoreRepository.findNomeByidGiocatore(1)).thenReturn(playerName1);
        when(giocatoreRepository.findNomeByidGiocatore(2)).thenReturn(playerName2);
        when(giocatoreRepository.saldoGiocatore(eq(playerName1), eq(gameId))).thenReturn(100);
        when(giocatoreRepository.saldoGiocatore(eq(playerName2), eq(gameId))).thenReturn(100);
        propertyHandler.rispostaScambio(data, true, session);
        verify(messageService, times(1)).sendSystemMessage(eq(gameId), anyString(), any(), eq(session));
        verify(messageService, times(1)).rispostaGestisciProprieta(anyString(), eq(session));
    }

    @Test
    void testRispostaScambio_Declined() throws Exception {
        String gameId = "game-1";
        String playerName1 = "player1";
        String playerName2 = "player2";
        PlayerProperties property1 = new PlayerProperties();
        PlayerProperties property2 = new PlayerProperties();
        Map<String, Object> data = new HashMap<>();
        data.put("property1", property1);
        data.put("property2", property2);
        data.put("offertaMonetaria", 50);
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getSessionByPlayerName(anyString(), anyString())).thenReturn(session);
        when(giocatoreRepository.findNomeByidGiocatore(anyInt())).thenReturn(playerName1, playerName2);
        propertyHandler.rispostaScambio(data, false, session);
        verify(messageService, times(1)).sendSystemMessage(anyString(), anyString(), any(), any());
        verify(messageService, times(1)).rispostaGestisciProprieta(anyString(), any());
    }

    @Test
    public void testIpotecaProprieta() throws Exception {
        PlayerProperties property = new PlayerProperties();
        property.setNome("property1");
        property.setPrezzoCorrente(200);
        String gameId = "game-1";
        String playerName = "player1";

        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(pCPPRepository.findPosizioneByNomeCasellaAndIdpartita(anyString(), anyString())).thenReturn(1);
        when(pCPPRepository.findPlayerProperties(gameId, playerName)).thenReturn(Collections.emptyList());

        propertyHandler.ipotecaProprieta(property, session);

        verify(pCPPRepository, times(1)).setPrezzoCorrente(eq(100), eq(gameId), eq(1));
        verify(giocatoreRepository, times(1)).setSaldoGiocatore(eq(playerName), eq(gameId), eq(-100));
        verify(pCPPRepository, times(1)).setProprietario(isNull(), eq(gameId), eq("property1"));
        verify(messageService, times(1)).updateBalance(any(), eq(gameId), eq(playerName));
        verify(messageService, times(1)).inviaMessaggio(eq(session), eq("updateProperties"), eq("properties"), any());
        verify(messageService, times(1)).sendSystemMessage(eq(gameId), anyString(), any(), eq(session));
    }

    @Test
    void testUpdateProperties() throws IOException {
        String gameId = "game-1";
        String playerName = "player1";
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        propertyHandler.updateProperties(session);
        verify(pCPPRepository, times(1)).findPlayerProperties(anyString(), anyString());
        verify(messageService, times(1)).inviaMessaggio(any(), anyString(), anyString(), any());
    }

    @Test
    void testNumPuntiFedelta() {
        String playerName = "player1";
        String gameId = "game-1";
        Giocatore giocatore = new Giocatore();
        giocatore.setTipo("imprenditore");
        Partita partita = new Partita();
        partita.setLivelloDifficolta("Facile");
        when(giocatoreRepository.findGiocatoreByIdpartita_CodiceInvitoAndNome(anyString(), anyString())).thenReturn(giocatore);
        when(partitaRepository.findByCodiceInvito(anyString())).thenReturn(partita);
        int punti = propertyHandler.numPuntiFedelta(playerName, gameId);
        assertEquals(4, punti);
    }

    @Test
    void testAcquistaProprietaPunti_SufficientPoints() throws Exception {
        String[] messageParts = {"acquista", "property1"};
        String gameId = "game-1";
        String playerName = "player1";
        Giocatore giocatore = new Giocatore();
        giocatore.setPuntiFedelta(400);
        giocatore.setSaldo(100);
        giocatore.setTipo("imprenditore");
        Partita partita = new Partita();
        partita.setLivelloDifficolta("Facile");
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(pCPPRepository.prezzoCasella2(anyString(), anyString())).thenReturn(100);
        when(giocatoreRepository.findGiocatoreByIdpartita_CodiceInvitoAndNome(anyString(), anyString())).thenReturn(giocatore);
        when(partitaRepository.findByCodiceInvito(anyString())).thenReturn(partita); // Aggiungi questo mock
        when(gameHandler.getGameSessions()).thenReturn(Collections.emptyMap());
        propertyHandler.acquistaProprietaPunti(messageParts, session);
        verify(giocatoreRepository, times(1)).setPuntiGiocatore(playerName, gameId, 400);
        verify(messageService, times(1)).inviaMessaggio(session, "acquistoRiuscito");
    }

    @Test
    void testAcquistaProprietaPunti_Combination() throws Exception {
        String[] messageParts = {"acquista", "property1"};
        String gameId = "game-1";
        String playerName = "player1";
        Giocatore giocatore = new Giocatore();
        giocatore.setPuntiFedelta(200);
        giocatore.setSaldo(100);
        giocatore.setTipo("imprenditore");
        Partita partita = new Partita();
        partita.setLivelloDifficolta("Facile");
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(pCPPRepository.prezzoCasella2(anyString(), anyString())).thenReturn(100);
        when(giocatoreRepository.findGiocatoreByIdpartita_CodiceInvitoAndNome(anyString(), anyString())).thenReturn(giocatore);
        when(partitaRepository.findByCodiceInvito(anyString())).thenReturn(partita);
        when(gameHandler.getGameSessions()).thenReturn(Collections.emptyMap());
        propertyHandler.acquistaProprietaPunti(messageParts, session);
        verify(giocatoreRepository, times(1)).setSaldoGiocatore(playerName, gameId, 50);
        verify(giocatoreRepository, times(1)).setPuntiGiocatore(playerName, gameId, 200);
        verify(messageService, times(1)).inviaMessaggio(session, "acquistoRiuscito");
    }
    @Test
    void testAcquistaProprietaPunti_InsufficientPointsAndBalance() throws Exception {
        String[] messageParts = {"acquista", "property1"};
        String gameId = "game-1";
        String playerName = "player1";
        Giocatore giocatore = new Giocatore();
        giocatore.setPuntiFedelta(100);
        giocatore.setSaldo(50);
        giocatore.setTipo("imprenditore");
        Partita partita = new Partita();
        partita.setLivelloDifficolta("Facile");
        when(gameHandler.getGameIdBySession(session)).thenReturn(gameId);
        when(gameHandler.getPlayerNameBySession(session)).thenReturn(playerName);
        when(pCPPRepository.prezzoCasella2(anyString(), anyString())).thenReturn(100);
        when(giocatoreRepository.findGiocatoreByIdpartita_CodiceInvitoAndNome(anyString(), anyString())).thenReturn(giocatore);
        when(partitaRepository.findByCodiceInvito(anyString())).thenReturn(partita);
        propertyHandler.acquistaProprietaPunti(messageParts, session);
        verify(messageService, times(1)).inviaMessaggio(session, "acquistoFallito");
    }
    @Test
    void testOffertaMonetaria_StringInput() {
        Map<String, Object> data = new HashMap<>();
        data.put("offertaMonetaria", "50");
        Integer result = propertyHandler.offertaMonetaria(data);
        assertEquals(50, result);
    }

    @Test
    void testOffertaMonetaria_IntegerInput() {
        Map<String, Object> data = new HashMap<>();
        data.put("offertaMonetaria", 50);
        Integer result = propertyHandler.offertaMonetaria(data);
        assertEquals(50, result);
    }

    @Test
    void testOffertaMonetaria_InvalidInput() {
        Map<String, Object> data = new HashMap<>();
        data.put("offertaMonetaria", 50.0); // Invalid type
        assertThrows(IllegalArgumentException.class, () -> propertyHandler.offertaMonetaria(data));
    }

    @Test
    void testNumPuntiFedelta_Facile_Imprenditore() {
        String playerName = "player1";
        String gameId = "game-1";
        Giocatore giocatore = new Giocatore();
        giocatore.setTipo("imprenditore");
        Partita partita = new Partita();
        partita.setLivelloDifficolta("Facile");
        when(giocatoreRepository.findGiocatoreByIdpartita_CodiceInvitoAndNome(anyString(), anyString())).thenReturn(giocatore);
        when(partitaRepository.findByCodiceInvito(anyString())).thenReturn(partita);
        int punti = propertyHandler.numPuntiFedelta(playerName, gameId);
        assertEquals(4, punti);
    }
    @Test
    void testNumPuntiFedelta_Facile_NonImprenditore() {
        String playerName = "player1";
        String gameId = "game-1";
        Giocatore giocatore = new Giocatore();
        giocatore.setTipo("giocatore");
        Partita partita = new Partita();
        partita.setLivelloDifficolta("Facile");
        when(giocatoreRepository.findGiocatoreByIdpartita_CodiceInvitoAndNome(anyString(), anyString())).thenReturn(giocatore);
        when(partitaRepository.findByCodiceInvito(anyString())).thenReturn(partita);
        int punti = propertyHandler.numPuntiFedelta(playerName, gameId);
        assertEquals(2, punti);
    }
    @Test
    void testNumPuntiFedelta_Medio_Imprenditore() {
        String playerName = "player1";
        String gameId = "game-1";
        Giocatore giocatore = new Giocatore();
        giocatore.setTipo("imprenditore");
        Partita partita = new Partita();
        partita.setLivelloDifficolta("Medio");
        when(giocatoreRepository.findGiocatoreByIdpartita_CodiceInvitoAndNome(anyString(), anyString())).thenReturn(giocatore);
        when(partitaRepository.findByCodiceInvito(anyString())).thenReturn(partita);
        int punti = propertyHandler.numPuntiFedelta(playerName, gameId);
        assertEquals(8, punti);
    }
    @Test
    void testNumPuntiFedelta_Medio_NonImprenditore() {
        String playerName = "player1";
        String gameId = "game-1";
        Giocatore giocatore = new Giocatore();
        giocatore.setTipo("giocatore");
        Partita partita = new Partita();
        partita.setLivelloDifficolta("Medio");
        when(giocatoreRepository.findGiocatoreByIdpartita_CodiceInvitoAndNome(anyString(), anyString())).thenReturn(giocatore);
        when(partitaRepository.findByCodiceInvito(anyString())).thenReturn(partita);
        int punti = propertyHandler.numPuntiFedelta(playerName, gameId);
        assertEquals(4, punti);
    }
    @Test
    void testNumPuntiFedelta_Difficile_Imprenditore() {
        String playerName = "player1";
        String gameId = "game-1";
        Giocatore giocatore = new Giocatore();
        giocatore.setTipo("imprenditore");
        Partita partita = new Partita();
        partita.setLivelloDifficolta("Difficile");
        when(giocatoreRepository.findGiocatoreByIdpartita_CodiceInvitoAndNome(anyString(), anyString())).thenReturn(giocatore);
        when(partitaRepository.findByCodiceInvito(anyString())).thenReturn(partita);
        int punti = propertyHandler.numPuntiFedelta(playerName, gameId);
        assertEquals(16, punti);
    }
    @Test
    void testNumPuntiDifficile_NonImprenditore() {
        String playerName = "player1";
        String gameId = "game-1";
        Giocatore giocatore = new Giocatore();
        giocatore.setTipo("giocatore");
        Partita partita = new Partita();
        partita.setLivelloDifficolta("Difficile");
        when(giocatoreRepository.findGiocatoreByIdpartita_CodiceInvitoAndNome(anyString(), anyString())).thenReturn(giocatore);
        when(partitaRepository.findByCodiceInvito(anyString())).thenReturn(partita);
        int punti = propertyHandler.numPuntiFedelta(playerName, gameId);
        assertEquals(8, punti);
    }
}