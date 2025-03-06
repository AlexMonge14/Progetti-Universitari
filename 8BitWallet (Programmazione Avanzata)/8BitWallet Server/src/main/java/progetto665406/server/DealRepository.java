package progetto665406.server;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

// Interfaccia Repository associata a "Deal" necessaria per interagire 
// col database tramite JPA

public interface DealRepository extends CrudRepository<Deal, DealID>{
    List<Deal> findTop10ByOrderByRisparmioDesc();
    List<Deal> findById_GiocoContaining(String gioco);
}
