-- --------------------------------------------------
-- Operazioni Capitolo 4
-- --------------------------------------------------


-- --------------------------------------------------
-- Operazione 4.2.1  
-- FilmDisponibili di un Utente
-- --------------------------------------------------
DROP PROCEDURE IF EXISTS filmdisponibili_abbonamento;
DELIMITER $$
CREATE PROCEDURE filmdisponibili_abbonamento(IN _utenteid INT)
BEGIN
DECLARE risultato INT DEFAULT NULL;

-- si cerca se l'utente sia effettivamente attualmente abbonato
SET risultato = (
SELECT A.Tipo
FROM Utente U LEFT OUTER JOIN Abbonamento A ON U.Abbonamento = A.Tipo
WHERE U.ID = _utenteid);

-- se non lo è, si termina l'operazione 
IF risultato IS NULL THEN 
	SIGNAL SQLSTATE '45000'
	SET MESSAGE_TEXT = 'Utente non iscritto ad alcun Abbonamento';
    
-- se lo è, si cerca il numero di FilmDisponibili
ELSEIF risultato IS NOT NULL THEN
SELECT A1.FilmDisponibili
FROM Abbonamento A1 
WHERE A1.Tipo = risultato;
END IF;
END $$
DELIMITER ;

call filmdisponibili_abbonamento(1);


-- --------------------------------------------------
-- Operazione 4.2.2
-- Creazione Fattura e Pagamento con Carta di Credito
-- --------------------------------------------------
DROP PROCEDURE IF EXISTS fattura_pagamento;
DELIMITER $$
CREATE PROCEDURE fattura_pagamento(IN _utente INT,
								   IN _PAN BIGINT,
								   IN _CVV INT,
                                   IN _mese INT,
                                   IN _anno INT,
                                   IN _nome VARCHAR(50),
                                   IN _cognome VARCHAR(50),
                                   OUT esito tinyint)
BEGIN
DECLARE abbonamento VARCHAR(50) DEFAULT '';
DECLARE tariffa VARCHAR(50) DEFAULT '';
DECLARE durata VARCHAR(50) DEFAULT '';

-- si controlla che la carta non sia scaduta
IF _anno < YEAR(CURRENT_DATE) THEN 
	SIGNAL SQLSTATE '45000'
	SET MESSAGE_TEXT = 'Inserimento errato';
ELSEIF _anno = YEAR(CURRENT_DATE) AND Mese < MONTH(CURRENT_DATE) THEN
	SIGNAL SQLSTATE '45000'
	SET MESSAGE_TEXT = 'Inserimento errato';
END IF;

SET abbonamento = (SELECT U.Abbonamento
				   FROM Utente U 
				   WHERE U.ID = _utente);

IF abbonamento = '' THEN 
	SIGNAL SQLSTATE '45000'
	SET MESSAGE_TEXT = 'Utente non abbonato';
END IF;

SET tariffa = (SELECT A.Tariffa
			   FROM Utente U INNER JOIN Abbonamento A ON A.Tipo = U.Abbonamento
               WHERE U.ID = _utente);
               
SET durata = (SELECT A.Durata
			   FROM Utente U INNER JOIN Abbonamento A ON A.Tipo = U.Abbonamento
               WHERE U.ID = _utente);
               
INSERT INTO CartaDiCredito (PAN, CVV, MeseScadenza, AnnoScadenza, NomeIntestatario, CognomeIntestatario, Utente)
VALUES (_PAN, _CVV, _mese, _anno, _nome, _cognome, _utente);

INSERT INTO Fattura (DataEmissione, Quota, DataScadenza, DataPagamento, Utente, CartaPAN, CartaCVV, CartaMese, CartaAnno)
VALUES (CURRENT_DATE, tariffa, CURRENT_DATE + INTERVAL durata DAY, CURRENT_DATE, _utente, _PAN, _CVV, _mese, _anno);

SET esito = TRUE;

END $$
DELIMITER ;


-- --------------------------------------------------
-- Operazione 4.2.3
-- Lista Film con Artisti premiati
-- --------------------------------------------------
DROP PROCEDURE IF EXISTS lista_filmpremiati;
DELIMITER $$
CREATE PROCEDURE lista_filmpremiati()
BEGIN

-- si cercano i titoli di Film i cui Artisti abbiano vinto almeno un premio
SELECT F.Titolo
FROM Film F
WHERE F.ID IN (SELECT R.Film
               FROM Regia R INNER JOIN PremiazioneRegista PR ON R.Regista = PR.Regista)
OR F.ID IN (SELECT I.Film
			FROM Interpretazione I INNER JOIN PremiazioneAttore PA ON I.Attore = PA.Attore);
