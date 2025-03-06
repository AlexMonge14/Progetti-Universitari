package progetto665406.server;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;

// Classe associata alla tabella "users" tramite JPA

@Entity
@Table(name="users", schema="665406")
public class Utente implements Serializable {
    
    @EmbeddedId
    private UtenteID id; // Si riferisce alla classe "UtenteID"
    @Column(name="Nome")
    private String nome;
    @Column(name="Cognome")
    private String cognome;
    @Column(name="Saldo")
    private double saldo;

    public Utente() {
        this.id = null;
        this.nome = null;
        this.cognome = null;
        this.saldo = 0;
    }

    public Utente(UtenteID id, String nome, String cognome, double saldo) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.saldo = saldo;
    }

    public UtenteID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public double getSaldo() {
        return saldo;
    }
    
    public void setId(UtenteID id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
    
}
