package progetto665406.server;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Classe associata alla tabella "games" tramite JPA

@Entity
@Table(name="games", schema="665406")
public class Game {
    @Id
    @Column(name="ID")
    private Integer id;
    @Column(name="Nome")
    private String nome;
    @Column(name="Minore")
    private double minore;

    public Game() {
    }

    public Game(Integer id, String nome, double minore) {
        this.id = id;
        this.nome = nome;
        this.minore = minore;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getMinore() {
        return minore;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setMinore(double minore) {
        this.minore = minore;
    }
    
    
}
