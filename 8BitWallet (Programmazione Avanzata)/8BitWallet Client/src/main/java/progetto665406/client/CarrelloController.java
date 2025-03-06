package progetto665406.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CarrelloController {
    
    private static final Logger logger = LogManager.getLogger(CarrelloController.class);
    
    @FXML private Text t1;
    @FXML private Text t2;
    @FXML private Text t3;
    @FXML TableView<Deal> dealsTable = new TableView<>();
    
    private ObservableList<Deal> ol = FXCollections.observableArrayList();

    public void initialize() {
        ControllerUtilities.setProfilo(t1, t2);
        ControllerUtilities.setDealsTable(dealsTable, ol);
        ControllerUtilities.setTabellaCarrello(ol);
        ControllerUtilities.setTotale(t2, t3);
    }    
    
    @FXML 
    public void switchToHomePage() throws IOException {
        ControllerUtilities.switchTo("homepage");
    }
    
    // Metodi relativi al menu a scomparsa della tabella
    
    @FXML 
    public void RimuoviCarrello() {
        
        // Si cancella l'elemento selezionato sia dalla tabella che dal carrello della sessione,
        // poi si setta il nuovo valore del prezzo totale
        
        Deal d = dealsTable.getSelectionModel().getSelectedItem();
        ol.remove(d);
        SessionManager.getCarrello().remove(d);
        ControllerUtilities.setTotale(t2, t3);
    }
    
    @FXML
    public void popUpStore() {
        ControllerUtilities.utilPopUpStore(dealsTable);
    }
    
    @FXML
    public void popUpGame() {
        ControllerUtilities.utilPopUpGame(dealsTable);
    }
    
    @FXML 
    public void simulaAcquisto() {
        
        // Se il totale è maggiore del saldo (acquistabile è false), si lancia un Alert di errore
        
        if(!SessionManager.isAcquistabile()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Simulazione fallita");
            alert.setHeaderText("Il tuo saldo non copre il costo totale degli elementi nel carrello.");
            alert.setContentText("Effettua una ricarica nella schermata apposita accessibile dall'HomePage o rimuovi qualche elemento.");
            alert.showAndWait();
        }
        
        else {
            Task task = new Task<Void>() {
                @Override
                public Void call() {
                    try {
                        
                        // Richiesta POST all'API che gestisce gli acquisti, passando le 
                        // info dell'utente che ha fatto richiesta
                        
                        URL url = new URL("http://localhost:8080/utente/acquisto");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        con.setDoOutput(true);
                        con.connect();
                        
                        // Il valore totale viene passato come double, dopo un'attenta conversione
                        
                        String importo = t3.getText();
                        String numero = importo.replaceAll("[^0-9.,]", "");
                        double totale = Double.parseDouble(numero);
                        
                        String urlParameters = "username=" + SessionManager.getUtenteAutenticato().getUsername() + "&totale=" + totale;
                        
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
                                        alert.setHeaderText("Acquisto non riuscito");
                                        alert.setContentText("Riprovare successivamente.");
                                        alert.showAndWait();
                                    } 
                                }); 
                            }
                            
                            // Se è riuscita si aggiorna il saldo della sessione, si svuota il carrello e si torna all'HomePage
                            
                            else {
                                Platform.runLater(new Runnable() { 
                                    @Override
                                    public void run() {  
                                        try {
                                            double temp = SessionManager.getUtenteAutenticato().getSaldo() - totale;
                                            BigDecimal bigDecimal = new BigDecimal(temp);
                                            bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
                                            double rounded = bigDecimal.doubleValue();
                                            SessionManager.getUtenteAutenticato().setSaldo(rounded);
                                            SessionManager.getCarrello().clear();
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
