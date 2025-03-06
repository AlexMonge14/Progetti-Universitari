package progetto665406.client;

// Classe che rappresenta un elemento Deal, utile per accedere ai dati specifici

public class Deal {
    private final String titolo;
    private final int store;
    private final double prezzoScontato;
    private final double prezzoPieno;
    private final int risparmio;

    public Deal(String titolo, int store, double prezzoScontato, double prezzoPieno, int risparmio) {
        this.titolo = titolo;
        this.store = store;
        this.prezzoScontato = prezzoScontato;
        this.prezzoPieno = prezzoPieno;
        this.risparmio = risparmio;
    }

    public String getTitolo() {
        return titolo;
    }

    public int getStore() {
        return store;
    }

    public double getPrezzoScontato() {
        return prezzoScontato;
    }

    public double getPrezzoPieno() {
        return prezzoPieno;
    }

    public int getRisparmio() {
        return risparmio;
    }
            
}
