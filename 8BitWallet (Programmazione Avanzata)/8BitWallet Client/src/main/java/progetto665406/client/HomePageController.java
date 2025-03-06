package progetto665406.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Classe Controllore relativa alla schermata dell'HomePage

public class HomePageController {
    
    private static final Logger logger = LogManager.getLogger(HomePageController.class);
    
    @FXML private Text t1;
    @FXML private Text t2;
    @FXML TableView<Deal> dealsTable = new TableView<>();
    
    private ObservableList<Deal> ol = FXCollections.observableArrayList();
    
    @FXML 
    public void initialize() {
        
        // Vengono inizializzate le info riguardanti l'utente loggato e la tabella delle offerte
        
        ControllerUtilities.setProfilo(t1, t2);
        ControllerUtilities.setDealsTable(dealsTable, ol);
        
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    
                    // Richiesta GET all'API che ha il compito di popolare la tabella delle offerte 
                    
                    URL url = new URL("http://localhost:8080/games/bestdeals");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    
                    try(BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                        String line;
                        StringBuffer response = new StringBuffer();
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }  
                        
                        Gson gson = new Gson();
                        JsonArray deals = gson.fromJson(response.toString(), JsonArray.class);
                        
                        Platform.runLater(new Runnable() { 
                            @Override
                            public void run() {  
                                for(int i = 0; i < deals.size(); i++) {
                                    JsonObject j = deals.get(i).getAsJsonObject();
                                        
                                    Deal d = new Deal(j.getAsJsonObject("id").get("gioco").getAsString(),
                                                      j.getAsJsonObject("id").get("store").getAsInt(),
                                                      j.get("scontato").getAsDouble(),
                                                      j.get("pieno").getAsDouble(),
                                                      j.get("risparmio").getAsInt());
                                    ol.add(d);
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
    
    // Metodi che permettono di passare a diverse schermate
    
    @FXML 
    public void switchToRicarica() throws IOException {
        ControllerUtilities.switchTo("ricarica");
    }
    
    @FXML 
    public void switchToCatalogo() throws IOException {
        ControllerUtilities.switchTo("catalogo");
    }
    
    @FXML
    public void switchToCarrello() throws IOException {
        ControllerUtilities.switchTo("listacarrello");
    }
    
    // Metodi relativi al menu a scomparsa della tabella
    
    @FXML
    public void AggiungiCarrello() {
        ControllerUtilities.utilAggiungiCarrello(dealsTable);
    }
    
    @FXML
    public void popUpStore() {
        ControllerUtilities.utilPopUpStore(dealsTable);
    }
    
    @FXML
    public void popUpGame() {
        ControllerUtilities.utilPopUpGame(dealsTable);
    }

}
