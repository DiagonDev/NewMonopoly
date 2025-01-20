package unimib.daBancherz.NewMonopoly.Handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.GiocatoreRepository;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.PartitaCasellaPrezzoproprietaRepository;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.PartitaProbabilitaRepository;
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
    GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();

    public PropertyHandler(PartitaCasellaPrezzoproprietaRepository pCPPRepository, GiocatoreRepository giocatoreRepository, GameHandler gameHandler, MessageHandler messageHandler) {
        this.pCPPRepository = pCPPRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.gameHandler = gameHandler;
        this.messageHandler = messageHandler;
    }

    public void acquistaProprieta(String[] messageParts, WebSocketSession session) throws Exception {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);

        int prezzoCasella = pCPPRepository.prezzoCasella2(messageParts[1], gameId);
        int saldoGiocatore = giocatoreRepository.saldoGiocatore(playerName, gameId);
        if(saldoGiocatore > prezzoCasella){
            pCPPRepository.setGiocatore(playerName, gameId, messageParts[1]);
            giocatoreRepository.aggiornamentoSaldo(playerName, gameId, prezzoCasella);
            messageHandler.updateBalance(gameHandler.getGameSessions(), gameId);
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
        if (useCase.equals("GestisciProprieta")){
            playerPropertiesList = pCPPRepository.findPlayerProperties(gameId,playerName);
        } else if(useCase.equals("PingScambiaProprieta")){
            playerPropertiesList = pCPPRepository.findOtherPlayerProperties(gameId,playerName);
        }
        String playerPropertiesListMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "propertiesOwned",
                "properties", playerPropertiesList
        ));
        session.sendMessage(new TextMessage(playerPropertiesListMessage));
    }

    public void gestisciCase(String nomeGiocatore, String idPartita, Integer posizione, WebSocketSession session) throws IOException {
        int saldoGiocatore = giocatoreRepository.saldoGiocatore(nomeGiocatore, idPartita);
        int costoCasa = pCPPRepository.prezzoCasa(posizione, idPartita);
        int numCase = pCPPRepository.contaCase(posizione);
        if (saldoGiocatore > costoCasa && numCase < 4) {
            giocatoreRepository.aggiornamentoSaldo(nomeGiocatore, idPartita, costoCasa);
            pCPPRepository.compraCasa(posizione);
            String casaMessage = new ObjectMapper().writeValueAsString(Map.of(
                    "type", "acquistoRiuscito"
            ));
            session.sendMessage(new TextMessage(casaMessage));
        } else {
            String casaMessage = new ObjectMapper().writeValueAsString(Map.of(
                    "type", "acquistoFallito"
            ));
            session.sendMessage(new TextMessage(casaMessage));
        }
    }

    public void effettuaScambio(PlayerProperties property1, PlayerProperties property2, Integer money,  WebSocketSession session) throws IOException {
        Integer idProprietario2 = property2.getIdGiocatore();
        Integer idProprietario1 = property1.getIdGiocatore();
        List<String> proprietario = giocatoreRepository.findNomeAndIdpartitaByidGiocatore(idProprietario2);
        List<String> richiedente = giocatoreRepository.findNomeAndIdpartitaByidGiocatore(idProprietario1);
        gameHandler.getSessionByPlayerName(proprietario.get(0), proprietario.get(1));
        String casaMessage = new ObjectMapper().writeValueAsString(Map.of(
                "type", "exchangeRequest",
                "property1", property1,
                "property2", property2,
                "money", money,
                "playerName", richiedente.get(0)
        ));
        session.sendMessage(new TextMessage(casaMessage));
    }

    public void costruisciCase(String[] parts, WebSocketSession session) {
    }

    public void ipotecaProprieta(String[] parts, WebSocketSession session) {

    }

    public void rispostaScambio(PlayerProperties property1, PlayerProperties property2, Integer offertaMonetaria, boolean flag, WebSocketSession session) throws IOException {
        if(flag){
            Integer idProprietario2 = property2.getIdGiocatore();
            Integer idProprietario1 = property1.getIdGiocatore();
            String nomeCasella1 = property1.getNome();
            String nomeCasella2 = property2.getNome();
            List<String> proprietario = giocatoreRepository.findNomeAndIdpartitaByidGiocatore(idProprietario2);
            List<String> richiedente = giocatoreRepository.findNomeAndIdpartitaByidGiocatore(idProprietario1);

            Integer soldi;
            // se offerta > 0 toglie i soldi al richiedente
            // se offerta < 0 toglie i soldi al proprietario
            if(offertaMonetaria>0){
                soldi = giocatoreRepository.saldoGiocatore(richiedente.get(0), richiedente.get(1));
                if (soldi>offertaMonetaria)
                    giocatoreRepository.aggiornamentoSaldo(richiedente.get(0), richiedente.get(1), offertaMonetaria);
                else {
                    // Messaggio per dire che sei povero
                    return;
                }
            } else {
                offertaMonetaria = - offertaMonetaria;
                soldi = giocatoreRepository.saldoGiocatore(proprietario.get(0), proprietario.get(1));
                if (soldi>offertaMonetaria)
                    giocatoreRepository.aggiornamentoSaldo(proprietario.get(0), proprietario.get(1), offertaMonetaria);
                else {
                    // Messaggio per dire che sei povero
                    return;
                }
            }
            pCPPRepository.setGiocatore(richiedente.get(0), richiedente.get(1), nomeCasella2);
            pCPPRepository.setGiocatore(proprietario.get(0), proprietario.get(1), nomeCasella1);

            messageHandler.rispostaGestisciProprieta("Scambio accettato", session);
        } else {
            messageHandler.rispostaGestisciProprieta("Scambio declinato", session);
        }
    }
}
