package progetto665406.server;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

// Classe Controllore relativa al path "/utente", con metodi che gestiscono 
// query sulla tabella "users", quali login, registrazione, ricarica e acquisto.
// Di questi viene fornita una classe di Test apposita

@Controller
@RequestMapping(path="/utente")
public class UtenteController {
    
    // Repository collegate automaticamente al controllore
    
    @Autowired
    private UtenteRepository utenteRepository;
    
    @RequestMapping(method = RequestMethod.POST, path = "/login")
    public @ResponseBody Utente login(@RequestParam String username, @RequestParam String password) {
        Utente u = utenteRepository.findByid_Username(username);
        
        // Se l'utente non esiste, vengono restituiti valori null, altrimenti quello trovato
        
        if (u == null)
            u = new Utente();
        
        // Se checkpw (che confronto la passwordStored con quella inviata dalla richiesta) è false,
        // la password non è corretta e si restituisce un oggetto con valori null
        
        else {
            if(!BCrypt.checkpw(password, u.getId().getPassword()))
                u = new Utente();
        }
        return u;
    }
    
    @RequestMapping(method = RequestMethod.POST, path = "/registrazione")
    public @ResponseBody boolean verificaRegistrazione(@RequestParam String nome, @RequestParam String cognome, @RequestParam String username, @RequestParam String password, @RequestParam String saldo) {
        
        // Tramite BCrypt, la password viene crittografata
        
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        
        // Si cerca se l'utente già esiste tramite l'username, in quel caso la registrazione fallisce
        
        UtenteID id = new UtenteID(username, hashedPassword); // Viene salvata la password trattata con hashing
        Utente u = utenteRepository.findByid_Username(username);
        
        // Se invece non esiste, si inserisce
        
        if(u == null) {
            double numero = Double.parseDouble(saldo);
            u = new Utente(id, nome, cognome, numero);
            utenteRepository.save(u);
            return true;
        }
        else
            return false;
    }
    
    @RequestMapping(method = RequestMethod.POST, path = "/ricarica")
    public @ResponseBody boolean ricarica(@RequestParam String username, @RequestParam double saldo) {
        
        // Si cerca se l'utente che ha richiesto la ricarica esiste effettivamente,
        // si aggiorna il saldo e si effettua l'operazione
        
        Utente u = utenteRepository.findByid_Username(username);
        if(u == null)
            return false;
        else {
            u.setSaldo(saldo);
            utenteRepository.save(u);
            return true;
        }
    }
    
    @RequestMapping(method = RequestMethod.POST, path = "/acquisto")
    public @ResponseBody boolean simulaAcquisto(@RequestParam String username, @RequestParam double totale) {
        
        // Si cerca se l'utente che ha richiesto la ricarica esiste effettivamente,
        // si aggiorna il saldo e si effettua l'operazione
        
        Utente u = utenteRepository.findByid_Username(username);
        if(u == null)
            return false;
        else {
            double temp = u.getSaldo() - totale;
            BigDecimal bigDecimal = new BigDecimal(temp); 
            bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP); // La differenza potrebbe portare a risultati sballati in virgola mobile, si approssima
            double rounded = bigDecimal.doubleValue();
            u.setSaldo(rounded);
            utenteRepository.save(u);
            return true;
        }
    }
    
}
