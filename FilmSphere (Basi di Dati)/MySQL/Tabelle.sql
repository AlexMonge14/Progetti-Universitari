-- --------------------------------------------------
-- Creazione DataBase 
-- --------------------------------------------------
DROP SCHEMA IF EXISTS FilmSphere ;
CREATE SCHEMA IF NOT EXISTS FilmSphere CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci; 
USE FilmSphere;


-- --------------------------------------------------
-- Area Contenuti
-- --------------------------------------------------


-- --------------------------------------------------
-- Tabella AreaGeografica
-- --------------------------------------------------
DROP TABLE IF EXISTS AreaGeografica;
CREATE TABLE IF NOT EXISTS AreaGeografica (
	Stato VARCHAR(50) NOT NULL,
    PRIMARY KEY (Stato)
) Engine=InnoDB;

-- --------------------------------------------------
-- Tabella Film
-- --------------------------------------------------
DROP TABLE IF EXISTS Film;
CREATE TABLE IF NOT EXISTS Film (
	ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Titolo VARCHAR(100) NOT NULL,
    Durata INT NOT NULL,
    Descrizione VARCHAR(500) NOT NULL,
    RatingAssoluto FLOAT NOT NULL,
	StatoProduzione VARCHAR(50) NOT NULL,
    AnnoProduzione INT NOT NULL,
    FOREIGN KEY (StatoProduzione) REFERENCES AreaGeografica (Stato),
    CHECK (AnnoProduzione >= 1920)
) Engine=InnoDB;

-- --------------------------------------------------
-- Tabella Genere
-- --------------------------------------------------
DROP TABLE IF EXISTS Genere;
CREATE TABLE IF NOT EXISTS Genere (
	ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Descrizione VARCHAR(100) NOT NULL
) Engine=InnoDB;

-- --------------------------------------------------
-- Tabella Tipologia
-- --------------------------------------------------
DROP TABLE IF EXISTS Tipologia;
CREATE TABLE IF NOT EXISTS Tipologia (
	Film INT NOT NULL,
    Genere INT NOT NULL, 
    PRIMARY KEY (Film, Genere),
	FOREIGN KEY (Film) REFERENCES Film (ID),
    FOREIGN KEY (Genere) REFERENCES Genere (ID)
) Engine=InnoDB;

-- --------------------------------------------------
-- Tabella Esclusione
-- --------------------------------------------------
DROP TABLE IF EXISTS Esclusione;
CREATE TABLE IF NOT EXISTS Esclusione(
	Genere INT NOT NULL, 
    Abbonamento VARCHAR(50) NOT NULL, 
    PRIMARY KEY (Genere, Abbonamento),
    FOREIGN KEY (Genere) REFERENCES Genere (ID),
    FOREIGN KEY (Abbonamento) REFERENCES Abbonamento (Tipo)
    ON UPDATE CASCADE ON DELETE CASCADE
) Engine=InnoDB;

-- --------------------------------------------------
-- Tabella RecensioneUtente
-- --------------------------------------------------
DROP TABLE IF EXISTS RecensioneUtente;
CREATE TABLE IF NOT EXISTS RecensioneUtente (
	Utente INT NOT NULL, 
    Film INT NOT NULL, 
    Data DATE NOT NULL,
    Voto FLOAT NOT NULL,
    Testo VARCHAR(500) NULL,
    PRIMARY KEY (Utente, Film),
    FOREIGN KEY (Utente) REFERENCES Utente (ID),
    FOREIGN KEY (Film) REFERENCES FILM (ID),
    CHECK (YEAR(Data) > 2020),
    CHECK (Voto BETWEEN 0 AND 10)
) Engine=InnoDB;

-- --------------------------------------------------
-- Tabella Critico
-- --------------------------------------------------
DROP TABLE IF EXISTS Critico;
CREATE TABLE IF NOT EXISTS Critico (
	ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Nome VARCHAR(50) NOT NULL,
    Cognome VARCHAR(50) NOT NULL,
    Reputazione INT NOT NULL
    CHECK (Reputazione BETWEEN 1 AND 5)
) Engine=InnoDB;

-- --------------------------------------------------
-- Tabella RecensioneCritico
-- --------------------------------------------------
DROP TABLE IF EXISTS RecensioneCritico;
CREATE TABLE IF NOT EXISTS RecensioneCritico (
	Film INT NOT NULL, 
    Critico INT NOT NULL,
    Data DATE NOT NULL,
    Voto FLOAT NOT NULL,
    Testo VARCHAR(500) NOT NULL,
    PRIMARY KEY (Film, Critico),
    FOREIGN KEY (Film) REFERENCES Film (ID),
    FOREIGN KEY (Critico) REFERENCES Critico (ID),
    CHECK (YEAR(Data) >= 2020),
    CHECK (Voto BETWEEN 0 AND 10)
) Engine=InnoDB;

