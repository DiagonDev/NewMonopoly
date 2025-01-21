package unimib.daBancherz.NewMonopoly.dataBase.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Giocatore;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Partita;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Partita_Probabilita;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.Probabilita;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import java.util.Optional;

@SpringBootTest
public class GameServiceTest {

    @MockBean
    private PartitaRepository partitaRepository;

    @MockBean
    private GiocatoreRepository giocatoreRepository;

    @MockBean
    private PartitaCasellaPrezzoproprietaRepository partitaCasellaPrezzoproprietaRepository;

    @MockBean
    private ProbabilitaRepository probabilitaRepository;

    @MockBean
    private PartitaProbabilitaRepository partitaProbabilitaRepository;

    @MockBean
    private ImprevistoRepository imprevistoRepository;

    @MockBean
    private PartitaImprevistoRepository partitaImprevistoRepository;

    @MockBean
    private PedinaRepository pedinaRepository;

    @Autowired
    private GameService gameService;

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

    /*@Test
    void testPopulateGameProbability(){
        String gameId = "12345";
        // Creiamo un mock di una partita esistente
        Partita partitaMock = new Partita();
        partitaMock.setCodiceInvito(gameId);

        when(partitaRepository.findById(gameId)).thenReturn(Optional.of(partitaMock));

        ArgumentCaptor<Partita_Probabilita> captor = ArgumentCaptor.forClass(Partita_Probabilita.class);
        verify(partitaProbabilitaRepository, times(1)).save(captor.capture());

        Partita_Probabilita savedPartitaProbabilita = captor.getValue();

        // Verifica che il giocatore salvato abbia i dati corretti
        assertEquals(gameId, savedGiocatore.getNome());
        assertEquals(1500, savedGiocatore.getSaldo());
        assertEquals("giocatore", savedGiocatore.getTipo());
        assertEquals(0, savedGiocatore.getPuntiFedelta());
    }*/
}
