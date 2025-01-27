package unimib.dabancherz.newmonopoly.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import unimib.dabancherz.newmonopoly.MessageService;
import unimib.dabancherz.newmonopoly.database.entity.Giocatore;
import unimib.dabancherz.newmonopoly.database.entity.Partita;
import unimib.dabancherz.newmonopoly.database.repository.*;
import unimib.dabancherz.newmonopoly.model.PlayerProperties;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class PropertyHandler {
    private final PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    private final GiocatoreRepository giocatoreRepository;
    private final CasellaRepository casellaRepository;
    private final PartitaRepository partitaRepository;
    private final GameHandler gameHandler;
    private final MessageService messageService;
    ObjectMapper objectMapper = new ObjectMapper();

    private static final String PROPERTIESKEY = "properties";
    private static final String UPDATEPROPERTIES_KEY = "updateProperties";
    private static final String IMPRENDITOREKEY = "imprenditore";

    public PropertyHandler(PartitaCasellaPrezzoproprietaRepository pCPPRepository, GiocatoreRepository giocatoreRepository, GameHandler gameHandler, MessageService messageService, CasellaRepository casellaRepository, PartitaRepository partitaRepository) {
        this.pCPPRepository = pCPPRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.gameHandler = gameHandler;
        this.messageService = messageService;
        this.casellaRepository = casellaRepository;
        this.partitaRepository = partitaRepository;
    }

    public void acquistaProprieta(String[] messageParts, WebSocketSession session) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);

        int prezzoCasella = pCPPRepository.prezzoCasella2(messageParts[1], gameId);
        int saldoGiocatore = giocatoreRepository.saldoGiocatore(playerName, gameId);

        if(saldoGiocatore > prezzoCasella) {
            giocatoreRepository.setSaldoGiocatore(playerName, gameId, prezzoCasella);
            completaAcquistoProprieta(gameId, playerName, messageParts[1], session);
        }else
            messageService.inviaMessaggio(session, "acquistoFallito");
    }

    public void acquistaProprietaPunti(String[] messageParts, WebSocketSession session) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);

        int corrispondenzaPunti = numPuntiFedelta(playerName, gameId);
        int prezzoCasella = pCPPRepository.prezzoCasella2(messageParts[1], gameId);
        Giocatore giocatore = giocatoreRepository.findGiocatoreByIdpartita_CodiceInvitoAndNome(gameId, playerName);
        int puntiFedelta = giocatore.getPuntiFedelta();
        if(puntiFedelta != 0) {
            if (puntiFedelta >= prezzoCasella * corrispondenzaPunti)     //controlla se ha abbastanza punti fedeltà
                giocatoreRepository.setPuntiGiocatore(playerName, gameId, prezzoCasella * corrispondenzaPunti);
            else {                                                       // fa la combinazione di soldi e punti fedeltà
                int saldoGiocatore = giocatore.getSaldo();
                int prezzo = (prezzoCasella * corrispondenzaPunti - puntiFedelta) / corrispondenzaPunti;
                if (saldoGiocatore >= prezzo) {                    //controlla se ha abbastanza soldi
                    giocatoreRepository.setSaldoGiocatore(playerName, gameId, prezzo);
                    giocatoreRepository.setPuntiGiocatore(playerName, gameId, puntiFedelta);
                } else {
                    messageService.inviaMessaggio(session, "acquistoFallito");
                    return;
                }
            }
            completaAcquistoProprieta(gameId, playerName, messageParts[1], session);
        } else acquistaProprieta(messageParts, session);
    }

    public void completaAcquistoProprieta(String gameId, String playerName, String nomeProprieta, WebSocketSession session) throws Exception {
        pCPPRepository.setProprietario(playerName, gameId, nomeProprieta);
        messageService.updateBalance(gameHandler.getGameSessions(), gameId, playerName);

        messageService.inviaMessaggio(session, "acquistoRiuscito");
        String content = playerName + " ha acquistato: " + nomeProprieta;
        messageService.sendSystemMessage(gameId, content, gameHandler.getGameSessions(), session);
    }

    public void gestisciProprieta(WebSocketSession session) throws IOException {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        List<PlayerProperties> playerPropertiesList = pCPPRepository.findOtherPlayerProperties(gameId,playerName);

        messageService.inviaMessaggio(session, "allProperties", PROPERTIESKEY, playerPropertiesList);
    }

    public void gestisciCase(PlayerProperties property, Integer casine,  WebSocketSession session) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        String coloreCasella = property.getColore();
        int numCase = property.getNumCasa();
        int costoCasa = property.getPrezzoCasaCorrente();
        int saldoGiocatore = giocatoreRepository.saldoGiocatore(playerName, gameId);
        int countColore = casellaRepository.countByColore(coloreCasella);
        int countColoreGiocatore = pCPPRepository.countProprietaColore(playerName, coloreCasella, gameId);

        if(numCase<5 && saldoGiocatore > (casine * countColore* costoCasa) && countColore == countColoreGiocatore)
            completaCostruzioneCase(gameId, playerName, coloreCasella, casine, costoCasa, countColore, session);
        else
            messageService.inviaMessaggio(session, "costruzioneFallita");
    }

    public void completaCostruzioneCase(String gameId, String playerName, String coloreCasella, int casine, int costoCasa, int countColore, WebSocketSession session) throws Exception {
        giocatoreRepository.setSaldoGiocatore(playerName, gameId, (casine * countColore * costoCasa));
        messageService.updateBalance(gameHandler.getGameSessions(), gameId, playerName);
        pCPPRepository.aggiungiCase(coloreCasella, casine);

        String content = playerName + " ha costruito " + casine + " case sulle proprietà di colore " + coloreCasella;
        messageService.sendSystemMessage(gameId, content, gameHandler.getGameSessions(), session);
    }


    public void effettuaScambio(Map<String, Object> data, WebSocketSession session) throws Exception {
        PlayerProperties property1 = property(data, "property1");
        PlayerProperties property2 = property(data, "property2");
        Integer offertaMonetaria= offertaMonetaria(data);

        String nomeRichiedente = giocatoreRepository.findNomeByidGiocatore(property1.getIdGiocatore());
        String nomeProprietario  = giocatoreRepository.findNomeByidGiocatore(property2.getIdGiocatore());
        String gameId = gameHandler.getGameIdBySession(session);
        WebSocketSession session2 = gameHandler.getSessionByPlayerName(nomeProprietario, gameId);

        messageService.exchangeRequestMessage(nomeRichiedente, property1, property2, offertaMonetaria, session2);
        String content = nomeRichiedente + " ha chiesto uno scambio a " + nomeProprietario + ". " + property1.getNome() + " per: " + property2.getNome();
        messageService.sendSystemMessage(gameId, content, gameHandler.getGameSessions(), session);
    }

    public void rispostaScambio(Map<String, Object> data, boolean flag, WebSocketSession session) throws Exception {
        PlayerProperties property1 = property(data, "property1");
        PlayerProperties property2 = property(data, "property2");
        Integer offertaMonetaria= offertaMonetaria(data);

        String gameId = gameHandler.getGameIdBySession(session);
        String nomeProprietario = giocatoreRepository.findNomeByidGiocatore(property2.getIdGiocatore());
        String nomeRichiedente = giocatoreRepository.findNomeByidGiocatore(property1.getIdGiocatore());
        WebSocketSession sessionRichiedente = gameHandler.getSessionByPlayerName(nomeRichiedente, gameId);

        if (flag) {
            completaScambio(gameId, nomeProprietario, nomeRichiedente, property1, property2, offertaMonetaria, session, sessionRichiedente);
        } else {
            rifiutaScambio(gameId, nomeProprietario, nomeRichiedente, session);
        }
    }

    public void ipotecaProprieta(PlayerProperties property, WebSocketSession session) throws Exception {
        String nomeCasella = property.getNome();
        String gameId= gameHandler.getGameIdBySession(session);
        String nomeGiocatore = gameHandler.getPlayerNameBySession(session);
        Integer prezzo = property.getPrezzoCorrente()/2;
        Integer posizione = pCPPRepository.findPosizioneByNomeCasellaAndIdpartita(nomeCasella, gameId);

        pCPPRepository.setPrezzoCorrente(prezzo, gameId, posizione);
        giocatoreRepository.setSaldoGiocatore(nomeGiocatore, gameId, - (prezzo) );
        messageService.updateBalance(gameHandler.getGameSessions(), gameId, nomeGiocatore);
        pCPPRepository.setProprietario(null, gameId, nomeCasella);

        aggiornaProprieta(gameId, nomeGiocatore, session);
        String content = nomeGiocatore + " ha ipotecato: " + nomeCasella;
        messageService.sendSystemMessage(gameId, content, gameHandler.getGameSessions(), session);
    }

    public void aggiornaProprieta(String gameId, String nomeGiocatore, WebSocketSession session) throws IOException {
        List<PlayerProperties> giocatorePropertiesList = pCPPRepository.findPlayerProperties(gameId, nomeGiocatore);
        messageService.inviaMessaggio(session, UPDATEPROPERTIES_KEY, PROPERTIESKEY, giocatorePropertiesList);
    }

    public void completaScambio(String gameId, String nomeProprietario, String nomeRichiedente, PlayerProperties property1, PlayerProperties property2, Integer offertaMonetaria, WebSocketSession session, WebSocketSession sessionRichiedente) throws Exception {
        int saldoRichiedente = giocatoreRepository.saldoGiocatore(nomeRichiedente, gameId);

        if (saldoRichiedente > offertaMonetaria) {
            giocatoreRepository.setSaldoGiocatore(nomeRichiedente, gameId, offertaMonetaria);
            giocatoreRepository.setSaldoGiocatore(nomeProprietario, gameId, -offertaMonetaria);
            messageService.updateBalance(gameHandler.getGameSessions(), gameId, nomeRichiedente);
            messageService.updateBalance(gameHandler.getGameSessions(), gameId, nomeProprietario);
            pCPPRepository.setProprietario(nomeRichiedente, gameId, property2.getNome());
            pCPPRepository.setProprietario(nomeProprietario, gameId, property1.getNome());

            messageService.rispostaGestisciProprieta("Scambio accettato", session);
            String content = nomeProprietario + " ha accettato lo scambio di " + nomeRichiedente + ". ";
            messageService.sendSystemMessage(gameId, content, gameHandler.getGameSessions(), session);

            aggiornaProprietaScambio(gameId, nomeProprietario, nomeRichiedente, session, sessionRichiedente);
        } else {
            messageService.inviaMessaggio(session, "scambioFallito", "reason", "Saldo insufficiente");
        }
    }

    public void aggiornaProprietaScambio(String gameId, String nomeProprietario, String nomeRichiedente, WebSocketSession session, WebSocketSession sessionRichiedente) throws IOException {
        List<PlayerProperties> proprietarioPropertiesList = pCPPRepository.findPlayerProperties(gameId, nomeProprietario);
        messageService.inviaMessaggio(session, UPDATEPROPERTIES_KEY, PROPERTIESKEY, proprietarioPropertiesList);

        List<PlayerProperties> richiedentePropertiesList = pCPPRepository.findPlayerProperties(gameId, nomeRichiedente);
        messageService.inviaMessaggio(sessionRichiedente, UPDATEPROPERTIES_KEY, PROPERTIESKEY, richiedentePropertiesList);
    }

    public void rifiutaScambio(String gameId, String nomeProprietario, String nomeRichiedente, WebSocketSession session) throws Exception {
        messageService.rispostaGestisciProprieta("Scambio declinato", session);
        String content = nomeProprietario + " ha rifiutato lo scambio di " + nomeRichiedente;
        messageService.sendSystemMessage(gameId, content, gameHandler.getGameSessions(), session);
    }

    public void updateProperties(WebSocketSession session) throws IOException {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);

        aggiornaProprieta(gameId, playerName, session);
    }

    public PlayerProperties property(Map<String, Object> data, String numberProperty){
        return objectMapper.convertValue(data.get(numberProperty), PlayerProperties.class);
    }

    public Integer offertaMonetaria(Map<String, Object> data){
        int offertaMonetaria;
        Object offertaMonetariaObj = data.get("offertaMonetaria");
        // Gestione sicura di offertaMonetaria
        if (offertaMonetariaObj instanceof String) {
            offertaMonetaria = Integer.parseInt((String) offertaMonetariaObj); // Converti da stringa
        } else if (offertaMonetariaObj instanceof Integer) {
            offertaMonetaria = (Integer) offertaMonetariaObj; // Già un Integer, usa direttamente
        } else {
            throw new IllegalArgumentException("Tipo non valido per offertaMonetaria: " + offertaMonetariaObj.getClass());
        }
        return offertaMonetaria;
    }

    //Restituisce quanto corrisponde 1€ in punti fedeltà in base al tipo di giocatore e al livello della partita
    public Integer numPuntiFedelta(String playerName, String gameId){
        Partita partita = partitaRepository.findByCodiceInvito(gameId);
        Giocatore giocatore = giocatoreRepository.findGiocatoreByIdpartita_CodiceInvitoAndNome(gameId, playerName);
        String livello = partita.getLivelloDifficolta();
        switch (livello){
            case "Facile":
                if (giocatore.getTipo().equals(IMPRENDITOREKEY))
                    return 4;
                else return 2;
            case "Medio":
                if (giocatore.getTipo().equals(IMPRENDITOREKEY))
                    return 8;
                else return 4;
            case "Difficile":
                if (giocatore.getTipo().equals(IMPRENDITOREKEY))
                    return 16;
                else return 8;
            default:
                throw new IllegalArgumentException("Tipo di messaggio non supportato");
        }
    }
}