package unimib.dabancherz.newmonopoly.database.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import unimib.dabancherz.newmonopoly.database.entity.*;
import unimib.dabancherz.newmonopoly.database.repository.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameServiceTest {

    @Mock
    private PartitaRepository partitaRepository;
    @Mock
    private GiocatoreRepository giocatoreRepository;
    @Mock
    private PartitaCasellaPrezzoproprietaRepository partitaCasellaPrezzoproprietaRepository;
    @Mock
    private OpportunitaRepository opportunitaRepository;
    @Mock
    private PartitaOpportunitaRepository partitaOpportunitaRepository;
    @Mock
    private PedinaRepository pedinaRepository;
    @Mock
    private RegolafedeltaRepository regolafedeltaRepository;
    @Mock
    private PartitaRegolafedeltaRepository partitaRegolafedeltaRepository;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
     void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testCreateGameAndPlayer() {
        Partita nuovaPartita = new Partita();
        nuovaPartita.setCodiceInvito("gameId");
        nuovaPartita.setLivelloDifficolta("difficile");
        nuovaPartita.setStato("nonIniziata");
        nuovaPartita.setRandomizzazione(true);
        nuovaPartita.setCodiceInvito("gameId");

        Opportunita opportunita = new Opportunita();
        opportunita.setIdOpportunita(1);

        when(partitaRepository.save(any(Partita.class))).thenReturn(nuovaPartita);
        when(partitaRepository.findById("gameId")).thenReturn(Optional.of(nuovaPartita));
        when(opportunitaRepository.findAll()).thenReturn(Collections.singletonList(opportunita));
        doNothing().when(partitaCasellaPrezzoproprietaRepository).populateWithRandomizationTrue("gameId");
        doNothing().when(partitaCasellaPrezzoproprietaRepository).updatePrices("gameId");

        gameService.createGameAndPlayer("playerName", "difficile", "true", "gameId");

        verify(partitaRepository, times(1)).save(any(Partita.class));
        verify(giocatoreRepository, times(1)).save(any(Giocatore.class));
        verify(partitaCasellaPrezzoproprietaRepository, times(1)).populateWithRandomizationTrue("gameId");
        verify(partitaCasellaPrezzoproprietaRepository, times(1)).updatePrices("gameId");
        verify(partitaOpportunitaRepository, times(1)).save(any(Partita_Opportunita.class));
    }

    @Test
     void testCreateGameAndPlayerWithRandomizationFalse() {
        Partita nuovaPartita = new Partita();
        nuovaPartita.setCodiceInvito("gameId");
        nuovaPartita.setLivelloDifficolta("difficile");
        nuovaPartita.setStato("nonIniziata");
        nuovaPartita.setRandomizzazione(false);
        nuovaPartita.setCodiceInvito("gameId");

        Opportunita opportunita = new Opportunita();
        opportunita.setIdOpportunita(1);

        when(partitaRepository.save(any(Partita.class))).thenReturn(nuovaPartita);
        when(partitaRepository.findById("gameId")).thenReturn(Optional.of(nuovaPartita));
        when(opportunitaRepository.findAll()).thenReturn(Collections.singletonList(opportunita));
        doNothing().when(partitaCasellaPrezzoproprietaRepository).populateWithRandomizationFalse("gameId");
        doNothing().when(partitaCasellaPrezzoproprietaRepository).updatePrices("gameId");

        gameService.createGameAndPlayer("playerName", "difficile", "false", "gameId");

        verify(partitaRepository, times(1)).save(any(Partita.class));
        verify(giocatoreRepository, times(1)).save(any(Giocatore.class));
        verify(partitaCasellaPrezzoproprietaRepository, times(1)).populateWithRandomizationFalse("gameId");
        verify(partitaCasellaPrezzoproprietaRepository, times(1)).updatePrices("gameId");
        verify(partitaOpportunitaRepository, times(1)).save(any(Partita_Opportunita.class));
    }

    @Test
     void testAddPlayer() {
        Partita partita = new Partita();
        partita.setCodiceInvito("gameId");

        when(partitaRepository.findByCodiceInvito("gameId")).thenReturn(partita);

        gameService.addPlayer("playerName", "gameId");

        verify(giocatoreRepository, times(1)).save(any(Giocatore.class));
    }

    @Test
     void testAddPlayerPartitaNotFound() {
        when(partitaRepository.findByCodiceInvito("gameId")).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gameService.addPlayer("playerName", "gameId");
        });

        String expectedMessage = "La partita con ID gameId non esiste.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
     void testPopulateGameOpportunity() {
        Opportunita opportunita = new Opportunita();
        opportunita.setIdOpportunita(1);

        when(opportunitaRepository.findAll()).thenReturn(Collections.singletonList(opportunita));
        when(partitaRepository.findById("gameId")).thenReturn(Optional.of(new Partita()));

        gameService.populateGameOpportunity("gameId");

        verify(partitaOpportunitaRepository, times(1)).save(any(Partita_Opportunita.class));
    }

    @Test
     void testPopulateGameRoule() {
        Regolafedelta regolafedelta = new Regolafedelta();
        regolafedelta.setIdRegolafedelta(1);

        when(regolafedeltaRepository.findAll()).thenReturn(Collections.singletonList(regolafedelta));
        when(partitaRepository.findById("gameId")).thenReturn(Optional.of(new Partita()));

        gameService.populateGameRoule("gameId");

        verify(partitaRegolafedeltaRepository, times(1)).save(any(Partita_Regolafedelta.class));
    }

    @Test
     void testDeletePlayer() {
        when(giocatoreRepository.findIdByNomeAndPartitaCodiceInvito("playerName", "gameId")).thenReturn(1);

        gameService.deletePlayer("gameId", "playerName");

        verify(giocatoreRepository, times(1)).deleteByIdGiocatore(1);
    }

    @Test
     void testDeletePlayerIdGiocatoreNull() {
        when(giocatoreRepository.findIdByNomeAndPartitaCodiceInvito("playerName", "gameId")).thenReturn(null);

        gameService.deletePlayer("gameId", "playerName");

        verify(giocatoreRepository, never()).deleteByIdGiocatore(anyInt());
    }


    @Test
     void testGetUnusedPedineByPartita() {
        List<Integer> unusedPedine = Collections.singletonList(1);

        when(pedinaRepository.findUnusedPedineByPartita("gameId")).thenReturn(unusedPedine);

        List<Integer> result = gameService.getUnusedPedineByPartita("gameId");

        assertEquals(unusedPedine, result);
    }

    @Test
     void testGetPlayersWithIdLowerThan() {
        List<String> players = Collections.singletonList("player1");

        when(giocatoreRepository.findIdGiocatoreByNome("playerName", "gameId")).thenReturn(1);
        when(giocatoreRepository.findGiocatoriConIdMinore("gameId", 1)).thenReturn(players);

        List<String> result = gameService.getPlayersWithIdLowerThan("gameId", "playerName");

        assertEquals(players, result);
    }

    @Test
     void testGetPlayersWithIdLowerThanIdGiocatoreNull() {
        when(giocatoreRepository.findIdGiocatoreByNome("playerName", "gameId")).thenReturn(null);

        List<String> result = gameService.getPlayersWithIdLowerThan("gameId", "playerName");

        assertEquals(Collections.emptyList(), result);
    }

    @Test
     void testGetPlayersWithIdLowerThanEmptyList() {
        List<String> players = Collections.emptyList();

        when(giocatoreRepository.findIdGiocatoreByNome("playerName", "gameId")).thenReturn(1);
        when(giocatoreRepository.findGiocatoriConIdMinore("gameId", 1)).thenReturn(players);

        List<String> result = gameService.getPlayersWithIdLowerThan("gameId", "playerName");

        assertEquals(players, result);
    }
}