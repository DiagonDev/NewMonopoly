/// ESEMPIO USO DAO

package unimib.daBancherz.NewMonopoly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unimib.daBancherz.NewMonopoly.DAO.GiocatoreDAO;
import unimib.daBancherz.NewMonopoly.Entity.Giocatore;

import java.util.List;

@Service
public class GiocatoreService {

    @Autowired
    private GiocatoreDAO giocatoreDAO;

    // Metodo per ottenere tutti i giocatori
    public List<Giocatore> getAllGiocatori() {
        return giocatoreDAO.findAll();
    }

    // Metodo per salvare un nuovo giocatore
    public Giocatore saveGiocatore(Giocatore giocatore) {
        return giocatoreDAO.save(giocatore);
    }

    // Metodo per trovare un giocatore per ID
    public Giocatore findGiocatoreById(Long id) {
        return giocatoreDAO.findById(id).orElse(null);
    }

    // Metodo per eliminare un giocatore
    public void deleteGiocatore(Long id) {
        giocatoreDAO.deleteById(id);
    }
}
