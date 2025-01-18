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

import java.io.IOException;
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

    public void gestisciProprieta(String[] messageParts, WebSocketSession session) throws IOException {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        
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

    public void gestisciAlbergo(String nomeGiocatore, String idPartita, Integer posizione, WebSocketSession session) throws IOException {
        int saldoGiocatore = giocatoreRepository.saldoGiocatore(nomeGiocatore, idPartita);
        int costoAlbergo = pCPPRepository.prezzoCasa(posizione, idPartita);
        Boolean numAlbergo = pCPPRepository.contaAlbergo(posizione);
        if (saldoGiocatore > costoAlbergo && !(numAlbergo)) {
            giocatoreRepository.aggiornamentoSaldo(nomeGiocatore, idPartita, costoAlbergo);
            pCPPRepository.compraAlbergo(posizione);
            String albergoMessage = new ObjectMapper().writeValueAsString(Map.of(
                    "type", "acquistoRiuscito"
            ));
            session.sendMessage(new TextMessage(albergoMessage));
        } else {
            String albergoMessage = new ObjectMapper().writeValueAsString(Map.of(
                    "type", "acquistoFallito"
            ));
            session.sendMessage(new TextMessage(albergoMessage));
        }
    }
}
