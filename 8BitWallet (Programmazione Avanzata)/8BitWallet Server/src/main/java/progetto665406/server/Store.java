package progetto665406.server;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

// Classe associata alla tabella "stores" tramite JPA

@Entity
@Table(name="stores", schema="665406")
public class Store implements Serializable {
    @Id
    @Column(name="ID")
    private Integer id;
    @Column(name="Nome")
    private String nome;
    @Column(name="Attivo")
    private int attivo;

    public Store() {
    }

    public Store(int id, String nome, int attivo) {
        this.id = id;
        this.nome = nome;
        this.attivo = attivo;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getAttivo() {
        return attivo;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setAttivo(int attivo) {
        this.attivo = attivo;
    }
    
}
