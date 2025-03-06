package progetto665406.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Classe Controllore relativa alla schermata del Catalogo

public class CatalogoController {
    
    private static final Logger logger = LogManager.getLogger(CatalogoController.class);
    
    @FXML ToggleButton b1;
    @FXML TableView<Deal> dealsTable = new TableView<>();
    private ObservableList<Deal> ol = FXCollections.observableArrayList(); 
    
    @FXML
    public void initialize() {
        
        // Per inizializzare la tabella viene simulata atificialmente la pressione del primo bottone
        
        ControllerUtilities.setDealsTable(dealsTable, ol);
        ActionEvent event = new ActionEvent(b1, null);
        handleToggle(event);
    }
    
    // Metodo per tornare all'HomePage
    
    @FXML 
    public void switchToHomePage() throws IOException {
        ControllerUtilities.switchTo("homepage");
    }
    
    public void handleToggle(ActionEvent event) {
        
        // Si ottiene il bottone premuto
        
        ToggleButton clickedButton = (ToggleButton) event.getSource();
        
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    
                    // Se è stato selezionato un toggle, si svuota la tabella e si fa Richiesta GET all'API
                    // che restituisce la saga di videogiochi scelta per ripopolare la stessa
                    
                    if(clickedButton.isSelected()) {
                        String categoria = clickedButton.getText();
                        String categoriaEncoded = URLEncoder.encode(categoria, StandardCharsets.UTF_8.toString()); // L'encoding serve a gestire eventuali spazi nel titolo
                        ol.clear();

                        URL url = new URL("http://localhost:8080/games/dealcategoria?gioco=" + categoriaEncoded);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("GET");

                        con.getResponseCode();

                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                            String line;
                            StringBuilder response = new StringBuilder();
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
                        catch(Exception ioe) {
                            logger.error("ERRORE, catturata un'eccezione che ha causato un malfunzionamento.", ioe);
                        }
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
