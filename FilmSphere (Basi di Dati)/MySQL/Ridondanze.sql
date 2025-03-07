-- --------------------------------------------------
-- Ridondanze
-- --------------------------------------------------


-- --------------------------------------------------
-- Rilevanza
-- --------------------------------------------------
DROP TRIGGER IF EXISTS aggiorna_rilevanza_premattore;
DELIMITER $$
CREATE TRIGGER aggiorna_rilevanza_premattore
AFTER INSERT ON PremiazioneAttore
FOR EACH ROW
BEGIN 
DECLARE numerofilm INT DEFAULT 0;
DECLARE valorenumero FLOAT DEFAULT 0;
DECLARE prestigiopremivinti INT DEFAULT 0;
DECLARE sommapremivinti INT DEFAULT 0;
DECLARE valorepremi FLOAT DEFAULT 0;
DECLARE votofinale FLOAT DEFAULT 0;

SET numerofilm = (SELECT COUNT(*)
				  FROM Interpretazione I 
                  WHERE I.Attore = NEW.Attore);

SET prestigiopremivinti = (SELECT SUM(Prestigio)
						   FROM PremiazioneAttore PA INNER JOIN Premio P ON PA.TitoloPremio = P.Titolo AND PA.CategoriaPremio = P.Categoria
						   WHERE PA.Attore = NEW.Attore);

IF numerofilm BETWEEN 1 AND 10 THEN
	SET valorefilm = 1;
ELSEIF numerofilm BETWEEN 11 AND 20 THEN
	SET valorefilm = 2;
ELSEIF numerofilm BETWEEN 21 AND 39 THEN
	SET valorefilm = 2.5;
ELSEIF numerofilm >= 40 THEN
	SET valorefilm = 3;
END IF;

IF prestigiopremivinti < 20 THEN
	SET valorepremi = prestigiopremivinti/10;
ELSEIF prestigiopremivinti >= 20 THEN
	SET valorepremi = 2;
END IF;
    
SET votofinale = valorefilm + valorepremi;

UPDATE Artista 
SET Rilevanza = votofinale
WHERE ID = NEW.Attore;

END $$
DELIMITER ;


DROP TRIGGER IF EXISTS aggiorna_rilevanza_premregista;
DELIMITER $$
CREATE TRIGGER aggiorna_rilevanza_premregista
AFTER INSERT ON PremiazioneRegista
FOR EACH ROW
BEGIN 
DECLARE numerofilm INT DEFAULT 0;
DECLARE valorenumero FLOAT DEFAULT 0;
DECLARE prestigiopremivinti INT DEFAULT 0;
DECLARE sommapremivinti INT DEFAULT 0;
DECLARE valorepremi FLOAT DEFAULT 0;
DECLARE votofinale FLOAT DEFAULT 0;

SET numerofilm = (SELECT COUNT(*)
				  FROM Regia R
                  WHERE R.Regista = NEW.Regista);

SET prestigiopremivinti = (SELECT SUM(Prestigio)
						   FROM PremiazioneRegistaPR INNER JOIN Premio P ON PR.TitoloPremio = P.Titolo AND PR.CategoriaPremio = P.Categoria
						   WHERE PR.Regista = NEW.Regista);

IF numerofilm BETWEEN 1 AND 4 THEN
	SET valorefilm = 1;
ELSEIF numerofilm BETWEEN 5 AND 9 THEN
	SET valorefilm = 2;
ELSEIF numerofilm BETWEEN 10 AND 14 THEN
	SET valorefilm = 2.5;
ELSEIF numerofilm >= 15 THEN
	SET valorefilm = 3;
END IF;

IF prestigiopremivinti < 20 THEN
	SET valorepremi = prestigiopremivinti/10;
ELSEIF prestigiopremivinti >= 20 THEN
	SET valorepremi = 2;
END IF;
    
SET votofinale = valorefilm + valorepremi;

UPDATE Artista 
SET Rilevanza = votofinale
WHERE ID = NEW.Regista;

END $$
DELIMITER ;


DROP TRIGGER IF EXISTS aggiorna_rilevanza_inter;
DELIMITER $$
CREATE TRIGGER aggiorna_rilevanza_inter
AFTER INSERT ON Interpretazione
FOR EACH ROW
BEGIN
DECLARE numerofilm INT DEFAULT 0;
DECLARE valorenumero FLOAT DEFAULT 0;
DECLARE prestigiopremivinti INT DEFAULT 0;
DECLARE sommapremivinti INT DEFAULT 0;
DECLARE valorepremi FLOAT DEFAULT 0;
DECLARE votofinale FLOAT DEFAULT 0;

SET numerofilm = (SELECT COUNT(*)
				  FROM Interpretazione I 
                  WHERE I.Attore = NEW.Attore);

SET prestigiopremivinti = (SELECT SUM(Prestigio)
						   FROM PremiazioneAttore PA INNER JOIN Premio P ON PA.TitoloPremio = P.Titolo AND PA.CategoriaPremio = P.Categoria
						   WHERE PA.Attore = NEW.Attore);
                           
