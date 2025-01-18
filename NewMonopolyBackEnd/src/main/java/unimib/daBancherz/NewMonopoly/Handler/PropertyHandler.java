package unimib.daBancherz.NewMonopoly.Handler;

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
    private final PartitaProbabilitaRepository partitaProbabilitaRepository;
    private final GameHandler gameHandler;
    GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();

    public PropertyHandler(PartitaCasellaPrezzoproprietaRepository pCPPRepository, GiocatoreRepository giocatoreRepository, PartitaProbabilitaRepository partitaProbabilitaRepository, GameHandler gameHandler) {
        this.pCPPRepository = pCPPRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.partitaProbabilitaRepository = partitaProbabilitaRepository;
        this.gameHandler = gameHandler;
    }

    public void acquistaProprieta(String[] messageParts, WebSocketSession session) throws IOException {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);

        int prezzoCasella = pCPPRepository.prezzoCasella2(messageParts[1], gameId);
        int saldoGiocatore = giocatoreRepository.saldoGiocatore(playerName, gameId);
        if(saldoGiocatore > prezzoCasella){
            pCPPRepository.setGiocatore(playerName, gameId, messageParts[1]);
            giocatoreRepository.aggiornamentoSaldo(playerName, gameId, prezzoCasella);
            String probabilitaMessage = new ObjectMapper().writeValueAsString(Map.of(
                    "type", "acquistoRiuscito"
            ));
            session.sendMessage(new TextMessage(probabilitaMessage));
        }else{
            String probabilitaMessage = new ObjectMapper().writeValueAsString(Map.of(
                    "type", "acquistoFallito"
            ));
            session.sendMessage(new TextMessage(probabilitaMessage));
        }
    }

    public void gestisciProprieta(String[] messageParts, WebSocketSession session) throws IOException {
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        
    }


}
