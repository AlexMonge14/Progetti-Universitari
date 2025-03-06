package progetto665406.server;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

// Classe Controllore relativa al path "/games", con metodi che gestiscono 
// query sulle tabelle "deals", "stores" e "games"

@Controller
@RequestMapping(path="/games")
public class GamesController {
    
    // Repository collegate automaticamente al controllore
    
    @Autowired
    private DealRepository dealRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private GameRepository gameRepository;
    
    @RequestMapping(method = RequestMethod.GET, path = "/bestdeals")
    public @ResponseBody List<Deal> getDeals() {
        
        // Top 10 migliori offerte da mostrare nell'HomePage
        
        return dealRepository.findTop10ByOrderByRisparmioDesc();
    }
    
    @RequestMapping(method = RequestMethod.GET, path = "/dealcategoria")
    public @ResponseBody List<Deal> getCategoria(@RequestParam String gioco) {
        
        // Si popola la tabella in catalogo con elementi contenenti la stringa passata
        
        return dealRepository.findById_GiocoContaining(gioco);
    }
    
    @RequestMapping(method = RequestMethod.GET, path = "/popupstore")
    public @ResponseBody Store getStore(@RequestParam int id) {
        
        // Si recuperano le informazioni per il PopUp dello Store
        
        Optional<Store> storeOpt = storeRepository.findById(id); // Optional perché il metodo è implicito in CrudRepository
        return storeOpt.get();
    }
    
    @RequestMapping(method = RequestMethod.GET, path = "/popupgame")
    public @ResponseBody Game getGame(@RequestParam String nome) {
        
        // Si recuperano le informazioni per il PopUp del Gioco 
        
        return gameRepository.findByNome(nome);
    }
}