-- --------------------------------------------------
-- Tabella Artista
-- --------------------------------------------------
DROP TABLE IF EXISTS Artista;
CREATE TABLE IF NOT EXISTS Artista (
    ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Nome VARCHAR(50) NOT NULL,
    Cognome VARCHAR(50) NOT NULL,
    Rilevanza FLOAT NOT NULL,
    Ruolo VARCHAR(20) NOT NULL
    CHECK (Rilevanza BETWEEN 1 AND 5)
)  ENGINE=INNODB;

-- --------------------------------------------------
-- Tabella Interpretazione
-- --------------------------------------------------
DROP TABLE IF EXISTS Interpretazione;
CREATE TABLE IF NOT EXISTS Interpretazione (
	Attore INT NOT NULL, 
    Film INT NOT NULL,
    PRIMARY KEY (Attore, Film),
    FOREIGN KEY (Attore) REFERENCES Artista (ID),
    FOREIGN KEY (Film) REFERENCES Film (ID)
) Engine=InnoDB;

-- --------------------------------------------------
-- Tabella Regia
-- --------------------------------------------------
DROP TABLE IF EXISTS Regia;
CREATE TABLE IF NOT EXISTS Regia (
	Regista INT NOT NULL, 
    Film INT NOT NULL,
    PRIMARY KEY (Regista, Film),
    FOREIGN KEY (Regista) REFERENCES Artista (ID),
    FOREIGN KEY (Film) REFERENCES Film (ID)
) Engine=InnoDB;

-- --------------------------------------------------
-- Tabella Premio
-- --------------------------------------------------
DROP TABLE IF EXISTS Premio;
CREATE TABLE IF NOT EXISTS Premio (
	Titolo VARCHAR(50) NOT NULL,
    Categoria VARCHAR(50) NOT NULL,
    Prestigio INT NOT NULL,
    PRIMARY KEY (Titolo, Categoria),
    CHECK (Prestigio BETWEEN 1 AND 5)
) Engine=InnoDB;

-- --------------------------------------------------
-- Tabella PremiazioneFilm
-- --------------------------------------------------
DROP TABLE IF EXISTS PremiazioneFilm;
CREATE TABLE IF NOT EXISTS PremiazioneFilm (
	TitoloPremio VARCHAR(50) NOT NULL,
    CategoriaPremio VARCHAR(50) NOT NULL,
    Film INT NOT NULL,
    Data DATE NOT NULL,
    PRIMARY KEY (TitoloPremio, CategoriaPremio, Film),
    FOREIGN KEY (TitoloPremio) REFERENCES Premio (Titolo),
    FOREIGN KEY (CategoriaPremio) REFERENCES Premio (Categoria),
    FOREIGN KEY (Film) REFERENCES Film (ID),
    CHECK (Data >= 1920)
) Engine=InnoDB;

-- --------------------------------------------------
-- Tabella PremiazioneAttore
-- --------------------------------------------------
DROP TABLE IF EXISTS PremiazioneAttore;
CREATE TABLE IF NOT EXISTS PremiazioneAttore (
	TitoloPremio VARCHAR(50) NOT NULL,
    CategoriaPremio VARCHAR (50) NOT NULL,
    Attore INT NOT NULL,
    Data DATE NOT NULL,
    PRIMARY KEY (TitoloPremio, CategoriaPremio, Attore),
    FOREIGN KEY (TitoloPremio) REFERENCES Premio (Titolo),
    FOREIGN KEY (CategoriaPremio) REFERENCES Premio (Categoria),
    FOREIGN KEY (Attore) REFERENCES Artista (ID),
    CHECK (Data >= 1920)
) Engine=InnoDB;

-- --------------------------------------------------
-- Tabella PremiazioneRegista
-- --------------------------------------------------
DROP TABLE IF EXISTS PremiazioneRegista;
CREATE TABLE IF NOT EXISTS PremiazioneRegista (
	TitoloPremio VARCHAR(50) NOT NULL,
    CategoriaPremio VARCHAR (50) NOT NULL,
    Regista INT NOT NULL,
    Data DATE NOT NULL,
    PRIMARY KEY (TitoloPremio, CategoriaPremio, Regista),
    FOREIGN KEY (TitoloPremio) REFERENCES Premio (Titolo),
    FOREIGN KEY (CategoriaPremio) REFERENCES Premio (Categoria),
    FOREIGN KEY (Regista) REFERENCES Artista (ID),
    CHECK (Data >= 1920)
) Engine=InnoDB;

-- --------------------------------------------------
-- Tabella Lingua
-- --------------------------------------------------
DROP TABLE IF EXISTS Lingua;
CREATE TABLE IF NOT EXISTS Lingua (
	Idioma VARCHAR(50) NOT NULL,
    PRIMARY KEY (Idioma)
) Engine=InnoDB;

