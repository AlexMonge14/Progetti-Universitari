package progetto665406.server;

import org.springframework.data.repository.CrudRepository;

// Interfaccia Repository associata a "Game" necessaria per interagire 
// col database tramite JPA

public interface GameRepository extends CrudRepository<Game, Integer> {
    Game findByNome(String name);
}
