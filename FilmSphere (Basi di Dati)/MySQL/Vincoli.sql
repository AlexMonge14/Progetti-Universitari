-- --------------------------------------------------
-- Popolamento
-- --------------------------------------------------


-- --------------------------------------------------
-- Controllo Ruolo Artista
-- --------------------------------------------------
DROP TRIGGER IF EXISTS controllo_ruoloartista;
DELIMITER $$
CREATE TRIGGER controllo_ruoloartista
BEFORE INSERT ON Artista
FOR EACH ROW
BEGIN

IF NEW.Ruolo <> 'Attore' OR NEW.Ruolo <> 'Regista' THEN
	SIGNAL SQLSTATE '45000'
	SET MESSAGE_TEXT = 'Inserimento errato';
END IF;

END $$
DELIMITER ;


DROP TRIGGER IF EXISTS controllo_ruoloregia;
DELIMITER $$
CREATE TRIGGER controllo_ruoloregia
BEFORE INSERT ON Regia
FOR EACH ROW
BEGIN
DECLARE ruolo VARCHAR(50) DEFAULT '';

SET ruolo = (SELECT A.Ruolo
			 FROM Artista A
			 WHERE A.ID = NEW.Regista);
             
IF ruolo <> 'Regista' THEN
	SIGNAL SQLSTATE '45000'
	SET MESSAGE_TEXT = 'Inserimento errato';
END IF;

END $$
DELIMITER ;


DROP TRIGGER IF EXISTS controllo_ruolointerpretazione;
DELIMITER $$
CREATE TRIGGER controllo_ruolointerpretazione
BEFORE INSERT ON Interpretazione
FOR EACH ROW
BEGIN

DECLARE ruolo VARCHAR(50) DEFAULT '';

SET ruolo = (SELECT A.Ruolo
			 FROM Artista A
			 WHERE A.ID = NEW.Attore);
             
IF ruolo <> 'Attore' THEN
	SIGNAL SQLSTATE '45000'
	SET MESSAGE_TEXT = 'Inserimento errato';
END IF;

END $$
DELIMITER ;


DROP TRIGGER IF EXISTS controllo_ruolopremiazioneregista;
DELIMITER $$
CREATE TRIGGER controllo_ruolopremiazioneregista
BEFORE INSERT ON PremiazioneRegista
FOR EACH ROW
BEGIN
DECLARE ruolo VARCHAR(50) DEFAULT '';

SET ruolo = (SELECT A.Ruolo
			 FROM Artista A
			 WHERE A.ID = NEW.Regista);
             
IF ruolo <> 'Regista' THEN
	SIGNAL SQLSTATE '45000'
	SET MESSAGE_TEXT = 'Inserimento errato';
END IF;

END $$
DELIMITER ;


DROP TRIGGER IF EXISTS controllo_ruolopremiazioneattore;
DELIMITER $$
CREATE TRIGGER controllo_ruolopremiazioneattore
BEFORE INSERT ON PremiazioneAttore
FOR EACH ROW
BEGIN
DECLARE ruolo VARCHAR(50) DEFAULT '';

SET ruolo = (SELECT A.Ruolo
			 FROM Artista A
			 WHERE A.ID = NEW.Attore);
             
IF ruolo <> 'Attore' THEN
	SIGNAL SQLSTATE '45000'
	SET MESSAGE_TEXT = 'Inserimento errato';
END IF;

END $$
DELIMITER ;

-- --------------------------------------------------
-- Inserimento premio errato
-- --------------------------------------------------
DROP TRIGGER IF EXISTS controllo_premio;
DELIMITER $$ 
CREATE TRIGGER controllo_premio
BEFORE INSERT ON Premio
FOR EACH ROW 
BEGIN 

IF NEW.Categoria <> 'Film' OR NEW.Categoria <> 'Attore' OR NEW.Categoria <> 'Regista' THEN
	SIGNAL SQLSTATE '45000'
	SET MESSAGE_TEXT = 'Inserimento errato';
END IF;
END $$
DELIMITER ;

-- --------------------------------------------------
-- Iscrizione ad Abbonamento con Blocco VM
-- --------------------------------------------------
DROP PROCEDURE IF EXISTS controllo_inserimentoabb;
DELIMITER $$
CREATE TRIGGER controllo_inserimentoabb
BEFORE INSERT ON Utente
FOR EACH ROW
BEGIN
DECLARE eta INT DEFAULT 0;
DECLARE vm INT DEFAULT NULL;

SET vm = (SELECT A.VM
		  FROM Abbonamento A
          WHERE NEW.Abbonamento = A.Tipo);

SET eta = (SELECT YEAR(CURRENT_DATE) - YEAR(NEW.DataNascita)
		   FROM Abbonamento A
           WHERE NEW.Abbonamento = A.Tipo);

IF vm IS NOT NULL AND eta < vm THEN 
	SIGNAL SQLSTATE '45000'
	SET MESSAGE_TEXT = 'Inserimento errato';
END IF;

END $$
DELIMITER ;


DROP PROCEDURE IF EXISTS controllo_aggiornamentoabb;
DELIMITER $$
CREATE TRIGGER controllo_aggiornamentoabb
BEFORE UPDATE ON Utente
FOR EACH ROW
BEGIN
DECLARE eta INT DEFAULT 0;
DECLARE vm INT DEFAULT NULL;

SET vm = (SELECT A.VM
		  FROM Abbonamento A
          WHERE NEW.Abbonamento = A.Tipo);

