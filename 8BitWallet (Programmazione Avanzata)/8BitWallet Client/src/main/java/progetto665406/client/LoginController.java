package progetto665406.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.*;

// Classe Controllore relativa alla schermata di Login e Registrazione

public class LoginController {
    
    private static final Logger logger = LogManager.getLogger(LoginController.class);

    @FXML private Button carica;
    @FXML private Button login;
    @FXML private Button reg;
    @FXML private ProgressIndicator pi;
    @FXML private TextField usrlogin;
    @FXML private PasswordField pswlogin;
    @FXML private TextField nom;
    @FXML private TextField cog;
    @FXML private TextField usrreg;
    @FXML private PasswordField pswreg;
    
    @FXML
    public void initialize() {
        
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                
                // Si controlla se il database è stato precedentemente popolato tramite l'API "popolato"
                
                try {
                    URL url = new URL("http://localhost:8080/caricadati/popolato");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                        String line;
                        StringBuffer response = new StringBuffer();
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }  

                        String message = response.toString();
                        
                        Platform.runLater(new Runnable() { 
                            @Override
                            public void run() {  
                                pi.setProgress(1);
                                
                                // In caso sia popolato, viene disabilito il bottone
                                // per caricare i dati (l'operazione viene segnalata come completata)
                                // e si abilitano quelli per login e registrazione
                                
                                if(message.equals("true")) {
                                    carica.setDisable(true);
                                    pi.setVisible(true);
                                    login.setDisable(false);
                                    reg.setDisable(false);
                                }
                                
                                // Se non è popolato si disabilitano i bottoni per login e registrazione
                                
                                
                                else {
                                    pi.setVisible(false);
                                    login.setDisable(true);
                                    reg.setDisable(true);
                                }
                            } 
                        }); 
                        
                    }
                }
                catch(Exception e) {
                    logger.error("ERRORE, catturata un'eccezione che ha causato un malfunzionamento.", e);
                }
                return null;
            }
        };  
        new Thread(task).start();
    }

    @FXML
    public void popolaDatabase() throws IOException {
        
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                
                // Su input dell'utente viene invocata l'API apposita per popolare il database
                
                try {
                    URL url = new URL("http://localhost:8080/caricadati");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    con.connect();
                    
                    con.getResponseCode();
                    
                    // L'interfaccia è aggiornata per permettere l'utilizzo delle sue funzionalità
                    
                    Platform.runLater(new Runnable() { 
                      @Override
                      public void run() {  
                        carica.setDisable(true); 
                        pi.setVisible(true);
                        login.setDisable(false);
                        reg.setDisable(false);
                      } 
                    }); 
                }
                catch (IOException ioe) {
                    logger.error("ERRORE, catturata un'eccezione che ha causato un malfunzionamento.", ioe);
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    // Metodi volti a sanificare gli input dell'utente tramite Regular Expressions
    
    @FXML
    public boolean validaAnagrafica(String input) {
        String anagraficaRegex = "^[A-Z][a-z]+$";
        return input.matches(anagraficaRegex);
    }
    
    @FXML
    public boolean validaUsername(String input) {
        String usernameRegex = "^[a-zA-Z0-9]{8,16}$";
        return input.matches(usernameRegex);
    }
    
    @FXML
    public boolean validaPassword(String input) {
        String passwordRegex = "^[a-zA-Z0-9]{6,10}$";
        return input.matches(passwordRegex);
    }
    
    @FXML 
    public void handleLogin() {
        String username = usrlogin.getText();
        String password = pswlogin.getText();
        
        // Se i campi non sono validi si lancia un Alert
        
        if(!validaUsername(username) || !validaPassword(password)) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Errore di validazione");
            alert.setHeaderText("Input non valido");
            alert.setContentText("Per favore, inserisci i dati nel formato corretto.");
            alert.showAndWait();
            usrlogin.setText("");
            pswlogin.setText("");
        }
        
        else {
            Task task = new Task<Void>() {
                @Override
                public Void call() {
                    try {
                        
                        // Viene mandato una richiesta POST con i dati immessi per effettuare il Login
                        
                        URL url = new URL("http://localhost:8080/utente/login");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        con.setDoOutput(true);
                        con.connect();

                        String urlParameters = "username=" + username + "&password=" + password;
                        
                        try (DataOutputStream writer = new DataOutputStream(con.getOutputStream())) {
                            writer.writeBytes(urlParameters); 
                            writer.flush();
                        }
                        
                        con.getResponseCode();
                        
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                            String line;
                            StringBuffer response = new StringBuffer();
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            } 
                            
                            // Il JSON viene gestito, controllando che i valori non siano null (caso di login fallito)
                            
                            Gson gson = new Gson();
                            JsonElement user = gson.fromJson(response.toString(), JsonElement.class);
                            JsonObject rootObject = user.getAsJsonObject();
                            String nome = rootObject.has("nome") && !rootObject.get("nome").isJsonNull() ? rootObject.get("nome").getAsString() : "";
                            String cognome = rootObject.has("cognome") && !rootObject.get("cognome").isJsonNull() ? rootObject.get("cognome").getAsString() : "";
                            double saldo = rootObject.get("saldo").getAsDouble();
                            
                            // Se il login è fallito si lancia un Alert

                            if(nome.equals("") && cognome.equals("")) {
                                Platform.runLater(new Runnable() { 
                                    @Override
                                    public void run() {  
                                        Alert alert = new Alert(AlertType.ERROR);
                                        alert.setTitle("Errore di autenticazione");
                                        alert.setHeaderText("Login non riuscito");
                                        alert.setContentText("Credenziali errate, riprova.");
                                        alert.showAndWait();
                                        usrlogin.setText("");
                                        pswlogin.setText("");
                                    } 
                                }); 
                            }
                            
                            // Se invece è riuscito si salvano i dati della sessione e si carica la schermata di HomePage
                            
                            else {
                                Platform.runLater(new Runnable() { 
                                    @Override
                                    public void run() {  
                                        try {
                                            SessionManager.setUtenteAutenticato(new Utente(username, password, nome, cognome, saldo));
                                            ControllerUtilities.switchTo("homepage");
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
    
    @FXML
    public void handleRegistrazione() {
        String nome = nom.getText();
        String cognome = cog.getText();
        String username = usrreg.getText();
        String password = pswreg.getText();
        
        // Se i campi non sono validi si lancia un Alert
        
        if(!validaAnagrafica(nome) || !validaAnagrafica(cognome) || !validaUsername(username) || !validaPassword(password)) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Errore di validazione");
            alert.setHeaderText("Input non valido");
            alert.setContentText("Per favore, inserisci i dati nel formato corretto.");
            alert.showAndWait();
            nom.setText("");
            cog.setText("");
            usrreg.setText("");
            pswreg.setText("");
        }
        
        else {
            Task task = new Task<Void>() {
                @Override
                public Void call() {
                    try {
                        
                        // Viene mandato una richiesta POST con i dati immessi per effettuare la Registrazione
                        
                        URL url = new URL("http://localhost:8080/utente/registrazione");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        con.setDoOutput(true);
                        con.connect();
                        
                        String urlParameters = "nome=" + nome + "&cognome=" + cognome + "&username=" + username + "&password=" + password + "&saldo=0";
                        
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
                            
                            // Se la registrazine è fallita si lancia un Alert di errore
                            
                            String message = response.toString();
                            if(message.equals("false")) {
                                Platform.runLater(new Runnable() { 
                                    @Override
                                    public void run() {  
                                        Alert alert = new Alert(AlertType.ERROR);
                                        alert.setTitle("Errore di autenticazione");
                                        alert.setHeaderText("Registrazione non riuscita");
                                        alert.setContentText("Credenziali associate ad un utente esistente.");
                                        alert.showAndWait();
                                        nom.setText("");
                                        cog.setText("");
                                        usrreg.setText("");
                                        pswreg.setText("");
                                    } 
                                }); 
                            }
                            
                            // Se invece è riuscita, si lancia un Alert informativo, che notifica
                            // il successo dell'operazione e la possibilità di loggarsi
                            
                            else {
                                Platform.runLater(new Runnable() { 
                                    @Override
                                    public void run() {  
                                        Alert alert = new Alert(AlertType.INFORMATION);
                                        alert.setTitle("Operazione Riuscita!");
                                        alert.setHeaderText("Registrazione andata a buon fine.");
                                        alert.setContentText("Accedi subito dalla schermata di login.");
                                        alert.showAndWait();
                                        nom.setText("");
                                        cog.setText("");
                                        usrreg.setText("");
                                        pswreg.setText("");
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