-- --------------------------------------------------
-- Tabella Doppiaggio
-- --------------------------------------------------
DROP TABLE IF EXISTS Doppiaggio;
CREATE TABLE IF NOT EXISTS Doppiaggio (
	Contenuto INT NOT NULL,
    Lingua VARCHAR(50) NOT NULL,
    PRIMARY KEY (Contenuto, Lingua),
    FOREIGN KEY (Contenuto) REFERENCES Contenuto (ID),
    FOREIGN KEY (Lingua) REFERENCES Lingua (Idioma)
    ) Engine=InnoDB;
    
-- --------------------------------------------------
-- Tabella Sottotitolaggio
-- --------------------------------------------------
DROP TABLE IF EXISTS Sottotitolaggio;
CREATE TABLE IF NOT EXISTS Sottotitolaggio (
	Contenuto INT NOT NULL,
    Lingua VARCHAR(50) NOT NULL,
    PRIMARY KEY (Contenuto, Lingua),
    FOREIGN KEY (Contenuto) REFERENCES Contenuto (ID),
    FOREIGN KEY (Lingua) REFERENCES Lingua (Idioma)
    ) Engine=InnoDB;
    
    
-- --------------------------------------------------
-- Area Formati
-- --------------------------------------------------


-- --------------------------------------------------
-- Tabella Contenuto
-- --------------------------------------------------
DROP TABLE IF EXISTS Contenuto;
CREATE TABLE IF NOT EXISTS Contenuto (
	ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    AnnoUscita INT NOT NULL,
    Lunghezza INT NOT NULL,
    GrandezzaFile INT NOT NULL,
    Bitrate INT NOT NULL,
    Film INT NOT NULL,
    Formato INT NOT NULL,
    CodecVideo VARCHAR(50) NOT NULL,
    CodecAudio VARCHAR(50) NOT NULL,
    FOREIGN KEY (Film) REFERENCES Film (ID),
    FOREIGN KEY (Formato) REFERENCES Formato (CodiceFormato)
    )Engine=InnoDB;
    
-- --------------------------------------------------
-- Tabella Restrizione
-- --------------------------------------------------   
DROP TABLE IF EXISTS Restrizione;
CREATE TABLE IF NOT EXISTS Restrizione (
	Contenuto INT NOT NULL,
    AreaGeografica VARCHAR(50) NOT NULL,
    PRIMARY KEY (Contenuto, AreaGeografica),
    FOREIGN KEY (Contenuto) REFERENCES Contenuto (ID),
    FOREIGN KEY (AreaGeografica) REFERENCES AreaGeografica (Stato)
)Engine=InnoDB;

-- --------------------------------------------------
-- Tabella Formato
-- --------------------------------------------------  
DROP TABLE IF EXISTS Formato;
CREATE TABLE IF NOT EXISTS Formato (
	CodiceFormato INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Estensione VARCHAR(20) NOT NULL,
    Famiglia VARCHAR(50) NOT NULL,
    BitdepthVideo INT NOT NULL,
    BitdepthAudio INT NOT NULL,
    Risoluzione INT NOT NULL,
    RapportoAspetto FLOAT NOT NULL
)Engine=InnoDB;


-- --------------------------------------------------
-- Area Clienti
-- -------------------------------------------------- 


