package progetto665406.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Double.parseDouble;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Classe Controllore relativa alla schermata della Ricarica del saldo dell'utente

public class RicaricaController {
    
    private static final Logger logger = LogManager.getLogger(RicaricaController.class);
    
    @FXML private Text t1;
    @FXML private Text t2;
    @FXML private TextField rica;
    
    @FXML 
    public void initialize() {
        
        // Vengono inizializzate le info riguardanti l'utente loggato
        
        ControllerUtilities.setProfilo(t1, t2);
    }
    
    // Metodo per tornare all'HomePage
    
    @FXML 
    public void switchToHomePage() throws IOException {
        ControllerUtilities.switchTo("homepage");
    }
    
    // Metodo che sanifica l'input dell'utente relativo al saldo
    
    @FXML
    public boolean validaImporto(String input) {
        String numberRegex = "^(100|[1-9]?[0-9])$";
        return input.matches(numberRegex);
    }
    
    @FXML 
    public void ricaricaSaldo() {
        String input = rica.getText();
        
        // Se l'input non è valido si lancia un Alert
        
        if(!validaImporto(input)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di validazione");
            alert.setHeaderText("Input non valido");
            alert.setContentText("Inserisci SOLO UN NUMERO da 1 a 100.");
            alert.showAndWait();
            rica.setText("");
        }
        
        // Se è valido si converte a double e si somma al saldo corrente
        
        else {
            double importo = parseDouble(input);
            double saldoCorrente = SessionManager.getUtenteAutenticato().getSaldo();
            double saldoAggiornato = importo + saldoCorrente;
            
            Task task = new Task<Void>() {
                @Override
                public Void call() {
                    try {
                        
                        // Richiesta POST all'API che gestisce le ricariche del saldo, passando le 
                        // info dell'utente che ha fatto richiesta
                        
                        URL url = new URL("http://localhost:8080/utente/ricarica");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        con.setDoOutput(true);
                        con.connect();

                        String urlParameters = "username=" + SessionManager.getUtenteAutenticato().getUsername() + "&saldo=" + saldoAggiornato;
                        
                        try (DataOutputStream writer = new DataOutputStream(con.getOutputStream())) {
                            writer.writeBytes(urlParameters); 
                            writer.flush();
                        }
                        
                        con.getResponseCode();
                        
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                            String line;
                            StringBuilder response = new StringBuilder();
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }  
                            
                            String message = response.toString();
                            
                            // Se l'operazione è fallita si lancia un Alert
                            
                            if(message.equals("false")) {
                                Platform.runLater(new Runnable() { 
                                    @Override
                                    public void run() {  
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Errore");
                                        alert.setHeaderText("Ricarica non riuscita");
                                        alert.setContentText("Riprovare successivamente.");
                                        alert.showAndWait();
                                    } 
                                }); 
                            }
                            
                            // Se è riuscita si aggiorna il saldo della sessione e si torna all'HomePage
                            
                            else {
                                Platform.runLater(new Runnable() { 
                                    @Override
                                    public void run() {  
                                        try {
                                            SessionManager.getUtenteAutenticato().setSaldo(saldoAggiornato);
                                            App.setRoot("homepage");
                                        }
                                        catch(IOException ioe) {
                                           logger.error("ERRORE, catturata un'eccezione che ha causato un malfunzionamento.", ioe);
                                        }
                                    } 
                                }); 
                            }
                        }
                        
                    }
                    catch(IOException ioe) {
                    logger.error("ERRORE, catturata un'eccezione che ha causato un malfunzionamento.", ioe);
                }
                    return null;
                }
            };
            new Thread(task).start();
        }
    }
}
