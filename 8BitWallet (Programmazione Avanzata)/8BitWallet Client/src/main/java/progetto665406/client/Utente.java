package progetto665406.client;

// Classe che rappresenta le informazioni dell'Utente in sessione

public class Utente {
    private String username;
    private String password;
    private String nome;
    private String cognome;
    private double saldo;

    public Utente(String username, String password, String nome, String cognome, double saldo) {
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.saldo = saldo;
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

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public double getSaldo() {
        return saldo;
    }
    
    
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
