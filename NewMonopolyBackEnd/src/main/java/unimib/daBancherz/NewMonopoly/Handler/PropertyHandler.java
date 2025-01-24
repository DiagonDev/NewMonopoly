package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.MessageService;
import unimib.daBancherz.NewMonopoly.database.Repository.*;
import unimib.daBancherz.NewMonopoly.model.PlayerProperties;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class PropertyHandler {
    private final PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    private final GiocatoreRepository giocatoreRepository;
    private final GameHandler gameHandler;
    private final MessageService messageService;
    private final CasellaRepository casellaRepository;
    ObjectMapper objectMapper = new ObjectMapper();

    public PropertyHandler(PartitaCasellaPrezzoproprietaRepository pCPPRepository, GiocatoreRepository giocatoreRepository, GameHandler gameHandler, MessageService messageService, CasellaRepository casellaRepository) {
        this.pCPPRepository = pCPPRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.gameHandler = gameHandler;
        this.messageService = messageService;
        this.casellaRepository = casellaRepository;
    }

    public void acquistaProprieta(String[] messageParts, WebSocketSession session) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);

        int prezzoCasella = pCPPRepository.prezzoCasella2(messageParts[1], gameId);
        int saldoGiocatore = giocatoreRepository.saldoGiocatore(playerName, gameId);

        if(saldoGiocatore > prezzoCasella)
            completaAcquistoProprieta(gameId, playerName, messageParts[1], prezzoCasella, session);
        else
            messageService.inviaMessaggio(session, "acquistoFallito");
    }

    public void completaAcquistoProprieta(String gameId, String playerName, String nomeProprieta, int prezzo, WebSocketSession session) throws Exception {
        pCPPRepository.setProprietario(playerName, gameId, nomeProprieta);
        giocatoreRepository.setSaldoGiocatore(playerName, gameId, prezzo);
        messageService.updateBalance(gameHandler.getGameSessions(), gameId, playerName);

        messageService.inviaMessaggio(session, "acquistoRiuscito");
        String content = playerName + " ha acquistato: " + nomeProprieta;
        messageService.sendSystemMessage(gameId, content, gameHandler.getGameSessions(), session);
    }

    public void gestisciProprieta(WebSocketSession session) throws IOException {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        List<PlayerProperties> playerPropertiesList = pCPPRepository.findOtherPlayerProperties(gameId,playerName);

        messageService.inviaMessaggio(session, "allProperties", "properties", playerPropertiesList);
    }

    public void gestisciCase(PlayerProperties property, Integer casine,  WebSocketSession session) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        String coloreCasella = property.getColore();
        int numCase = property.getNumCasa();
        int costoCasa = property.getPrezzoCasaCorrente();
        int saldoGiocatore = giocatoreRepository.saldoGiocatore(playerName, gameId);
        int countColore = casellaRepository.countByColore(coloreCasella);

        if(numCase<5 && saldoGiocatore > (casine * countColore* costoCasa))
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
        messageService.inviaMessaggio(session, "updateProperties", "properties", giocatorePropertiesList);
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
        messageService.inviaMessaggio(session, "updateProperties", "properties", proprietarioPropertiesList);

        List<PlayerProperties> richiedentePropertiesList = pCPPRepository.findPlayerProperties(gameId, nomeRichiedente);
        messageService.inviaMessaggio(sessionRichiedente, "updateProperties", "properties", richiedentePropertiesList);
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
        Integer offertaMonetaria;
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
}