END $$
DELIMITER ;

call lista_filmpremiati();


-- --------------------------------------------------
-- Operazione 4.2.4
-- Film in Lingua Specifica
-- --------------------------------------------------
DROP PROCEDURE IF EXISTS film_inlingua;
DELIMITER $$
CREATE PROCEDURE film_inlingua(IN _lingua VARCHAR(50),
							   IN _film INT,
                               OUT esito tinyint)
BEGIN
DECLARE esistenza INT DEFAULT NULL;

-- si cerca se esiste almeno un contenuto doppiato o sottotitolato nella lingua cercata
SET esistenza = (
SELECT C.ID
FROM Contenuto C
WHERE (C.ID IN (SELECT D.ID
			   FROM Doppiaggio D
			   WHERE D.Lingua = _lingua)
OR C.ID IN (SELECT S.ID
			FROM Sottotitolaggio S
			WHERE S.Lingua = _lingua)
)
AND C.Film = _film
LIMIT 1
);

-- in base all'esito della query precedente, si imposta il tinyint dell'esito dell'operazione
IF esistenza IS NOT NULL THEN
SET esito = TRUE;
ELSEIF esistenza IS NULL THEN
SET esito = FALSE;
END IF;

END $$
DELIMITER ;

call film_inlingua('francese', 2, @esito);
SELECT @esito;


-- --------------------------------------------------
-- Operazione 4.2.5
-- Rating Assoluto
-- --------------------------------------------------
DROP PROCEDURE IF EXISTS rating_assoluto;
DELIMITER $$
CREATE PROCEDURE rating_assoluto(IN _film INT)
BEGIN 

-- quest'operazione è stata notevolmente semplificata grazie alla ridondanza RatingAssoluto, il cui aggiornamento è particolarmente significativo
SELECT F.RatingAssoluto
FROM Film F
WHERE F.ID = _film;

END $$
DELIMITER ;

call rating_assoluto(5);


-- --------------------------------------------------
-- Operazione 4.2.6
-- Rating Relativo
-- --------------------------------------------------
DROP PROCEDURE IF EXISTS rating_relativo;
DELIMITER $$
CREATE PROCEDURE rating_relativo(IN _film INT,
								 IN _percrating INT,
								 IN _genere1 INT,
								 IN _genere2 INT,
                                 IN _percgeneri INT,
                                 IN _paese1 VARCHAR(50),
                                 IN _paese2 VARCHAR(50),
                                 IN _paese3 VARCHAR(50),
                                 IN _paese4 VARCHAR(50),
                                 IN _paese5 VARCHAR(50),
                                 IN _percpaese INT,
                                 IN _anno1 INT,
                                 IN _anno2 INT,
                                 IN _percanno INT
                                 )
BEGIN
DECLARE coefficiente_ra FLOAT DEFAULT 0;
DECLARE coefficiente_ge FLOAT DEFAULT 0;
DECLARE coefficiente_pa FLOAT DEFAULT 0;
DECLARE coefficiente_an FLOAT DEFAULT 0;
DECLARE rating FLOAT DEFAULT NULL;
DECLARE anno INT DEFAULT 0;
DECLARE paese VARCHAR(50) DEFAULT '';
DECLARE finito INT DEFAULT 0;
DECLARE genere INT DEFAULT 0;
DECLARE ratingrelativo INT DEFAULT 0;

DECLARE cursore CURSOR FOR 
	SELECT T.Genere
	FROM Tipologia T
    WHERE T.Film = _film;
    
DECLARE CONTINUE HANDLER FOR NOT FOUND SET finito = 1;

-- controllo percentuali inserite
IF (_percrating + _percgeneri + _percpaese + _percanno) <> 100 THEN
	SIGNAL SQLSTATE '45000'
	SET MESSAGE_TEXT = 'Valori di percentuale errati';
END IF;
    
-- controllo presenza RatingAssoluto per il film in questione
SET rating =
(SELECT F.RatingAssoluto
FROM Film F
WHERE F.ID = _film);

IF rating IS NULL THEN
	SIGNAL SQLSTATE '45000'
	SET MESSAGE_TEXT = 'Rating Assoluto non ancora disponibile';
END IF;

-- si imposta il coefficiente per il rating
SET coefficiente_ra = rating * (_percrating/100);

SET anno = 
(SELECT F.AnnoProduzione
 FROM Film F 
 WHERE F.ID = _film);
 
 -- si imposta il coefficiente per l'anno
 IF anno BETWEEN _anno1 AND _anno2 THEN
 SET coefficiente_an = _percanno/10;
 ELSE SET coefficiente_an = 0;
 END IF;

