/*package unimib.daBancherz.NewMonopoly.database.Service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.*;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.*;
import unimib.daBancherz.NewMonopoly.dataBase.Service.GameService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.*;

@SpringBootTest
public class GameServiceTest {

    @Mock
    private PartitaRepository partitaRepository;

    @Mock
    private GiocatoreRepository giocatoreRepository;

    @Mock
    private PartitaCasellaPrezzoproprietaRepository partitaCasellaPrezzoproprietaRepository;

    @Mock
    private ProbabilitaRepository probabilitaRepository;

    @Mock
    private PartitaProbabilitaRepository partitaProbabilitaRepository;

    @Mock
    private ImprevistoRepository imprevistoRepository;

    @Mock
    private PartitaImprevistoRepository partitaImprevistoRepository;

    @Mock
    private PedinaRepository pedinaRepository;

    @Autowired
    private GameService gameService;

    @Test
    void testCreateGameAndPlayer() {
        // Dati di input
        String playerName = "Admin";
        String difficulty = "easy";
        String randomization = "true";
        String gameId = "12345";

        // Mock per la partita
        Partita partitaMock = new Partita();
        partitaMock.setCodiceInvito(gameId);
        partitaMock.setLivelloDifficolta(difficulty);
        partitaMock.setRandomizzazione(true);

        // Mock per gli imprevisti e probabilità
        Imprevisto imprevistoMock = new Imprevisto();
        Probabilita probabilitaMock = new Probabilita();

        // Configura il comportamento dei repository
        when(partitaRepository.save(any(Partita.class))).thenReturn(partitaMock);
        when(partitaRepository.findById(gameId)).thenReturn(Optional.of(partitaMock));
        when(imprevistoRepository.findAll()).thenReturn(List.of(imprevistoMock));
        when(probabilitaRepository.findAll()).thenReturn(List.of(probabilitaMock));

        // Usa doNothing() per metodi void
        doNothing().when(partitaCasellaPrezzoproprietaRepository).populateWithRandomizationTrue(anyString());
        doNothing().when(partitaCasellaPrezzoproprietaRepository).updatePrices(anyString());

        // Configura salvataggi per imprevisti e probabilità
        when(partitaImprevistoRepository.save(any(Partita_Imprevisto.class))).thenReturn(new Partita_Imprevisto());
        when(partitaProbabilitaRepository.save(any(Partita_Probabilita.class))).thenReturn(new Partita_Probabilita());

        // Esegui il metodo da testare
        gameService.createGameAndPlayer(playerName, difficulty, randomization, gameId);

        // Verifica che i metodi siano stati chiamati correttamente
        verify(partitaRepository, times(1)).save(any(Partita.class));
        verify(giocatoreRepository, times(1)).save(any(Giocatore.class));
        verify(partitaCasellaPrezzoproprietaRepository, times(1)).populateWithRandomizationTrue(gameId);
        verify(partitaImprevistoRepository, times(1)).save(any(Partita_Imprevisto.class));
        verify(partitaProbabilitaRepository, times(1)).save(any(Partita_Probabilita.class));
    }

    @Test
    void testAddPlayerSuccess() {
        // Dati di input
        String playerName = "Giocatore1";
        String gameId = "12345";

        // Creiamo un mock di una partita esistente
        Partita partitaMock = new Partita();
        partitaMock.setCodiceInvito(gameId);

        // Simuliamo il comportamento di findByCodiceInvito
        when(partitaRepository.findByCodiceInvito(gameId)).thenReturn(partitaMock);

        // Eseguiamo il metodo addPlayer
        gameService.addPlayer(playerName, gameId);

        // Catturiamo l'argomento passato al metodo save di GiocatoreRepository
        ArgumentCaptor<Giocatore> captor = ArgumentCaptor.forClass(Giocatore.class);
        verify(giocatoreRepository, times(1)).save(captor.capture());

        // Otteniamo l'oggetto Giocatore catturato
        Giocatore savedGiocatore = captor.getValue();

        // Verifica che il giocatore salvato abbia i dati corretti
        assertEquals(playerName, savedGiocatore.getNome());
        assertEquals(1500, savedGiocatore.getSaldo());
        assertEquals("giocatore", savedGiocatore.getTipo());
        assertEquals(0, savedGiocatore.getPuntiFedelta());
    }

    @Test
    void testPopulateGameProbability(){
        String gameId = "12345";
        // Creiamo un mock di una partita esistente
        Partita partitaMock = new Partita();
        partitaMock.setCodiceInvito(gameId);
        Probabilita mockProbabilita = new Probabilita();

        when(partitaRepository.findById(gameId)).thenReturn(Optional.of(partitaMock));
        when(probabilitaRepository.findAll()).thenReturn(List.of(mockProbabilita));

        gameService.populateGameProbability(gameId);

        verify(partitaProbabilitaRepository, times(1)).save(any(Partita_Probabilita.class));
    }

    @Test
    void testPopulateGameUnexpected(){
        String gameId = "12345";

        Partita partitaMock = new Partita();
        partitaMock.setCodiceInvito(gameId);

        Imprevisto mockImprevisto = new Imprevisto();

        when(partitaRepository.findById(gameId)).thenReturn(Optional.of(partitaMock));
        when(imprevistoRepository.findAll()).thenReturn(List.of(mockImprevisto));

        gameService.populateGameUnexpected(gameId);

        verify(partitaImprevistoRepository, times(1)).save(any(Partita_Imprevisto.class));
    }

    @Test
    void testDeletePlayer() {
        String gameId = "game123";
        String playerName = "TestPlayer";

        when(giocatoreRepository.findIdByNomeAndPartitaCodiceInvito(playerName, gameId)).thenReturn(1);
        doNothing().when(giocatoreRepository).deleteByIdGiocatore(1);

        gameService.deletePlayer(gameId, playerName);

        verify(giocatoreRepository, times(1)).deleteByIdGiocatore(1);
    }

    @Test
    void testGetUnusedPedineByPartita() {
        // Dati di input
        String gameId = "12345";

        // Crea una lista di pedine non utilizzate (simulata)
        List<Integer> unusedPedineMock = Arrays.asList(1, 2, 3, 4, 5);

        // Configura il comportamento del repository
        when(pedinaRepository.findUnusedPedineByPartita(gameId)).thenReturn(unusedPedineMock);

        // Esegui il metodo da testare
        List<Integer> result = gameService.getUnusedPedineByPartita(gameId);

        // Verifica che il repository sia stato chiamato con il giusto gameId
        verify(pedinaRepository, times(1)).findUnusedPedineByPartita(gameId);

        // Verifica che il risultato sia corretto
        assertEquals(unusedPedineMock, result);
    }

    @Test
    void testGetPlayersWithIdLowerThan() {
        // Arrange
        String gameId = "game123";
        String playerName = "TestPlayer";

        // Mock the repository to return a list of players
        when(giocatoreRepository.findIdGiocatoreByNome(playerName, gameId)).thenReturn(1);
        when(giocatoreRepository.findGiocatoriConIdMinore(gameId, 1)).thenReturn(Arrays.asList("Player1", "Player2"));

        // Act
        List<String> result = gameService.getPlayersWithIdLowerThan(gameId, playerName);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains("Player1"));
        assertTrue(result.contains("Player2"));
    }
}
*/