IF numerofilm BETWEEN 1 AND 10 THEN
	SET valorefilm = 1;
ELSEIF numerofilm BETWEEN 11 AND 20 THEN
	SET valorefilm = 2;
ELSEIF numerofilm BETWEEN 21 AND 39 THEN
	SET valorefilm = 2.5;
ELSEIF numerofilm >= 40 THEN
	SET valorefilm = 3;
END IF;

IF prestigiopremivinti < 20 THEN
	SET valorepremi = prestigiopremivinti/10;
ELSEIF prestigiopremivinti >= 20 THEN
	SET valorepremi = 2;
END IF;
    
SET votofinale = valorefilm + valorepremi;

UPDATE Artista 
SET Rilevanza = votofinale
WHERE ID = NEW.Attore;

END $$
DELIMITER ;


DROP TRIGGER IF EXISTS aggiorna_rilevanza_regia;
DELIMITER $$
CREATE TRIGGER aggiorna_rilevanza_regia
AFTER INSERT ON Regia
FOR EACH ROW
BEGIN 
DECLARE numerofilm INT DEFAULT 0;
DECLARE valorenumero FLOAT DEFAULT 0;
DECLARE prestigiopremivinti INT DEFAULT 0;
DECLARE sommapremivinti INT DEFAULT 0;
DECLARE valorepremi FLOAT DEFAULT 0;
DECLARE votofinale FLOAT DEFAULT 0;

SET numerofilm = (SELECT COUNT(*)
				  FROM Regia R
                  WHERE R.Regista = NEW.Regista);

SET prestigiopremivinti = (SELECT SUM(Prestigio)
						   FROM PremiazioneRegistaPR INNER JOIN Premio P ON PR.TitoloPremio = P.Titolo AND PR.CategoriaPremio = P.Categoria
						   WHERE PR.Regista = NEW.Regista);

IF numerofilm BETWEEN 1 AND 4 THEN
	SET valorefilm = 1;
ELSEIF numerofilm BETWEEN 5 AND 9 THEN
	SET valorefilm = 2;
ELSEIF numerofilm BETWEEN 10 AND 14 THEN
	SET valorefilm = 2.5;
ELSEIF numerofilm >= 15 THEN
	SET valorefilm = 3;
END IF;

IF prestigiopremivinti < 20 THEN
	SET valorepremi = prestigiopremivinti/10;
ELSEIF prestigiopremivinti >= 20 THEN
	SET valorepremi = 2;
END IF;
    
SET votofinale = valorefilm + valorepremi;

UPDATE Artista 
SET Rilevanza = votofinale
WHERE ID = NEW.Regista;

END $$
DELIMITER ;



-- --------------------------------------------------
-- Reputazione
-- --------------------------------------------------
DROP TRIGGER IF EXISTS aggiorna_reputazione;
DELIMITER $$
CREATE TRIGGER aggiorna_reputazione
AFTER INSERT ON RecensioneCritico
FOR EACH ROW
BEGIN
DECLARE numerorecensioni INT DEFAULT 0;
DECLARE valorenumero INT DEFAULT 0;

SET numerorecensioni = (SELECT COUNT(*)
						FROM RecensioneCritico
                        WHERE Critico = NEW.Critico);
                        
IF numerorecensioni BETWEEN 1 AND 9 THEN
	SET valorenumero = 1;
ELSEIF numerorecensioni BETWEEN 10 AND 19 THEN
	SET valorenumero = 2;
ELSEIF numerorecensioni BETWEEN 20 AND 29 THEN
	SET valorenumero = 3;
ELSEIF numerorecensioni BETWEEN 30 AND 39 THEN
	SET valorenumero = 4;
ELSEIF numerorecensioni >= 40 THEN
	SET valorenumero = 5;
 END IF;
 
UPDATE Critico
SET Rilevanza = valorenumero
WHERE ID = NEW.Critico;

END $$
DELIMITER ;


-- --------------------------------------------------
-- FilmDisponibili
-- --------------------------------------------------
DROP TRIGGER IF EXISTS aggiorna_filmdisponibili;
DELIMITER $$
CREATE TRIGGER aggiorna_filmdisponibili
AFTER INSERT ON Film 
FOR EACH ROW
BEGIN
DECLARE filmtotali INT DEFAULT 0;
DECLARE finito INT DEFAULT 0;
DECLARE abbonamentotipo VARCHAR(50) DEFAULT '';
DECLARE filmesclusi INT DEFAULT 0;

DECLARE cursore CURSOR FOR
	SELECT D.E.Abbonamento, COUNT(*)
    FROM
	(SELECT DISTINCT(E.Abbonamento, T.Film)
    FROM Esclusione E INNER JOIN Tipologia T ON E.Genere = T.Genere) AS D
    GROUP BY D.E.Abbonamento;

DECLARE CONTINUE HANDLER FOR NOT FOUND SET finito = 1;

SET filmtotali = (SELECT COUNT(*)
				  FROM Film F);
                  
OPEN cursore;

