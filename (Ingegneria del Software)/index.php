<?php

use classi\GestioneCdL;
use classi\InterfacciaGrafica;

    require_once "lib/fpdf184/fpdf.php";
    require_once "lib/PHPMailer/src/PHPMailer.php";
    require_once "lib/PHPMailer/src/Exception.php";
    require_once "lib/PHPMailer/src/SMTP.php";
    require_once "classi/GestioneCdL.php";
    require_once "classi/GestioneCarrieraStudente.php";
    require_once "classi/Esame.php";
    require_once "classi/Laureando.php";
    require_once "classi/LaureandoIngegneriaInformatica.php";
    require_once "classi/SimulazioneVoto.php";
    require_once "classi/ProspettoPDFLaureando.php";
    require_once "classi/ProspettoPDFCommissione.php";
    require_once "classi/InterfacciaGrafica.php";
    require_once "classi/InviaProspetti.php";

    $boolean = false;

    if (isset($_POST['crea']) && $_POST['data'] != "" && $_POST['cdl'] != "" && $_POST['matricole'] != "")
    {
        $oggetto = new InterfacciaGrafica($_POST['data'], $_POST['cdl'], $_POST['matricole']);
        $oggetto->creaProspetti();
        $boolean = true;
    }

    if (isset($_POST['apri']) && $_POST['cdl'] != "")
    {
        $oggetto = new InterfacciaGrafica($_POST['data'], $_POST['cdl'], $_POST['matricole']);
        $oggetto->accediProspetti();
    }

    if (isset($_POST['invia']) && $_POST['cdl'] != "" && isset($_POST['matricole']))
    {
        $oggetto = new InterfacciaGrafica($_POST['data'], $_POST['cdl'], $_POST['matricole']);
        $oggetto->inviaProspetti();
    }

?>

<!DOCTYPE HTML>
<html lang="it">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="author" content="Alex Mongelluzzi">
        <meta name="description" content="Portale per l'organizzazione di appelli di laurea">
        <link rel="stylesheet" href="css/style.css">
        <title>Laureandosi 2.0</title>
        <script>
            function aggiornaStatus(emailInviate, maxEmail, boolean) {
                const p = document.getElementById("loadinginvio");
                if(boolean) {
                    p.innerText = "MAIL " + emailInviate + "/" + maxEmail + ": INVIO RIUSCITO";
                }
                else {
                    p.innerText = "MAIL " + emailInviate + "/" + maxEmail + ": ERRORE, INVIO FALLITO";
                }
            }
        </script>
    </head>
    <body>
        <div id="container">
            <h1>Gestione Prospetti di Laurea</h1>
            <form action="index.php" method="POST">
                <fieldset>
                    <div class="colonna">
                        <label for="cdl">CdL:</label>
                        <select class="data" name="cdl" id="cdl">
                            <option value="">Seleziona il CdL</option>

                            <?php

                                $data = GestioneCdL::restituisciConfigurazioneCdL();
                                foreach($data as $cdl) {
                                    echo '<option value="';
                                    echo $cdl["cdl"];
                                    echo '">';
                                    echo $cdl["cdl-alt"];
                                    echo'</option>';
                                }

                            ?>

                        </select>
                        <label for="data" id="datalaurea">Data Laurea:</label>
                        <input class="data" type="date" name="data" id="data">
                    </div>
                    <div class="colonna">
                        <label for="matricole">Matricole:</label>
                        <textarea name="matricole" id="matricole"></textarea>
                    </div>
                    <div class="colonna" id="dx">
                        <input type="submit" name="crea" value="Crea Prospetti" class="button" id="crea">
                        <input type="submit" name="apri" value="Apri prospetti" id="apri">
                        <input type="submit" name="invia" value="Invia Prospetti" class="button" id="invia">
                        <p id="mostrascritta">
                            <?php

                                if($boolean)
                                    echo 'Prospetti Creati';

                            ?>
                        </p>
                        <a href="/test/UnitTests.php">UnitTests</a>
                    </div>
                </fieldset>
            </form>
            <p id="loadinginvio">
            </p>
        </div>
    </body>
</html>
