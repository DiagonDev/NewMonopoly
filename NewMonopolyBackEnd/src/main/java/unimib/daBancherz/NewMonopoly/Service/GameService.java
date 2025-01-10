package unimib.daBancherz.NewMonopoly.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unimib.daBancherz.NewMonopoly.Entity.Giocatore;
import unimib.daBancherz.NewMonopoly.Entity.Partita;
import unimib.daBancherz.NewMonopoly.Repository.GiocatoreRepository;
import unimib.daBancherz.NewMonopoly.Repository.PartitaRepository;

@Service
public class GameService {

    private final PartitaRepository partitaRepository;
    private final GiocatoreRepository giocatoreRepository;

    @Autowired
    public GameService(PartitaRepository partitaRepository, GiocatoreRepository giocatoreRepository) {
        this.partitaRepository = partitaRepository;
        this.giocatoreRepository = giocatoreRepository;
    }

    public void createGameAndPlayer(String playerName, String difficulty, String gameId) {
        // Crea la partita
        Partita nuovaPartita = new Partita();
        nuovaPartita.setLivelloDifficolta(difficulty);
        nuovaPartita.setStato("nonIniziata");
        nuovaPartita.setRandomizzazione(false);
        nuovaPartita.setCodiceInvito(gameId);

        // Salva la partita nel database
        partitaRepository.save(nuovaPartita);

        // Crea il giocatore
        Giocatore nuovoGiocatore = new Giocatore();
        nuovoGiocatore.setIdpartita(nuovaPartita);
        nuovoGiocatore.setNome(playerName);
        nuovoGiocatore.setSaldo(100);
        nuovoGiocatore.setTipo("admin");
        nuovoGiocatore.setPuntiFedelta(0);

        // Salva il giocatore nel database
        giocatoreRepository.save(nuovoGiocatore);
    }

    public void addPlayer(String playerName, String gameId) {
        /// TO DO: controllare
        Partita partita = partitaRepository.findByCodiceInvito(gameId);
        if (partita == null) {
            throw new IllegalArgumentException("La partita con ID " + gameId + " non esiste.");
        }
        Giocatore nuovoGiocatore = new Giocatore();

        nuovoGiocatore.setIdpartita(partita);
        nuovoGiocatore.setNome(playerName);
        nuovoGiocatore.setSaldo(100);
        nuovoGiocatore.setTipo("giocatore");
        nuovoGiocatore.setPuntiFedelta(0);

        // Salva il giocatore nel database
        giocatoreRepository.save(nuovoGiocatore);
    }
}
