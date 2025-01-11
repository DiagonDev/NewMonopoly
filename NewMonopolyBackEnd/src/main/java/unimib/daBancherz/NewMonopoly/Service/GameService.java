package unimib.daBancherz.NewMonopoly.Service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unimib.daBancherz.NewMonopoly.Entity.*;
import unimib.daBancherz.NewMonopoly.Repository.*;

import java.util.Collections;
import java.util.List;

@Service
public class GameService {

    private final PartitaRepository partitaRepository;
    private final GiocatoreRepository giocatoreRepository;
    private final PartitaCasellaPrezzoproprietaRepository partitaCasellaPrezzoproprietaRepository;
    private final ProbabilitaRepository probabilitaRepository;
    private final PartitaProbabilitaRepository partitaProbabilitaRepository;
    private final ImprevistoRepository imprevistoRepository;
    private final PartitaImprevistoRepository partitaImprevistoRepository;


    @Autowired
    public GameService(PartitaRepository partitaRepository, GiocatoreRepository giocatoreRepository, PartitaCasellaPrezzoproprietaRepository partitaCasellaPrezzoproprietaRepository, CasellaRepository casellaRepository, ProbabilitaRepository probabilitaRepository, PartitaProbabilitaRepository partitaProbabilitaRepository, ImprevistoRepository imprevistoRepository, PartitaImprevistoRepository partitaImprevistoRepository) {
        this.partitaRepository = partitaRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.partitaCasellaPrezzoproprietaRepository = partitaCasellaPrezzoproprietaRepository;
        this.probabilitaRepository = probabilitaRepository;
        this.partitaProbabilitaRepository = partitaProbabilitaRepository;
        this.imprevistoRepository = imprevistoRepository;
        this.partitaImprevistoRepository = partitaImprevistoRepository;
    }

    //Crea la partita, aggiunge l'adim, popola partita_casella_prezzoproprietà in base alla randomizzazione
    public void createGameAndPlayer(String playerName, String difficulty, String gameId) {
        // Crea la partita
        Partita nuovaPartita = new Partita();
        nuovaPartita.setLivelloDifficolta(difficulty);
        nuovaPartita.setStato("nonIniziata");
        nuovaPartita.setRandomizzazione(false);
        nuovaPartita.setCodiceInvito(gameId);
        partitaRepository.save(nuovaPartita);       // Salva la partita nel database

        //inizializzo prezzo caselle
        if(nuovaPartita.getRandomizzazione())
            partitaCasellaPrezzoproprietaRepository.populateWithRandomizationTrue(gameId);
        else
            partitaCasellaPrezzoproprietaRepository.populateWithRandomizationFalse(gameId);

        // Crea il giocatore
        Giocatore nuovoGiocatore = new Giocatore();
        nuovoGiocatore.setIdpartita(nuovaPartita);
        nuovoGiocatore.setNome(playerName);
        nuovoGiocatore.setSaldo(1500);
        nuovoGiocatore.setTipo("admin");
        nuovoGiocatore.setPuntiFedelta(0);
        giocatoreRepository.save(nuovoGiocatore);   // Salva il giocatore nel database

        populateGameUnexpected(gameId);     //popola imprevisto
        populateGameProbability(gameId);    //popola probabilita
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


    public List<Casella> getCaselleForPartitaRandomizzate(String gameId) {
        // Ottieni tutte le caselle per la partita con il gameId, ordinate per idCasella
        List<Casella> caselle = partitaCasellaPrezzoproprietaRepository.findCaselleByPartita(gameId);

        // Separare le caselle in due gruppi (Proprietà e altro tipo)
        List<Casella> proprietaCaselle = caselle.stream()
                .filter(casella -> "Proprietà".equals(casella.getTipo()))
                .toList();

        List<Casella> altreCaselle = caselle.stream()
                .filter(casella -> !"Proprietà".equals(casella.getTipo()))
                .toList();

        // Mescolare le caselle di tipo "Proprietà" casualmente
        Collections.shuffle(proprietaCaselle);

        // Unire i due gruppi (caselle di tipo Proprietà casuali seguite da altre caselle ordinate)
        proprietaCaselle.addAll(altreCaselle);

        return proprietaCaselle;
    }

    public List<Casella >getCaselleForPartitaNonRandomizzate(String gameId){
        return partitaCasellaPrezzoproprietaRepository.findCaselleByPartita(gameId);
    }

    public void populateGameProbability(String gameId) {
        // Recupera tutte le probabilità
        List<Probabilita> listaProbabilita = probabilitaRepository.findAll();
        Partita partita = partitaRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Partita non trovata"));

        // Per ogni probabilità, crea un nuovo record in Partita_Probabilita
        for (Probabilita probabilita : listaProbabilita) {
            Partita_Probabilita partitaProbabilita = new Partita_Probabilita();
            partitaProbabilita.setIdpartita(partita);
            partitaProbabilita.setIdprobabilita(probabilita);
            partitaProbabilita.setUtilizzato(false);  // Impostiamo "utilizzato" a false
            partitaProbabilita.setIdgiocatore(null);  // Impostiamo "idgiocatore" a null
            partitaProbabilitaRepository.save(partitaProbabilita);
        }
    }

    public void populateGameUnexpected(String gameId) {
        // Recupera tutte gli imprevisti
        List<Imprevisto> listaImprevisti = imprevistoRepository.findAll();
        Partita partita = partitaRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Partita non trovata"));

        for (Imprevisto imprevisto : listaImprevisti) {
            Partita_Imprevisto partitaImprevisto = new Partita_Imprevisto();
            partitaImprevisto.setIdpartita(partita);
            partitaImprevisto.setIdimprevisto(imprevisto);
            partitaImprevisto.setUtilizzato(false);  // Impostiamo "utilizzato" a false
            partitaImprevisto.setIdgiocatore(null);  // Impostiamo "idgiocatore" a null
            partitaImprevistoRepository.save(partitaImprevisto);
        }
    }

    @Transactional
    public void deletePlayer(String gameId, String nomeGiocatore){
        Integer idGiocatore = giocatoreRepository.findIdByNomeAndPartitaCodiceInvito(nomeGiocatore, gameId);
        giocatoreRepository.deleteByIdGiocatore(idGiocatore);
    }
}
