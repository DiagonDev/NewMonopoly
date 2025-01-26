package unimib.dabancherz.newmonopoly.manager;

import org.springframework.stereotype.Component;
import unimib.dabancherz.newmonopoly.singleton.GameBoardSingleton;
import unimib.dabancherz.newmonopoly.database.entity.classiparametri.*;
import unimib.dabancherz.newmonopoly.database.repository.*;

@Component
public class OpportunitaManager {

    private final PartitaCasellaPrezzoproprietaRepository pCPPRepository;
    private final GiocatoreRepository giocatoreRepository;
    private final PartitaOpportunitaRepository partitaOpportunitaRepository;
    GameBoardSingleton gameBoard = GameBoardSingleton.getInstance();
    private final String PROBABILITAKEY = "Probabilità";

    public OpportunitaManager(PartitaCasellaPrezzoproprietaRepository pCPPRepository, GiocatoreRepository giocatoreRepository, PartitaOpportunitaRepository partitaOpportunitaRepository) {
        this.pCPPRepository = pCPPRepository;
        this.giocatoreRepository = giocatoreRepository;
        this.partitaOpportunitaRepository = partitaOpportunitaRepository;
    }

    public void gestisciImporto(Object parametri, String idPartita, String nomeGiocatore) {
        Importo importoDeserializzato = (Importo) parametri;
        int importo = importoDeserializzato.getImporto();
        giocatoreRepository.setSaldoGiocatore(nomeGiocatore, idPartita, importo);
    }

    public void gestisciPagamentoGiocatori(String tipoAzione, Object parametri, String idPartita, String nomeGiocatore) {
        Importo importoDeserializzato = (Importo) parametri;
        int importo = importoDeserializzato.getImporto();
        int soldi = (giocatoreRepository.contaGiocatoriInPartita(idPartita) - 1) * importo;

        if (tipoAzione.equals("paga_importo_giocatore")) {
            giocatoreRepository.pagaImportoGiocatori(importo, idPartita, nomeGiocatore);
            giocatoreRepository.setSaldoGiocatore(nomeGiocatore, idPartita, soldi);
        } else {
            giocatoreRepository.setSaldoGiocatore(nomeGiocatore, idPartita, -soldi);
            giocatoreRepository.pagaImportoGiocatori(-importo, idPartita, nomeGiocatore);
        }
    }

    public void gestisciPagamentoPossedimenti(Object parametri, String idPartita, String nomeGiocatore) {
        PagaPossedimenti pagaPossedimentiDeserializzato = (PagaPossedimenti) parametri;
        int importoCasa = pagaPossedimentiDeserializzato.getCosto_casa();
        int importoAlbergo = pagaPossedimentiDeserializzato.getCosto_albergo();

        int numCase = pCPPRepository.contaCaseTot(nomeGiocatore, idPartita);
        int numAlberghi = pCPPRepository.contaAlberghiTot(nomeGiocatore, idPartita);

        int totaleDaPagare = (numCase * importoCasa) + (numAlberghi * importoAlbergo);
        giocatoreRepository.setSaldoGiocatore(nomeGiocatore, idPartita, totaleDaPagare);
    }

    public int gestisciSpostamento(Object parametri, int posizione, String idPartita, String nomeGiocatore) {
        int idCasella;

        if (parametri instanceof IdCasella idCasellaDeserializzato) {
            idCasella = idCasellaDeserializzato.getId_casella();

            if (posizione > idCasella) {
                giocatoreRepository.setSaldoGiocatore(nomeGiocatore, idPartita, -200);
            }
        } else if (parametri instanceof TipoCasella tipoCasellaDeserializzato) {
            idCasella = pCPPRepository.findNextCasellaByTipo(tipoCasellaDeserializzato.getTipo_casella(), posizione, idPartita);
        } else {
            return 0;
        }
        gameBoard.setPlayerPosition(idPartita, nomeGiocatore, idCasella);
        return idCasella;
    }

    public int gestisciPrigione(Object parametri, String idPartita, String nomeGiocatore){
        IdCasella idCasellaDeserializzato = (IdCasella) parametri;
        int idCasella = idCasellaDeserializzato.getId_casella();

        gameBoard.setPlayerPosition(idPartita, nomeGiocatore, idCasella);
        gameBoard.setPlayerPrison(idPartita, nomeGiocatore, true);
        return idCasella;
    }

    public void gestisciUscitaPrigione(String idPartita, String nomeGiocatore, String typeBox) {
        if (typeBox.equals(PROBABILITAKEY)) {
            partitaOpportunitaRepository.setGiocatore(idPartita, nomeGiocatore,  "esci_prigione", "Probabilità");
        } else {
            partitaOpportunitaRepository.setGiocatore(idPartita, nomeGiocatore,  "esci_prigione", "Imprevisto");
        }
    }
}
