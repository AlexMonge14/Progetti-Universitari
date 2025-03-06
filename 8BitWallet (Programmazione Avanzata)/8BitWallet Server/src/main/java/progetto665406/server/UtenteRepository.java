package progetto665406.server;

import org.springframework.data.repository.CrudRepository;

// Interfaccia Repository associata a "Utente" necessaria per interagire 
// col database tramite JPA

public interface UtenteRepository extends CrudRepository<Utente, UtenteID> {
    Utente findByid_Username(String username);
    Utente save(Utente u);
}