SET eta = (SELECT YEAR(CURRENT_DATE) - YEAR(NEW.DataNascita)
		   FROM Abbonamento A
           WHERE NEW.Abbonamento = A.Tipo);

IF vm IS NOT NULL AND eta < vm THEN 
	SIGNAL SQLSTATE '45000'
	SET MESSAGE_TEXT = 'Aggiornamento errato';
END IF;

END $$
DELIMITER ;


-- --------------------------------------------------
-- Controllo Password per Registrazione Utente
-- --------------------------------------------------
DROP TRIGGER IF EXISTS controllo_password;
DELIMITER $$
CREATE TRIGGER controllo_password
BEFORE INSERT ON Utente 
FOR EACH ROW
BEGIN

IF CHAR_LENGTH(NEW.Password) < 7 OR CHAR_LENGTH(NEW.Password) > 18 THEN 
	SIGNAL SQLSTATE '45000' 
	SET MESSAGE_TEXT = 'Lunghezza password errata, deve essere compresa tra i 7 ed i 18 caratteri';
END IF;

END $$
DELIMITER ;



-- --------------------------------------------------
-- Controllo Visualizzazione su Contenuti Ristretti
-- --------------------------------------------------
DROP TRIGGER IF EXISTS controllo_visualizzazioneristretta;
DELIMITER $$
CREATE TRIGGER controllo_visualizzazioneristretta
BEFORE INSERT ON Visualizzazione
FOR EACH ROW
BEGIN

IF EXISTS (SELECT * 
		   FROM Restrizione R
		   WHERE R.Contenuto = NEW.Contenuto 
		   AND R.Stato = NEW.Stato) THEN
	SIGNAL SQLSTATE '45000'
	SET MESSAGE_TEXT = 'inserimento errato';
END IF;
END $$
DELIMITER ;


-- --------------------------------------------------
-- Evento Creazione Fatture per nuovi Abbonamenti
-- --------------------------------------------------
DROP EVENT IF EXISTS evento_creazionefatture;
DELIMITER $$
CREATE EVENT evento_creazionefatture
ON SCHEDULE EVERY 2 DAY 
STARTS '2024-02-09' '00:00:00'
DO
BEGIN
DECLARE finito INT DEFAULT 0;
DECLARE utentefattura INT DEFAULT 0;
DECLARE abbonamentofattura INT DEFAULT 0;
DECLARE duratafattura INT DEFAULT 0;

DECLARE cursore CURSOR FOR
	SELECT U.ID, A.Tariffa, A.Durata
	FROM Utente U INNER JOIN Abbonamento A ON U.Abbonamento = A.Tipo
    WHERE U.DataInizio > CURRENT_DATE - INTERVAL 2 DAY;

DECLARE CONTINUE HANDLER FOR NOT FOUND SET finito = 1;

OPEN cursore;
ciclo: LOOP
FETCH cursore INTO utentefattura, abbonamentofattura, duratafattura;

IF finito = 1 THEN
LEAVE ciclo;
END IF;

INSERT INTO Fattura (DataEmissione, Quota, DataScadenza, DataPagamento, Utente, CartaPAN, CartaCVV, CartaMese, CartaAnno)
VALUES (CURRENT_DATE, abbonamentofattura, CURRENT_DATE + INTERVAL duratafattura DAY, NULL, utentefattura, NULL, NULL, NULL, NULL);
END LOOP;

CLOSE cursore;

END $$
DELIMITER ;


-- --------------------------------------------------
-- Evento Cancellazione Iscrizioni e Fatture
-- --------------------------------------------------
DROP EVENT IF EXISTS evento_cancellazioneiscrizione;
DELIMITER $$
CREATE EVENT evento_cancellazioneiscrizione
ON SCHEDULE EVERY 1 DAY 
STARTS '2024-02-09' '00:00:00'
DO
BEGIN
DECLARE utenteid INT DEFAULT 0;
DECLARE finito INT DEFAULT 0;

DECLARE cursore CURSOR FOR 
	SELECT U.ID
    FROM Utente U INNER JOIN Abbonamento A ON U.Abbonamento = A.Tipo
    WHERE U.DataInizioAbbonamento + INTERVAL A.Durata DAY;

DECLARE CONTINUE HANDLER FOR NOT FOUND SET finito = 1;
OPEN cursore;

ciclo: LOOP
FETCH cursore into utenteid;
IF finito = 1 THEN
LEAVE ciclo;
END IF;

UPDATE Utente
SET Abbonamento = NULL AND DataInizioAbbonamento = NULL
WHERE U.ID = utenteid;

DELETE FROM Fattura
WHERE F.Utente = utenteid;
END LOOP;

END $$
DELIMITER ;



-- --------------------------------------------------
-- Evento Cancellazione Connessioni 
-- --------------------------------------------------
DROP EVENT IF EXISTS evento_cancellazioneconnessioni
DELIMITER $$
CREATE EVENT evento_cancellazioneconnessioni
ON SCHEDULE EVERY 1 MONTH
STARTS '2024-02-09' '00:00:00'
DO
BEGIN

WITH ConnessioniVecchie AS
(SELECT C1.Inizio AS Inizio
FROM Connessione C RIGHT OUTER JOIN Connessione C1 ON 
(C.Utente = C1.Utente
AND C.Inizio < C1.Inizio)
WHERE C.Utente IS NULL)

DELETE FROM Connessione
WHERE C.Inizio NOT IN (SELECT Inizio
					   FROM ConnessioniVecchie);
END $$
DELIMITER ;