SET paese = 
(SELECT F.PaeseProduzione
 FROM Film F 
 WHERE F.ID = _film);
 
 -- si imposta il coefficiente per il paese
IF (paese = _paese1 OR paese = _paese2 OR paese = _paese3 OR paese = _paese4 OR paese = _paese5) THEN
SET coefficiente_pa = _percpaese/10;
ELSE SET coefficiente_pa = 0;
END IF;

-- si usano un cursore ed un loop per confrontare i generi (al massimo 2)
OPEN cursore;

preleva: LOOP
	FETCH cursore INTO genere;
    IF finito = 1 THEN
    LEAVE preleva;
    END IF;
    IF genere = _genere1 OR genere = _genere2 THEN
    SET coefficiente_ge = 1;
    END IF;
END LOOP preleva;
CLOSE cursore;

-- si imposta il coefficiente dei generi
SET coefficiente_ge = coefficiente_ge * (_percgeneri/10);

-- si sommano i risultati
SET ratingrelativo = coefficiente_ra + coefficiente_ge + coefficiente_an + coefficiente_pa;
SELECT ratingrelativo;

END $$
DELIMITER ;

call rating_relativo(3, 25, 4, 5, 25, 'Italia', 'Germania', 'Francia', 'USA', 'Giappone', 25, 2000, 2030, 25);


-- --------------------------------------------------
-- Operazione 4.2.7
-- Raaccomandazione dei Contenuti
-- --------------------------------------------------
DROP PROCEDURE IF EXISTS raccomandazione_contenuti;
DELIMITER $$
CREATE PROCEDURE raccomandazione_contenuti(IN _utente INT)
BEGIN

-- si parte dai film visti dall'utente
WITH FilmVisti AS 
(SELECT DISTINCT(F.ID) AS ID
FROM Visualizzazione V INNER JOIN Contenuto C ON V.Contenuto = C.ID
INNER JOIN Film F ON C.Film = F.ID
WHERE V.Utente = _utente),

-- tramite FilmVisti si trovano i 10 piu' visti
TopTenFilm AS 
(SELECT FV.ID AS ID
FROM FilmVisti FV INNER JOIN Contenuto C ON C.Film = FV.ID
INNER JOIN Visualizzazioni V ON V.Contenuto = C.ID
WHERE V.Utente = _utente
GROUP BY FV.ID
ORDER BY COUNT(*) DESC
LIMIT 10),

-- si trova il genere preferito tra i piu' visti
GenereTop AS 
(SELECT T.Genere AS Genere
 FROM TopTenFilm TTF INNER JOIN Tipologia T ON TTF.ID = T.Film
 GROUP BY T.Genere
 HAVING COUNT(*) >= ALL (SELECT COUNT(*)
						 FROM TopTenFilm TTF INNER JOIN Tipologia T ON TTF.ID = T.Film
						 GROUP BY T.Genere)
LIMIT 1
), 

-- si trova lo stato piu' comune delle visualizzazioni per capire se vi sono restrizioni
StatoComune AS (
SELECT C.Stato AS Stato
FROM Connessione C
WHERE C.Utente = _utente
GROUP BY C.Stato
HAVING COUNT(*) >= ALL (SELECT COUNT(*)
						FROM Connessione C1
						WHERE C1.Utente = _utente
						GROUP BY C1.Stato)
LIMIT 1),

-- con i film visti si trovano di conseguenza i mai visti
FilmMaiVisti AS 
(SELECT F.ID AS ID
 FROM Film F LEFT OUTER JOIN FilmVisti FV ON F.ID = FV.ID
 WHERE FV.ID IS NULL
),

-- grazie allo stato comune e ai film mai visti si trovano quelli consigliabili
FilmNonRistretti AS 
(SELECT FMV.ID AS ID
 FROM FilmMaiVisti FMV INNER JOIN Contenuto C ON FMV.ID = C.Film
 LEFT OUTER JOIN (Restrizione INNER JOIN StatoComune SC ON SC.Stato = R.AreaGeografica) ON R.Contenuto = C.ID
 WHERE R.Contenuto IS NULL)
 
 -- l'ultima query provvede a trovare i piu' affini all'utente
 SELECT FNR.ID AS ID
 FROM FilmNonRistretti FNR INNER JOIN Tipologia T ON FNR.ID = T.Film 
 INNER JOIN GenereTop GT ON T.Genere = GT.Genere;
 
END $$
DELIMITER ;

call raccomandazione_contenuti(7);