-- --------------------------------------------------
-- Tabella Utente
-- --------------------------------------------------
DROP TABLE IF EXISTS Utente;
CREATE TABLE IF NOT EXISTS Utente ( 
	ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Nome VARCHAR(50) NOT NULL,
    Cognome VARCHAR(50) NOT NULL,
    Nickname VARCHAR(50) NOT NULL,
    Email VARCHAR(100) NOT NULL,
    Password VARCHAR(20) NOT NULL,
    DataNascita DATE NOT NULL,
    Abbonamento VARCHAR(50) NULL,
    DataInizioAbbonamento DATE DEFAULT NULL,
    FOREIGN KEY (Abbonamento) REFERENCES Abbonamento (Tipo)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;

-- --------------------------------------------------
-- Tabella Fattura
-- --------------------------------------------------
DROP TABLE IF EXISTS Fattura;
CREATE TABLE IF NOT EXISTS Fattura (
	CodiceFatturazione INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    DataEmissione DATE NOT NULL,
    Quota INT NOT NULL,
    DataScadenza DATE NOT NULL,
    DataPagamento DATE NULL,
    Utente INT NOT NULL,
    CartaPAN BIGINT NULL,
    CartaCVV INT NULL,
    CartaMese INT NULL,
    CartaAnno INT NULL,
    FOREIGN KEY (Utente) REFERENCES Utente (ID),
    FOREIGN KEY (CartaPAN) REFERENCES CartaDiCredito (PAN),
    FOREIGN KEY (CartaCVV) REFERENCES CartaDiCredito (CVV),
    FOREIGN KEY (CartaMese) REFERENCES CartaDiCredito (MeseScadenza),
    FOREIGN KEY (CartaAnno) REFERENCES CartaDiCredito (AnnoScadenza)
)  Engine = InnoDB;

-- --------------------------------------------------
-- Tabella Abbonamento
-- --------------------------------------------------
DROP TABLE IF EXISTS Abbonamento;
CREATE TABLE IF NOT EXISTS Abbonamento (
	Tipo VARCHAR(50) NOT NULL,
    Tariffa INT NOT NULL,
    Durata INT NOT NULL,
    VM INT NULL,
    FilmDisponibili INT NULL,
    MaxOre INT NULL,
    MaxGB INT NULL,
    PRIMARY KEY (Tipo),
    CHECK (VM <= 18)
) ENGINE = InnoDB;

-- --------------------------------------------------
-- Tabella Funzionalita
-- --------------------------------------------------
DROP TABLE IF EXISTS Funzionalita;
CREATE TABLE IF NOT EXISTS Funzionalita (
	Nome VARCHAR(50) NOT NULL,
    Descrizione VARCHAR(200) NOT NULL,
    PRIMARY KEY (Nome)
) ENGINE =InnoDB;

-- --------------------------------------------------
-- Tabella Offerta
-- --------------------------------------------------
DROP TABLE IF EXISTS Offerta;
CREATE TABLE IF NOT EXISTS Offerta (
	Abbonamento VARCHAR(50) NOT NULL,
    Funzionalita VARCHAR(50) NOT NULL,
    PRIMARY KEY (Abbonamento, Funzionalita),
    FOREIGN KEY (Abbonamento) REFERENCES Abbonamento (Tipo)
    ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (Funzionalita) REFERENCES Funzionalita (Nome)
) ENGINE =InnoDB;

-- --------------------------------------------------
-- Tabella CartaDiCredito
-- --------------------------------------------------
DROP TABLE IF EXISTS CartaDiCredito;
CREATE TABLE IF NOT EXISTS CartaDiCredito (
	PAN BIGINT NOT NULL,
    CVV INT NOT NULL,
    MeseScadenza INT NOT NULL,
    AnnoScadenza INT NOT NULL,
    NomeIntestatario VARCHAR(50) NOT NULL,
    CognomeIntestatario VARCHAR(50) NOT NULL,
    Utente INT NOT NULL,
    PRIMARY KEY (PAN, CVV, MeseScadenza, AnnoScadenza),
    FOREIGN KEY (Utente) REFERENCES Utente (ID),
    CHECK (MeseScadenza BETWEEN 1 AND 12),
    CHECK (AnnoScadenza >= 2024),
    CHECK (PAN BETWEEN 1111111111111111 AND 9999999999999999),
    CHECK (CVV BETWEEN 1 AND 999)
) ENGINE =InnoDB;

-- --------------------------------------------------
-- Tabella Connessione
-- --------------------------------------------------
DROP TABLE IF EXISTS Connessione;
CREATE TABLE IF NOT EXISTS Connessione (
	Utente INT NOT NULL,
    IP BIGINT NOT NULL,
    Inizio TIMESTAMP NOT NULL,
    Stato VARCHAR(50) NOT NULL,
    Hardware VARCHAR(50) NOT NULL,
    Fine TIMESTAMP NULL,
    PRIMARY KEY (Utente, IP, Inizio, Stato, Hardware),
    FOREIGN KEY (Utente) REFERENCES Utente (ID),
    CHECK (IP BETWEEN 1 AND 255255255255)
) ENGINE =InnoDB;

-- --------------------------------------------------
-- Tabella Visualizzazione
-- --------------------------------------------------
DROP TABLE IF EXISTS Visualizzazione;
CREATE TABLE IF NOT EXISTS Visualizzazione (
	Utente INT NOT NULL,
    IP BIGINT NOT NULL,
    Inizio TIMESTAMP NOT NULL,
    Stato VARCHAR(50) NOT NULL,
    Hardware VARCHAR(50) NOT NULL,
    Contenuto INT NOT NULL,
    Accesso TIMESTAMP NOT NULL,
    PRIMARY KEY (Utente, IP, Inizio, Stato, Hardware, Contenuto),
    FOREIGN KEY (Utente) REFERENCES Connessione (Utente)
    ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (IP) REFERENCES Connessione (IP)
    ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (Stato) REFERENCES Connessione (Stato)
    ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (Hardware) REFERENCES Connessione (Hardware)
    ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (Contenuto) REFERENCES Contenuto (ID)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE =InnoDB;