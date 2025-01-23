package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
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
    private final MessageHandler messageHandler;
    private final CasellaRepository casellaRepository;

    public PropertyHandler(PartitaCasellaPrezzoproprietaRepository pCPPRepository, GiocatoreRepository giocatoreRepository, GameHandler gameHandler, MessageHandler messageHandler, CasellaRepository casellaRepository) {
        this.pCPPRepository = pCPPRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.gameHandler = gameHandler;
        this.messageHandler = messageHandler;
        this.casellaRepository = casellaRepository;
    }

    public void acquistaProprieta(String[] messageParts, WebSocketSession session) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        Map<String, List<WebSocketSession>> gameSessions = gameHandler.getGameSessions();
        String content;

        int prezzoCasella = pCPPRepository.prezzoCasella2(messageParts[1], gameId);
        int saldoGiocatore = giocatoreRepository.saldoGiocatore(playerName, gameId);
        if(saldoGiocatore > prezzoCasella){
            pCPPRepository.setProprietario(playerName, gameId, messageParts[1]);
            giocatoreRepository.setSaldoGiocatore(playerName, gameId, prezzoCasella);
            messageHandler.updateBalance(gameHandler.getGameSessions(), gameId, playerName);
            String proprietaMessage = new ObjectMapper().writeValueAsString(Map.of(
                    "type", "acquistoRiuscito"
            ));
            session.sendMessage(new TextMessage(proprietaMessage));
            content = playerName + " ha acquistato: " + messageParts[1];
            messageHandler.sendSystemMessage(gameId, content, gameSessions, session);
        }else{
            String proprietaMessage = new ObjectMapper().writeValueAsString(Map.of(
                    "type", "acquistoFallito"
            ));
            session.sendMessage(new TextMessage(proprietaMessage));
        }
    }

    public void gestisciProprieta(WebSocketSession session) throws IOException {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        List<PlayerProperties> playerPropertiesList = pCPPRepository.findOtherPlayerProperties(gameId,playerName);

        String playerPropertiesListMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "allProperties",
                "properties", playerPropertiesList
        ));
        session.sendMessage(new TextMessage(playerPropertiesListMessage));
    }

    public void gestisciCase(PlayerProperties property, Integer casine,  WebSocketSession session) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        Integer idGiocatore = property.getIdGiocatore();
        String coloreCasella = property.getColore();
        int numCase = property.getNumCasa();
        int costoCasa = property.getPrezzoCasaCorrente();
        String nomeGiocatore = giocatoreRepository.findNomeByidGiocatore(idGiocatore);
        int saldoGiocatore = giocatoreRepository.saldoGiocatore(nomeGiocatore, gameId);
        int countColore = casellaRepository.countByColore(coloreCasella);
        Map<String, List<WebSocketSession>> gameSessions = gameHandler.getGameSessions();
        String content;

        if(numCase<5 && saldoGiocatore > (casine * countColore* costoCasa)){
            giocatoreRepository.setSaldoGiocatore(nomeGiocatore, gameId, (casine * countColore * costoCasa));
            messageHandler.updateBalance(gameHandler.getGameSessions(), gameId, playerName);
            pCPPRepository.aggiungiCase(coloreCasella, casine);
            content = playerName + " ha costruito " + casine + "case sulle proprietà di colore " + coloreCasella;
            messageHandler.sendSystemMessage(gameId, content, gameSessions, session);
            // Messaggio aggiornamento
        } else {
            // Messaggio fallito
        }
    }

    public void effettuaScambio(PlayerProperties property1, PlayerProperties property2, Integer money,  WebSocketSession session) throws Exception {
        String nomeRichiedente = giocatoreRepository.findNomeByidGiocatore(property1.getIdGiocatore());
        String nomeProprietario  = giocatoreRepository.findNomeByidGiocatore(property2.getIdGiocatore());
        String gameId = gameHandler.getGameIdBySession(session);
        WebSocketSession session2 = gameHandler.getSessionByPlayerName(nomeProprietario, gameId);
        Map<String, List<WebSocketSession>> gameSessions = gameHandler.getGameSessions();

        String casaMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "exchangeRequest",
                "property1", property1,
                "property2", property2,
                "money", money,
                "playerName", nomeRichiedente
        ));
        session2.sendMessage(new TextMessage(casaMessage));
        String content = nomeRichiedente + " ha chiesto uno scambio a " + nomeProprietario + ". " + property1 + " per: " + property2;
        messageHandler.sendSystemMessage(gameId, content, gameSessions, session);
    }

    public void ipotecaProprieta(PlayerProperties property, WebSocketSession session) throws Exception {
        String nomeCasella = property.getNome();
        String gameId= gameHandler.getGameIdBySession(session);
        String nomeGiocatore = gameHandler.getPlayerNameBySession(session);
        Integer prezzo = property.getPrezzoCorrente()/2;
        Integer posizione = pCPPRepository.findPosizioneByNomeCasellaAndIdpartita(nomeCasella, gameId);
        Map<String, List<WebSocketSession>> gameSessions = gameHandler.getGameSessions();
        List<PlayerProperties> giocatorePropertiesList;

        pCPPRepository.setPrezzoCorrente(prezzo, gameId, posizione);
        giocatoreRepository.setSaldoGiocatore(nomeGiocatore, gameId, - (prezzo) );
        messageHandler.updateBalance(gameHandler.getGameSessions(), gameId, nomeGiocatore);
        pCPPRepository.setProprietario(null, gameId, nomeCasella);
        messageHandler.rispostaAggiornaProprieta(gameId, nomeGiocatore, session);//Messaggio al frontend
        messageHandler.sendSystemMessage(gameId, nomeGiocatore + " ha ipotecato: " + nomeCasella, gameSessions, session);
        giocatorePropertiesList = pCPPRepository.findPlayerProperties(gameId,nomeGiocatore);

        String updateGiocatorePropertiesMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "updateProperties",
                "properties", giocatorePropertiesList
        ));
        session.sendMessage(new TextMessage(updateGiocatorePropertiesMessage));
    }

    public void rispostaScambio(PlayerProperties property1, PlayerProperties property2, Integer offertaMonetaria, boolean flag, WebSocketSession session) throws Exception {
        String gameId= gameHandler.getGameIdBySession(session);
        Integer idProprietario2 = property2.getIdGiocatore();
        Integer idProprietario1 = property1.getIdGiocatore();
        String nomeProprietario = giocatoreRepository.findNomeByidGiocatore(idProprietario2);
        String nomeRichiedente = giocatoreRepository.findNomeByidGiocatore(idProprietario1);
        WebSocketSession sessionRichiedente = gameHandler.getSessionByPlayerName(nomeRichiedente, gameId);
        List<PlayerProperties> proprietarioPropertiesList;
        List<PlayerProperties> richiedentePropertiesList;

        Map<String, List<WebSocketSession>> gameSessions = gameHandler.getGameSessions();
        String content;
        if(flag){
            // se offerta < 0 toglie i soldi al proprietario
            // se offerta > 0 toglie i soldi al richiedente

            Integer soldi = giocatoreRepository.saldoGiocatore(nomeRichiedente, gameId);
            if (soldi>offertaMonetaria) {
                giocatoreRepository.setSaldoGiocatore(nomeRichiedente, gameId, offertaMonetaria);
                giocatoreRepository.setSaldoGiocatore(nomeProprietario, gameId, -offertaMonetaria);
                messageHandler.updateBalance(gameHandler.getGameSessions(), gameId, nomeRichiedente);
                messageHandler.updateBalance(gameHandler.getGameSessions(), gameId, nomeProprietario);
                pCPPRepository.setProprietario(nomeRichiedente, gameId, property2.getNome());
                pCPPRepository.setProprietario(nomeProprietario, gameId, property1.getNome());
                messageHandler.rispostaGestisciProprieta("Scambio accettato", session);
                content= nomeProprietario + " ha accettato lo scambio di " + nomeRichiedente + ". " + property2 + " per: " + property1;
                messageHandler.sendSystemMessage(gameId, content, gameSessions, session);

                proprietarioPropertiesList = pCPPRepository.findPlayerProperties(gameId,nomeProprietario);
                richiedentePropertiesList = pCPPRepository.findPlayerProperties(gameId,nomeRichiedente);
                String updateProprietarioPropertiesMessage = new ObjectMapper().writeValueAsString(Map.of(
                        "type", "updateProperties",
                        "properties", proprietarioPropertiesList
                ));
                session.sendMessage(new TextMessage(updateProprietarioPropertiesMessage));

                String updateRichiedentePropertiesMessage = new ObjectMapper().writeValueAsString(Map.of(
                        "type", "updateProperties",
                        "properties", richiedentePropertiesList
                ));
                sessionRichiedente.sendMessage(new TextMessage(updateRichiedentePropertiesMessage));
            }
            else {
                // Messaggio per dire che sei povero
                return;
            }
        } else {
            messageHandler.rispostaGestisciProprieta("Scambio declinato", session);
            content = nomeProprietario + " ha rifiutato lo scambio di " + nomeRichiedente;
            messageHandler.sendSystemMessage(gameId, content, gameSessions, session);
        }
    }

    public void updateProperties(WebSocketSession session) throws IOException {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);

        List<PlayerProperties> updatePropertiesList = pCPPRepository.findPlayerProperties(gameId,playerName);
        String updatePropertiesMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "updateProperties",
                "properties", updatePropertiesList
        ));
        session.sendMessage(new TextMessage(updatePropertiesMessage));

    }
}
