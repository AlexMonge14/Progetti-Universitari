package progetto665406.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ControllerUtilities {
    
    private static final Logger logger = LogManager.getLogger(ControllerUtilities.class);
    
    public static void switchTo(String pagina) throws IOException {
        App.setRoot(pagina);
    }
    
    public static void setProfilo(Text t1, Text t2) {
        
        // Vengono impostati i valori relativi all'utente loggato, presi dal SessionManager
        
        t1.setText(SessionManager.getUtenteAutenticato().getNome() + " " + SessionManager.getUtenteAutenticato().getCognome());
        t2.setText(" " + SessionManager.getUtenteAutenticato().getSaldo() + " €");
        
        // Il saldo viene colorato a seconda del suo valore 
        // (rosso se basso, arancione se medio e verde se alto)
        
        if(SessionManager.getUtenteAutenticato().getSaldo() < 10)
            t2.setFill(Color.RED);
        else if(SessionManager.getUtenteAutenticato().getSaldo() < 50)
            t2.setFill(Color.ORANGE);
        else t2.setFill(Color.GREEN);
    }
    
    public static void setDealsTable(TableView<Deal> dealsTable, ObservableList<Deal> ol) {
        
        // La tabella viene dinamicamente popolata da colonne che rappresentano i campi di Deal,
        
        TableColumn titoloCol = new TableColumn("Titolo");
        titoloCol.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        TableColumn storeCol = new TableColumn("Store");
        storeCol.setCellValueFactory(new PropertyValueFactory<>("store"));
        TableColumn scontatoCol = new TableColumn("Prezzo Scontato");
        scontatoCol.setCellValueFactory(new PropertyValueFactory<>("prezzoScontato"));
        TableColumn pienoCol = new TableColumn("Prezzo Pieno");
        pienoCol.setCellValueFactory(new PropertyValueFactory<>("prezzoPieno"));
        TableColumn risparmioCol = new TableColumn("Risparmio");
        risparmioCol.setCellValueFactory(new PropertyValueFactory<>("risparmio"));
        
        dealsTable.getColumns().addAll(titoloCol, storeCol, scontatoCol, pienoCol, risparmioCol); 
        dealsTable.setItems(ol);
    }
    
    public static void setTabellaCarrello(ObservableList<Deal> ol) {
        
        // Si scorre la lista del carrello tramite un enhanced-for per popolare la tabella
        
        ArrayList<Deal> list = SessionManager.getCarrello();

        for(Deal item: list) {
            ol.add(item);
        }
    }
    
    public static void setTotale(Text t2, Text t3) {
        
        // Si prende il testo precedentemente settato e si converte in double
        
        String attuale = t2.getText();
        String saldo = attuale.replaceAll("[^0-9.]", "");
        double numero = Double.parseDouble(saldo);
        ArrayList<Deal> list = SessionManager.getCarrello();
        
        // Il prezzo totale viene aggiornato man mano tramite un enhanced-for che scorre il carrello
        
        double totale = 0;
        for(Deal item: list) {
            totale += item.getPrezzoScontato();
        }
        
        // Viene scritto il formato corretto (punto al posto della virgola)
        
        String formatted = String.format("%.2f", totale);
        String trueFormatted = formatted.replace(",", ".");
        t3.setText(trueFormatted + " €");
        
        // Se il totale è maggiore del saldo, viene colorato di rosso e acquistabile è settato a false
        
        if (totale > numero) {
            t3.setFill(Color.RED);
            SessionManager.setAcquistabile(false);
        }
        
        // Se il totale è minore/uguale del saldo, viene colorato di verde e acquistabile è settato a true
        
        else {
            t3.setFill(Color.GREEN);
            SessionManager.setAcquistabile(true);
        }
    }
    
    public static void utilAggiungiCarrello(TableView<Deal> dealsTable) {
        
        // L'elemento selezionato viene aggiunto al carrello, ciò è notificato
        // tramite un Alert informativo
        
        Deal d = dealsTable.getSelectionModel().getSelectedItem();
        String gioco = d.getTitolo();
        SessionManager.getCarrello().add(d);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operazione riuscita");
        alert.setHeaderText(gioco + " aggiunto al carrello con successo!");
        alert.setContentText("Visualizza il carrello dall'apposita sezione accessibile dall'HomePage");
        alert.showAndWait();
    }
    
    public static void utilPopUpGame(TableView<Deal> dealsTable) {
        Task task = new Task<Void>() {
            @Override 
            public Void call() {
                try {
                    String gioco = dealsTable.getSelectionModel().getSelectedItem().getTitolo();
                    String giocoEncoded = URLEncoder.encode(gioco, StandardCharsets.UTF_8.toString()); // L'encoding serve a gestire eventuali spazi nel titolo

                    URL url = new URL("http://localhost:8080/games/popupgame?nome=" + giocoEncoded);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");

                    con.getResponseCode();

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                        String line;
                        StringBuilder response = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }  
                        
                        // Il Json viene gestito e vengono prelevate le informazioni per popolare il Dialog

                        Gson gson = new Gson();
                        JsonElement element = gson.fromJson(response.toString(), JsonElement.class);
                        JsonObject rootObject = element.getAsJsonObject();
                        int id = rootObject.get("id").getAsInt();
                        double minore = rootObject.get("minore").getAsDouble();
                        
                        Platform.runLater(new Runnable() { 
                            @Override
                            public void run() {  
                                Dialog<String> dialog = new Dialog<>();
                                dialog.setHeaderText("Informazioni sul Gioco");
                                dialog.setContentText("ID: " + id + "\nNome: " + gioco + "\nPrezzo Minore sull'app: " + minore );
                                dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                                dialog.show();
                            } 
                        });  
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
    
    public static void utilPopUpStore(TableView<Deal> dealsTable) {
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    int store = dealsTable.getSelectionModel().getSelectedItem().getStore();

                    URL url = new URL("http://localhost:8080/games/popupstore?id=" + store);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");

                    con.getResponseCode();

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                        String line;
                        StringBuilder response = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }  
                        
                        // Il Json viene gestito e vengono prelevate le informazioni per popolare il Dialog

                        Gson gson = new Gson();
                        JsonElement element = gson.fromJson(response.toString(), JsonElement.class);
                        JsonObject rootObject = element.getAsJsonObject();
                        String nome = rootObject.get("nome").getAsString();
                        int attivo = rootObject.get("attivo").getAsInt();
                        
                        Platform.runLater(new Runnable() { 
                            @Override
                            public void run() {  
                                Dialog<String> dialog = new Dialog<>();
                                dialog.setHeaderText("Informazioni sullo Store");
                                dialog.setContentText("ID: " + store + "\nNome: " + nome + "\nAttivo: " + ((attivo == 1) ? "Si" : "No"));
                                dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                                dialog.show();
                            } 
                        });  
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