ciclo: LOOP
FETCH cursore INTO abbonamentotipo, filmesclusi;
IF finito = 1 THEN
LEAVE ciclo;
END IF;

UPDATE Abbonamento A
SET A.FilmDisponibili = (filmtotali - filmesclusi)
WHERE A.Tipo = abbonamentotipo;

END LOOP ciclo;

CLOSE cursore;

END $$
DELIMITER ;


-- --------------------------------------------------
-- RatingAssoluto
-- --------------------------------------------------
DROP PROCEDURE IF EXISTS aggiornamento_ra;
DELIMITER $$
CREATE PROCEDURE aggiornamento_ra(IN _film INT)
BEGIN 
DECLARE sommacritico INT DEFAULT 0;
DECLARE numerocritico INT DEFAULT 0;
DECLARE sommautente INT DEFAULT 0;
DECLARE numeroutente INT DEFAULT 0;
DECLARE sommaregista INT DEFAULT 0;
DECLARE numeroregista INT DEFAULT 0;
DECLARE sommaregista INT DEFAULT 0;
DECLARE numeroregista INT DEFAULT 0;
DECLARE sommaattore INT DEFAULT 0;
DECLARE numeroattore INT DEFAULT 0;
DECLARE sommapremio INT DEFAULT 0;
DECLARE numeropremio INT DEFAULT 0;
DECLARE coefficiente_cr FLOAT DEFAULT 0;
DECLARE coefficiente_ut FLOAT DEFAULT 0;
DECLARE coefficiente_rg FLOAT DEFAULT 0;
DECLARE coefficiente_at FLOAT DEFAULT 0;
DECLARE coefficiente_pr FLOAT DEFAULT 0;
DECLARE totale FLOAT DEFAULT 0;

SET sommacritico = (SELECT SUM(RC.Voto * C.Reputazione)
				    FROM RecensioneCritico RC INNER JOIN Critico C ON RC.Critico = C.ID
                    WHERE RC.Film = _film);
                    
SET numerocritico = (SELECT COUNT(*)
					 FROM RecensioneUtente RC
                     WHERE RC.Film = _film);

SET sommautente = (SELECT SUM(RU.Voto)
				   FROM RecensioneUtente RU
                   WHERE RU.Film = _film);
                   
SET numeroutente = (SELECT COUNT(*)
					FROM RecensioneUtente RU
                    WHERE RU.Film = _film);
                    
SET sommaregista = (SELECT SUM(A.Rilevanza)
					FROM Regia R INNER JOIN Artista A ON R.Regista = A.ID
                    WHERE R.Film = _film);
                    
SET numeroregista = (SELECT COUNT(*)
				     FROM Regia R 
					 WHERE R.Film = _Film);
                     
SET sommaattore = (SELECT SUM(A.Rilevanza)
				   FROM Interpretazione I INNER JOIN Artista A ON I.Attore = A.ID
				   WHERE I.Film = _film);
                    
SET numeroattore = (SELECT COUNT(*)
					FROM Interpretazione I 
					WHERE I.Film = _Film);
                    
SET sommapremio = (SELECT SUM(P.Prestigio)
				   FROM PremiazioneFilm PF INNER JOIN Premio P ON PF.CategoriaPremio = P.Categoria AND PF.TitoloPremio = P.Titolo
                   WHERE PF.Film = _film);
                   
SET numeropremio = (SELECT COUNT(*)
					FROM PremiazioneFilm PF
                    WHERE PF.Film = _film);
                    
SET coefficiente_cr = sommacritico/(numerocritico * 2);
SET coefficiente_ut = (sommautente/numeroutente) * 1.5;
SET coefficiente_rg = (sommaregista/numeroregista) * 4;
SET coefficiente_at = (sommaattore/numeroattore) * 4;
SET coefficiente_pr = (sommapremio/numeropremio) * 4;

SET totale = (coefficiente_cr + coefficiente_ut + coefficiente_rg + coefficiente_at + coefficiente_pr) / 10;

UPDATE Film
SET RatingAssoluto = totale
WHERE ID = _film;

END $$
DELIMITER ;

DROP PROCEDURE IF EXISTS aggiorna_ratingassoluto;
DELIMITER $$
CREATE PROCEDURE aggiorna_ratingassoluto()
BEGIN 
DECLARE filmid INT DEFAULT 0;
DECLARE finito INT DEFAULT 0;
DECLARE cursore CURSOR FOR
	SELECT F.ID
    FROM Film F;
    
DECLARE CONTINUE HANDLER FOR NOT FOUND SET finito = 1;

OPEN cursore;

aggiorna: LOOP
FETCH cursore INTO filmid;
IF finito = 1 THEN
LEAVE aggiorna;
END IF;

call aggiornamento_ra(filmid);

END LOOP;

CLOSE cursore;
END $$
DELIMITER ;

DROP EVENT IF EXISTS evento_ratingassoluto;
CREATE EVENT evento_ratingassoluto
ON SCHEDULE EVERY 1 DAY
STARTS '2024-02-09' '23:55:55'
DO
call aggiorna_ratingassoluto();
