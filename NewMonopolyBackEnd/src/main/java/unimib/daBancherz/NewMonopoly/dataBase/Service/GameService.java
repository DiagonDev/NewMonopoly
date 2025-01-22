package unimib.daBancherz.NewMonopoly.dataBase.Service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unimib.daBancherz.NewMonopoly.dataBase.Entity.*;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.*;

import java.util.Collections;
import java.util.List;

@Service
public class GameService {

    private final PartitaRepository partitaRepository;
    private final GiocatoreRepository giocatoreRepository;
    private final PartitaCasellaPrezzoproprietaRepository partitaCasellaPrezzoproprietaRepository;
    private final OpportunitaRepository opportunitaRepository;
    private final PartitaOpportunitaRepository partitaOpportunitaRepository;
    private final PedinaRepository pedinaRepository;
    private final RegolafedeltaRepository regolafedeltaRepository;
    private final PartitaRegolafedeltaRepository partitaRegolafedeltaRepository;

    private final String errorePartita = "Partita non trovata";

    @Autowired
    public GameService(PartitaRepository partitaRepository, GiocatoreRepository giocatoreRepository, PartitaCasellaPrezzoproprietaRepository partitaCasellaPrezzoproprietaRepository, OpportunitaRepository opportunitaRepository, PartitaOpportunitaRepository partitaOpportunitaRepository, PedinaRepository pedinaRepository, RegolafedeltaRepository regolafedeltaRepository, PartitaRegolafedeltaRepository partitaRegolafedeltaRepository) {
        this.partitaRepository = partitaRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.partitaCasellaPrezzoproprietaRepository = partitaCasellaPrezzoproprietaRepository;
        this.opportunitaRepository = opportunitaRepository;
        this.partitaOpportunitaRepository = partitaOpportunitaRepository;
        this.pedinaRepository = pedinaRepository;
        this.regolafedeltaRepository = regolafedeltaRepository;
        this.partitaRegolafedeltaRepository = partitaRegolafedeltaRepository;
    }

    //Crea la partita, aggiunge l'adim, popola partita_casella_prezzoproprietà in base alla randomizzazione
    public void createGameAndPlayer(String playerName, String difficulty, String randomization, String gameId) {
        // Crea la partita
        Partita nuovaPartita = new Partita();
        nuovaPartita.setLivelloDifficolta(difficulty);
        nuovaPartita.setStato("nonIniziata");
        nuovaPartita.setRandomizzazione(randomization.equals("true"));
        nuovaPartita.setCodiceInvito(gameId);
        partitaRepository.save(nuovaPartita);       // Salva la partita nel database

        //inizializzo prezzo caselle
        if (nuovaPartita.getRandomizzazione())
            partitaCasellaPrezzoproprietaRepository.populateWithRandomizationTrue(gameId);
        else
            partitaCasellaPrezzoproprietaRepository.populateWithRandomizationFalse(gameId);
        partitaCasellaPrezzoproprietaRepository.updatePrices(gameId);
        // Crea il giocatore
        Giocatore nuovoGiocatore = new Giocatore();
        nuovoGiocatore.setIdpartita(nuovaPartita);
        nuovoGiocatore.setNome(playerName);
        nuovoGiocatore.setSaldo(1500);
        nuovoGiocatore.setTipo("admin");
        nuovoGiocatore.setPuntiFedelta(0);
        giocatoreRepository.save(nuovoGiocatore);   // Salva il giocatore nel database

        populateGameOpportunity(gameId);    //popola probabilita e imprevisto
        populateGameRoule(gameId);     //popola regolafedelta
    }

    //aggiunge un nuovo giocatore
    public void addPlayer(String playerName, String gameId) {
        Partita partita = partitaRepository.findByCodiceInvito(gameId);
        if (partita == null) {
            throw new IllegalArgumentException("La partita con ID " + gameId + " non esiste.");
        }
        Giocatore nuovoGiocatore = new Giocatore();

        nuovoGiocatore.setIdpartita(partita);
        nuovoGiocatore.setNome(playerName);
        nuovoGiocatore.setSaldo(1500);
        nuovoGiocatore.setTipo("giocatore");
        nuovoGiocatore.setPuntiFedelta(0);
        giocatoreRepository.save(nuovoGiocatore);
    }

    public void populateGameOpportunity(String gameId) {
        // Recupera tutte le probabilità
        List<Opportunita> listaOpportunita = opportunitaRepository.findAll();
        Partita partita = partitaRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException(errorePartita));

        // Per ogni probabilità, crea un nuovo record in Partita_Probabilita
        for (Opportunita opportunita : listaOpportunita) {
            Partita_Opportunita partitaOpportunita = new Partita_Opportunita();
            partitaOpportunita.setIdpartita(partita);
            partitaOpportunita.setIdopportunita(opportunita);
            partitaOpportunita.setUtilizzato(false);  // Impostiamo "utilizzato" a false
            partitaOpportunita.setIdgiocatore(null);  // Impostiamo "idgiocatore" a null
            partitaOpportunitaRepository.save(partitaOpportunita);
        }
    }


    private void populateGameRoule(String gameId) {
        // Recupera tutte gli imprevisti
        List<Regolafedelta> listaRegole = regolafedeltaRepository.findAll();
        Partita partita = partitaRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException(errorePartita));

        for (Regolafedelta regolafedelta : listaRegole) {
            Partita_Regolafedelta partitaRegolafedelta = new Partita_Regolafedelta();
            partitaRegolafedelta.setIdpartita(partita);
            partitaRegolafedelta.setIdregolafedelta(regolafedelta);
            partitaRegolafedelta.setUtilizzato(false);  // Impostiamo "utilizzato" a false
            partitaRegolafedeltaRepository.save(partitaRegolafedelta);
        }
    }


    @Transactional
    public void deletePlayer(String gameId, String nomeGiocatore) {
        Integer idGiocatore = giocatoreRepository.findIdByNomeAndPartitaCodiceInvito(nomeGiocatore, gameId);

        if (idGiocatore != null) {
            giocatoreRepository.deleteByIdGiocatore(idGiocatore);
        }

    }
    public List<Integer> getUnusedPedineByPartita(String gameId) {
        return pedinaRepository.findUnusedPedineByPartita(gameId);
    }

    public List<String> getPlayersWithIdLowerThan(String gameId, String nomeGiocatore) {
        Integer idGiocatore = giocatoreRepository.findIdGiocatoreByNome(nomeGiocatore, gameId);
        if (idGiocatore != null) {
            List<String> giocatori = giocatoreRepository.findGiocatoriConIdMinore(gameId, idGiocatore);
            if (!giocatori.isEmpty())
                return giocatori;
            else return Collections.emptyList();    // Se non ci sono giocatori precedenti
        } else {
            return Collections.emptyList();  // Se il giocatore non viene trovato
        }
    }
}
