package unimib.daBancherz.NewMonopoly.Handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import unimib.daBancherz.NewMonopoly.Singleton.GameBoardSingleton;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.GiocatoreRepository;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.PartitaCasellaPrezzoproprietaRepository;
import unimib.daBancherz.NewMonopoly.dataBase.Repository.PartitaProbabilitaRepository;

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

    public void acquistaProprieta(String[] messageParts, WebSocketSession session){
        String gameId = gameHandler.getGameIdBySession(session);
        String playerName = gameHandler.getPlayerNameBySession(session);
        int idCasella = gameBoard.getPlayerPosition(gameId, playerName);

        int prezzoCasella = 0; //TODO: query per prendere il prezzo della casella dal nome e dal gameId
        int saldoGiocatore = giocatoreRepository.saldoGiocatore(playerName, gameId);
        if(saldoGiocatore > prezzoCasella){
            //pCPPRepository.setGiocatore();
            giocatoreRepository.aggiornamentoSaldo(playerName, gameId, prezzoCasella);
        }



    }
}
