package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.*;
import unimib.daBancherz.NewMonopoly.model.PlayerProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class PropertyHandler {
    private final PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    private final GiocatoreRepository giocatoreRepository;
    private final GameHandler gameHandler;
    private final MessageHandler messageHandler;
    private final CasellaRepository casellaRepository;
    private final PrezzoproprietaRepository prezzoproprietaRepository;
    GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();

    public PropertyHandler(PartitaCasellaPrezzoproprietaRepository pCPPRepository, GiocatoreRepository giocatoreRepository, GameHandler gameHandler, MessageHandler messageHandler, CasellaRepository casellaRepository, PrezzoproprietaRepository prezzoproprietaRepository) {
        this.pCPPRepository = pCPPRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.gameHandler = gameHandler;
        this.messageHandler = messageHandler;
        this.casellaRepository = casellaRepository;
        this.prezzoproprietaRepository = prezzoproprietaRepository;
    }

    public void acquistaProprieta(String[] messageParts, WebSocketSession session) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);

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
        }else{
            String proprietaMessage = new ObjectMapper().writeValueAsString(Map.of(
                    "type", "acquistoFallito"
            ));
            session.sendMessage(new TextMessage(proprietaMessage));
        }
    }

    public void gestisciProprieta(WebSocketSession session, String useCase) throws IOException {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        List<PlayerProperties> playerPropertiesList = new ArrayList<>();
        String type = "";
        if (useCase.equals("GestisciProprieta")){
            type = "propertiesOwned";
            playerPropertiesList = pCPPRepository.findPlayerProperties(gameId,playerName);
        } else if(useCase.equals("PingScambiaProprieta")){
            type = "allProperties";
            playerPropertiesList = pCPPRepository.findOtherPlayerProperties(gameId,playerName);
        }
        String playerPropertiesListMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", type,
                "properties", playerPropertiesList
        ));
        session.sendMessage(new TextMessage(playerPropertiesListMessage));
    }

    public void gestisciCase(PlayerProperties property, Integer casine,  WebSocketSession session) {
        String gameId = gameHandler.getGameIdBySession(session);
        Integer idGiocatore = property.getIdGiocatore();
        String coloreCasella = property.getColore();
        int numCase = property.getNumCasa();
        int costoCasa = property.getPrezzoCasaCorrente();
        String nomeGiocatore = giocatoreRepository.findNomeByidGiocatore(idGiocatore);
        int saldoGiocatore = giocatoreRepository.saldoGiocatore(nomeGiocatore, gameId);
        int countColore = casellaRepository.countByColore(coloreCasella);
        if(numCase<5 && saldoGiocatore > (casine * countColore* costoCasa)){
            giocatoreRepository.setSaldoGiocatore(nomeGiocatore, gameId, (casine * countColore * costoCasa));
            pCPPRepository.aggiungiCase(coloreCasella, casine);
            // Messaggio aggiornamento
        } else {
            // Messaggio fallito
        }
    }

    public void effettuaScambio(PlayerProperties property1, PlayerProperties property2, Integer money,  WebSocketSession session) throws IOException {
        String nomeRichiedente = giocatoreRepository.findNomeByidGiocatore(property1.getIdGiocatore());
        String nomeProprietario  = giocatoreRepository.findNomeByidGiocatore(property2.getIdGiocatore());
        String gameId = gameHandler.getGameIdBySession(session);
        WebSocketSession session2 = gameHandler.getSessionByPlayerName(nomeProprietario, gameId);
        String casaMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "exchangeRequest",
                "property1", property1,
                "property2", property2,
                "money", money,
                "playerName", nomeRichiedente
        ));
        session2.sendMessage(new TextMessage(casaMessage));
    }

    public void ipotecaProprieta(PlayerProperties property, WebSocketSession session) throws IOException {
        String nomeCasella = property.getNome();
        String gameId= gameHandler.getGameIdBySession(session);
        String nomeGiocatore = gameHandler.getPlayerNameBySession(session);
        Integer prezzo = property.getPrezzoCorrente()/2;
        Integer posizione = pCPPRepository.findPosizioneByNomeCasellaAndIdpartita(nomeCasella, gameId);
        pCPPRepository.setPrezzoCorrente(prezzo, gameId, posizione);
        giocatoreRepository.setSaldoGiocatore(nomeGiocatore, gameId, - (prezzo) );
        pCPPRepository.setProprietario(null, gameId, nomeCasella);
        messageHandler.rispostaAggiornaProprieta(gameId, nomeGiocatore, session);   //Messaggio al frontend
    }

    public void rispostaScambio(PlayerProperties property1, PlayerProperties property2, Integer offertaMonetaria, boolean flag, WebSocketSession session) throws IOException {
        if(flag){
            Integer idProprietario2 = property2.getIdGiocatore();
            Integer idProprietario1 = property1.getIdGiocatore();
            String nomeCasella1 = property1.getNome();
            String nomeCasella2 = property2.getNome();
            String nomeProprietario = giocatoreRepository.findNomeByidGiocatore(idProprietario2);
            String nomeRichiedente = giocatoreRepository.findNomeByidGiocatore(idProprietario1);
            String gameId= gameHandler.getGameIdBySession(session);

            Integer soldi;
            // se offerta > 0 toglie i soldi al richiedente
            // se offerta < 0 toglie i soldi al proprietario
            if(offertaMonetaria>0){
                soldi = giocatoreRepository.saldoGiocatore(nomeRichiedente, gameId);
                if (soldi>offertaMonetaria)
                    giocatoreRepository.setSaldoGiocatore(nomeRichiedente, gameId, offertaMonetaria);
                else {
                    // Messaggio per dire che sei povero
                    return;
                }
            } else {
                offertaMonetaria = - offertaMonetaria;
                soldi = giocatoreRepository.saldoGiocatore(nomeProprietario, gameId);
                if (soldi>offertaMonetaria)
                    giocatoreRepository.setSaldoGiocatore(nomeProprietario, gameId, offertaMonetaria);
                else {
                    // Messaggio per dire che sei povero
                    return;
                }
            }
            pCPPRepository.setProprietario(nomeRichiedente, gameId, nomeCasella2);
            pCPPRepository.setProprietario(nomeProprietario, gameId, nomeCasella1);

            messageHandler.rispostaGestisciProprieta("Scambio accettato", session);
        } else {
            messageHandler.rispostaGestisciProprieta("Scambio declinato", session);
        }
    }
}
