package progetto665406.client;

import java.util.*;

// Classe utile a mantenere informazioni mostrate a runtime, come i
// dati dell'utente ed il Carrello (vengono soppresse alla chiusura)

public class SessionManager {
    
    private static Utente utenteAutenticato;
    private static ArrayList<Deal> carrello = new ArrayList<>(); 
    private static boolean acquistabile; // booleano che indica la possibilità di simulare un acquisto

    public static void setAcquistabile(boolean acquistabile) {
        SessionManager.acquistabile = acquistabile;
    }
    
    public static void setUtenteAutenticato(Utente utenteAutenticato) {
        SessionManager.utenteAutenticato = utenteAutenticato;
    }
    
     public static Utente getUtenteAutenticato() {
        return utenteAutenticato;
    }

    public static ArrayList<Deal> getCarrello() {
        return carrello;
    }

    public static boolean isAcquistabile() {
        return acquistabile;
    }

    public static void setCarrello(ArrayList<Deal> carrello) {
        SessionManager.carrello = carrello;
    }
    
}
