package progetto665406.server;

import jakarta.persistence.Embeddable;

// Classe che definisce l'EmbeddedID utilizzato in "Deal"

@Embeddable
public class UtenteID {
    private String username;
    private String password;

    public UtenteID() {
    }

    public UtenteID(String username, String password) {
        this.username = username;
        this.password = password;
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
