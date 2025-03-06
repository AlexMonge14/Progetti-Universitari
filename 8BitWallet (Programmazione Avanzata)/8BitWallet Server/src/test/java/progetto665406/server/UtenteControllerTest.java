package progetto665406.server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// Classe di Test per i metodi di UtenteController
// Effettuare il testing dopo aver popolato il database tramite l'applicazione 
// (avviando prima il Server e poi il Client)

@SpringBootTest
public class UtenteControllerTest {
    
    @Autowired
    private UtenteController uc;
    
    public UtenteControllerTest() {
    }
    
    // Test del metodo "login"
    
    @Test
    public void testLoginRiuscito() {
        String username = "AlexMonge";
        String password = "AlexMonge";
        String result = uc.login(username, password).getId().getUsername();
        assertEquals(username, result, "Il test non ha restituito la risposta attesa.");
        System.out.println("Test Login Riuscito completato");
    }
    
    @Test
    public void testLoginFallito() {
        String username = "AlexMonge";
        String password = "AlexMong"; // password non corretta, dovrebbe fallire
        UtenteID expResult = null;
        UtenteID result = uc.login(username, password).getId();
        assertEquals(expResult, result, "Il test non ha restituito la risposta attesa.");
        System.out.println("Test Login Fallito completato");
    }

    // Test del metodo "verificaRegistrazione"
    
    @Test
    public void testVerificaRegistrazioneRiuscito() {
        String nome = "Test";
        String cognome = "Test";
        String username = "ProfiloTest";
        String password = "ProfiloT";
        String saldo = "0";
        boolean expResult = true;
        boolean result = uc.verificaRegistrazione(nome, cognome, username, password, saldo);
        assertEquals(expResult, result, "Il test non ha restituito la risposta attesa.");
        System.out.println("Test Registrazione Riuscita completato");
    }
    
    @Test
    public void testVerificaRegistrazioneFallito() {
        String nome = "Alex";
        String cognome = "Mongelluzzi";
        String username = "AlexMonge"; // username già esistente, dovrebbe fallire
        String password = "AlexMonge";
        String saldo = "0";
        boolean expResult = false;
        boolean result = uc.verificaRegistrazione(nome, cognome, username, password, saldo);
        assertEquals(expResult, result, "Il test non ha restituito la risposta attesa.");
        System.out.println("Test Registrazione Fallita completato");
    }

    // Test del metodo "Ricarica"
    
    @Test
    public void testRicarica() {
        String username = "AlexMonge";
        double saldo = 0.0;
        boolean expResult = true;
        boolean result = uc.ricarica(username, saldo);
        assertEquals(expResult, result, "Il test non ha restituito la risposta attesa.");
        System.out.println("Test Ricarica completato");
    }

    // Test del metodo "simulaAcquisto"
    
    @Test
    public void testSimulaAcquisto() {
        String username = "AlexMonge";
        double totale = 0.0;
        boolean expResult = true;
        boolean result = uc.simulaAcquisto(username, totale);
        assertEquals(expResult, result, "Il test non ha restituito la risposta attesa.");
        System.out.println("Test Acquisto completato");
    }
    
}
