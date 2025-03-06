package progetto665406.server;

import jakarta.persistence.Embeddable;

// Classe che definisce l'EmbeddedID utilizzato in "Deal"

@Embeddable
public class DealID {
    private String gioco;
    private int store;

    public DealID() {
    }

    public DealID(String gioco, int store) {
        this.gioco = gioco;
        this.store = store;
    }

    public String getGioco() {
        return gioco;
    }

    public int getStore() {
        return store;
    }

    public void setGioco(String gioco) {
        this.gioco = gioco;
    }

    public void setGioco(int store) {
        this.store = store;
    }
}
