package progetto665406.server;

import org.springframework.data.repository.CrudRepository;

// Interfaccia Repository associata a "Deal" necessaria per interagire 
// col database tramite JPA

public interface StoreRepository extends CrudRepository<Store, Integer>{
    
    // Non ci sono metodi dichiarati perché viene usato findById,
    // implicito in CrudRepository
    
}
