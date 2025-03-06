package progetto665406.server;

import com.google.gson.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

// Classe controllore del path "/caricadati", relativo alla popolazione del database

@Controller
@RequestMapping(path="/caricadati") 
public class CaricadatiController {
    
    // Metodo che consente di popolare il database alla pressione del bottone
    
    @RequestMapping(method = RequestMethod.POST, path = "")
    public @ResponseBody void caricaDati() throws Exception {
        try(Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/665406", "root", "root")) {
            
            // Creazione delle tabelle

            Statement st = co.createStatement();
            st.executeUpdate("CREATE TABLE users(" + 
                             "Username VARCHAR(255) NOT NULL," +
                             "Password VARCHAR(255) NOT NULL," +
                             "Nome VARCHAR(255) NOT NULL," +
                             "Cognome VARCHAR(255) NOT NULL," +
                             "Saldo DOUBLE NOT NULL," +
                             "PRIMARY KEY(Username, Password)" +
                             ");");
            st.executeUpdate("CREATE TABLE games(" +
                             "ID INT NOT NULL PRIMARY KEY," +
                             "Nome VARCHAR(255) NOT NULL," +
                             "Minore DOUBLE NOT NULL" +
                             ");");
            st.executeUpdate("CREATE TABLE stores(" +
                             "ID INT NOT NULL PRIMARY KEY," +
                             "Nome VARCHAR(255) NOT NULL," +
                             "Attivo INT NOT NULL" +
                             ");");
            st.executeUpdate("CREATE TABLE deals(" +
                             "Gioco VARCHAR(255) NOT NULL," +
                             "Store VARCHAR(255) NOT NULL," +
                             "Scontato DOUBLE NOT NULL," +
                             "Pieno DOUBLE NOT NULL," + 
                             "Risparmio INT NOT NULL," +
                             "PRIMARY KEY(Gioco, Store)" +
                             ");");
            
            // Popolamento della tabella "users" con un profilo per testare il login
            
            String credenziali = "AlexMonge";
            PreparedStatement prepared = co.prepareStatement("INSERT INTO users (Username, Password, Nome, Cognome, Saldo) VALUES (?, ?, ?, ?, ?)");
            prepared.setString(1, credenziali);
            prepared.setString(2, BCrypt.hashpw(credenziali, BCrypt.gensalt()));
            prepared.setString(3, "Alex");
            prepared.setString(4, "Mongelluzzi");
            prepared.setDouble(5, 0.0);
            prepared.executeUpdate();
            
            //titoli dei giochi da cui prelevare i dati usando l'API pubblica
            
            String titles[] = {"darksouls", "titanfall", "bioshock", "tekken8"}; 
            
            for (String title : titles) {
                URL url = new URL("https://www.cheapshark.com/api/1.0/games?title=" + title); 
                HttpURLConnection con = (HttpURLConnection) url.openConnection(); 
                con.setRequestMethod("GET");
                
                StringBuffer content = new StringBuffer();

                try(BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {                    
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {   
                        content.append(inputLine); 
                    }
                    in.close();
                }
                
                // Trattamento del JSON e popolamento della tabella "games"
                
                Gson gson = new Gson();
                JsonArray games = gson.fromJson(content.toString(), JsonArray.class);
                
                for(int i = 0; i < games.size(); i++) {
                    JsonObject g = games.get(i).getAsJsonObject();
                    PreparedStatement ps = co.prepareStatement("INSERT INTO games (ID, Nome, Minore) VALUES (?, ?, ?)");
                    ps.setInt(1, g.get("gameID").getAsInt());
                    ps.setString(2, g.get("external").getAsString());
                    ps.setDouble(3, g.get("cheapest").getAsDouble());
                    ps.executeUpdate();
                }
            }  
            
            URL url = new URL("https://www.cheapshark.com/api/1.0/stores"); 
            HttpURLConnection con = (HttpURLConnection) url.openConnection(); 
            con.setRequestMethod("GET");
             
            StringBuffer content = new StringBuffer();

            try(BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {                    
                String inputLine;
                while ((inputLine = in.readLine()) != null) {   
                    content.append(inputLine); 
                }
                in.close();
            }
            
            // Trattamento del JSON e popolamento della tabella "stores"
            
            Gson gson = new Gson();
            JsonArray stores = gson.fromJson(content.toString(), JsonArray.class);
            
            for(int i = 0; i < stores.size(); i++) {
                JsonObject g = stores.get(i).getAsJsonObject();
                PreparedStatement ps = co.prepareStatement("INSERT INTO stores (ID, Nome, Attivo) VALUES (?, ?, ?)");
                ps.setInt(1, g.get("storeID").getAsInt());
                ps.setString(2, g.get("storeName").getAsString());
                ps.setInt(3, g.get("isActive").getAsInt());
                ps.executeUpdate();
            }
            
            // ID utili per prendere i dati dei Deals dall'API pubblica
            
            String ids[] = {"128,186,93503,95571,99676,100529,102798,104715,107898,108532,142818,144565,149981,149983,153765,153781,158540,158545,158615,158616,164162,165359,165360,166326,167784", 
                            "169277,169278,171531,172246,175698,185267,186657,198232,202033,229598,229608,229613,229638,229646,270879,270883,270884,281658,281659,281732,281733,281924,286129,286130,286235"};
            
            for (String id : ids) {
                url = new URL("https://www.cheapshark.com/api/1.0/games?ids=" + id); 
                con = (HttpURLConnection) url.openConnection(); 
                con.setRequestMethod("GET");

                content = new StringBuffer();

                try(BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {                    
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {   
                        content.append(inputLine); 
                    }
                }
                
                // Trattamento del JSON e popolamento della tabella "deals"
            
                gson = new Gson();
                JsonElement deals = gson.fromJson(content.toString(), JsonElement.class);
                JsonObject rootObject = deals.getAsJsonObject();
                
                // Uso di Map di java.util per associare un JsonElement ad ogni id
                
                for(Map.Entry<String, JsonElement> entry : rootObject.entrySet()) {
                    JsonElement value = entry.getValue();

                    if (value.isJsonObject()) {
                        JsonObject data = value.getAsJsonObject();
                        String gioco = data.getAsJsonObject("info").get("title").getAsString();
                        
                        JsonArray dealsArray = data.get("deals").getAsJsonArray();
                        
                        for(int i = 0; i < dealsArray.size(); i++) {                         
                            JsonObject d = dealsArray.get(i).getAsJsonObject();
                            PreparedStatement ps = co.prepareStatement("INSERT INTO deals (Gioco, Store, Scontato, Pieno, Risparmio) VALUES (?, ?, ?, ?, ?)");
                            ps.setString(1, gioco);
                            ps.setInt(2, d.get("storeID").getAsInt());
                            ps.setDouble(3, d.get("price").getAsDouble());
                            ps.setDouble(4, d.get("retailPrice").getAsDouble());
                            ps.setInt(5, (int)d.get("savings").getAsDouble());
                            ps.executeUpdate();
                        }  
                    }
                }
            }
        }
    }     
    
    // Metodo che verifica l'effettivo popolamento del database
    
    @RequestMapping(method = RequestMethod.GET, path = "popolato")
    public @ResponseBody boolean isPopolato() throws Exception {
        try(Connection co = DriverManager.getConnection("jdbc:mysql://localhost:3306/665406", "root", "root")) {
            Statement st = co.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '665406';");
            
            // Se esiste un result set, allora è popolato
            
            if(rs.next())
                return true;
            
            else return false;
        }
    }
}