-- --------------------------------------------------
-- Operazione 4.2.8
-- Registrazione Utente ed Iscrizione in base a FilmDisponibili
-- --------------------------------------------------
DROP PROCEDURE IF EXISTS registrazione_filmdisponibili;
DELIMITER $$
CREATE PROCEDURE registrazione_filmdisponibili (IN _utentenome VARCHAR(50),
												IN _utentecognome VARCHAR(50),
                                                IN _utentenickname VARCHAR(50),
                                                IN _utenteemail VARCHAR(100),
                                                IN _utentepassword VARCHAR(20),
                                                IN _utentedatanascita DATE,
                                                IN _connessioneIP BIGINT,
                                                IN _connessionehardware VARCHAR(50),
                                                IN _connessionestato VARCHAR(50),
												IN _n INT)
BEGIN
DECLARE abbonamento VARCHAR(50) DEFAULT NULL;
DECLARE filmtotali INT DEFAULT 0;
DECLARE utenteid INT DEFAULT 0;

-- si controlla se n è coerente col numero di film totali
SET filmtotali = (SELECT COUNT(*)
				  FROM Film);

IF _n > filmtotali THEN
	SIGNAL SQLSTATE '45000'
	SET MESSAGE_TEXT = 'Valore di n errato';
END IF;

-- si cerca l'abbonamento più economico che rispetti la condizione
SET abbonamento = 
(SELECT A.Tipo
FROM Abbonamento A
WHERE A.FilmDisponibili >= _n
AND A.Tariffa = (SELECT MIN(A2.Tariffa)
				 FROM Abbonamento A2
				 WHERE A2.FilmDisponibili >= n));

-- si inseriscono Utente e Connessione nel database
INSERT INTO Utente (Nome, Cognome, Nickname, Email, Password, DataNascita, Abbonamento, DataInizioAbbonamento)
VALUES (_utentenome, _utentecognome, _utentenickname, _utenteemail, _utentepassword, _utentedatanascita, abbonamento, CURRENT_DATE);

SET utenteid = (SELECT ID
				FROM Utente
				WHERE Nome = _utentenome
                AND Cognome = _utentecognome
                AND Nickname = _utentenickname
                AND Email = _utenteemail
                AND Password = _utentepassword
                AND DataNascita = _utentedatanascita);

INSERT INTO Connessione (Utente, IP, Inizio, Stato, Hardware)
VALUES (utenteid, _connessioneIP, current_timestamp, _connessionestato, _connessionehardware);
END $$
DELIMITER ;

call registrazione_filmdisponibili('Alex', 'Mongelluzzi', 'AlexMonge14', 'AlexMonge@FilmSphere.com', 'dsgbhsnaSJNS', '2003/14/04', 1112678, 'Iphone15Pro', 'Italia', 30);


-- --------------------------------------------------
-- Operazione 4.2.9  -  Analytics 
-- Classifiche
-- --------------------------------------------------            
DROP PROCEDURE IF EXISTS classifiche;
DELIMITER $$
CREATE PROCEDURE classifiche(IN _stato VARCHAR(50),
							 IN _abbonamento VARCHAR(50))
BEGIN 

-- si conteggiano le visualizzazioni in base a Film e Formato
WITH Conteggio AS (
SELECT COUNT(*) as Visualizzazioni, C.Film, C.Formato
FROM Utente U INNER JOIN Visualizzazione V ON U.ID = V.Utente
INNER JOIN Contenuto C ON V.Contenuto = C.ID
WHERE V.Stato = _stato 
AND U.Abbonamento = _abbonamento
GROUP BY C.Film, C.Formato)

-- si stila una classifica relativo ai risultati della CTE
SELECT Con.Visualizzioni, Con.Film, Con.Formato
FROM Conteggio Con
ORDER BY Con.Visualizzazioni DESC
LIMIT 100;

END $$
DELIMITER ;

CALL classifiche('Italia', 'Basic');


-- --------------------------------------------------
-- Operazione 4.2.10  -  Custom Analytics 
-- Utenti longevi abbonati
-- --------------------------------------------------  
DROP PROCEDURE IF EXISTS utenti_longevi;
DELIMITER $$
CREATE PROCEDURE utenti_longevi(IN _n INT)
BEGIN 

-- Si cercano le prime connessioni di registrazione di n utenti
WITH ConnessioniVecchie AS
(SELECT *
FROM Connessione C RIGHT OUTER JOIN Connessione C1 ON 
(C.Utente = C1.Utente
AND C.Inizio < C1.Inizio)
WHERE C.Utente IS NULL
ORDER BY C1.Inizio
LIMIT _n)

-- Si contano quelli attualmente abbonati
SELECT COUNT(*)
FROM ConnessioniVecchie CV INNER JOIN Utente U ON U.ID = CV.Utente
WHERE U.Abbonamento IS NOT NULL;
END $$
DELIMITER ;

call utenti_longevi(10);
