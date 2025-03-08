<?php

    require_once __DIR__ . '/../classi/Laureando.php';
    require_once __DIR__ . '/../classi/GestioneCarrieraStudente.php';
    require_once __DIR__ . '/../classi/Esame.php';
    require_once __DIR__ . '/../classi/LaureandoIngegneriaInformatica.php';
    require_once __DIR__ . '/../classi/GestioneCdL.php';
    require_once __DIR__ . '/../classi/ProspettoPDFLaureando.php';
    require_once __DIR__ . '/../classi/ProspettoPDFCommissione.php';
    require_once __DIR__ . '/../classi/SimulazioneVoto.php';
    require_once __DIR__ . '/../classi/InviaProspetti.php';
    require_once __DIR__ . '/../classi/InterfacciaGrafica.php';
    require_once __DIR__ . '/../lib/PHPMailer/src/PHPMailer.php';
    require_once __DIR__ . '/../lib/PHPMailer/src/Exception.php';
    require_once __DIR__ . '/../lib/PHPMailer/src/SMTP.php';

    use classi\Laureando;
    use classi\LaureandoIngegneriaInformatica;
    use classi\InviaProspetti;

    class UnitTest
    {
        public $funzione;
        public $input;
        public $output;
        public $expected;

        public function __construct($funzione, $input, $expected)
        {
            $this->funzione = $funzione;
            $this->input = $input;
            $this->expected = $expected;
            echo "Expected output: " . var_export($this->expected, true) ;
        }

        public function runTest() : void
        {
            $this->output = call_user_func($this->funzione, $this->input);
            if($this->output == $this->expected) {
                echo '<p style="color:limegreen";>Test riuscito, output = ' . var_export($this->output, true) . '</p>';
            }
            else echo '<p style="color:red";>Test errato, output = ' . var_export($this->output, true) . '</p>';
        }

    }

?>
<html lang="it">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="author" content="Alex Mongelluzzi">
        <meta name="description" content="Portale per l'organizzazione di appelli di laurea">
        <title>UnitTests - Laureandosi 2.0</title>
    </head>
    <body>
        <h1>Unit Tests (test per singoli metodi su matricole 234567 e 345678)</h1>
        <h2>Laureando</h2>
        <h3>RestituisciMedia(234567)</h3>
        <?php

            function testMedia($array) : float
            {
                $l = new Laureando($array[0], $array[1], $array[2]);
                return round($l->restituisciMedia(), 3);
            }

            $test = new UnitTest('testMedia', array(234567, "27/02/2025", "T. Ing. Informatica"), 24.559);
            $test->runTest();

        ?>
        <h3>RestituisciCFU(234567)</h3>
        <?php

        function testCFU($array) : int
        {
            $l = new Laureando($array[0], $array[1], $array[2]);
            return $l->restituisciCFUMedia(false);
        }

        $test = new UnitTest('testCFU', array(234567, "27/02/2025", "T. Ing. Informatica"), 102);
        $test->runTest();

        ?>
        <h3>RestituisciCFUMedia(234567)</h3>
        <?php

        function testCFUMedia($array) : int
        {
            $l = new Laureando($array[0], $array[1], $array[2]);
            return $l->restituisciCFUMedia(true);
        }

        $test = new UnitTest('testCFUMedia', array(234567, "27/02/2025", "T. Ing. Informatica"), 102);
        $test->runTest();

        ?>
        <h2>LaureandoIngegneriaInformatica</h2>
        <h3>CalcolaBonus(345678)</h3>
        <?php

        function testBonus($array) : bool
        {
            $l = new LaureandoIngegneriaInformatica($array[0], $array[1], $array[2]);
            return $l->calcolaBonus();
        }

        $test = new UnitTest('testBonus', array(345678, "27/02/2017", "T. Ing. Informatica"), true);
        $test->runTest();

        ?>
    <h3>RestituisciMediaInformatica(345678)</h3>
        <?php

        function testMediaInformatica($array) : float
        {
            $l = new LaureandoIngegneriaInformatica($array[0], $array[1], $array[2]);
            return round($l->restituisciMediaInformatica(), 3);
        }

        $test = new UnitTest('testMediaInformatica', array(345678, "27/02/2017", "T. Ing. Informatica"), 25.75);
        $test->runTest();

        ?>
        <h3>RestituisciCFUMedia(345678 con bonus)</h3>
        <?php

        function testCFUMediaBonus($array) : int
        {
            $l = new LaureandoIngegneriaInformatica($array[0], $array[1], $array[2]);
            if ($l->calcolaBonus()) {
                $l->applicaBonus();
            }
            return $l->restituisciCFUMedia(true);
        }

        $test = new UnitTest('testCFUMediaBonus', array(345678, "27/02/2017", "T. Ing. Informatica"), 165);
        $test->runTest();

        ?>
        <h2>InviaProspetti</h2>
        <h3>GeneraMail(234567)</h3>
        <?php

        function testGeneraMail($array) : bool
        {
            $i = new InviaProspetti($array[0]);
            return is_object($i->generaMail(234567));
        }

        $test = new UnitTest('testGeneraMail', array("M. Ing. Elettronica"), true);
        $test->runTest();

        ?>
        <h3>InviaMail(234567)</h3>
        <?php

        function testInviaMail($array) : bool
        {
            $i = new InviaProspetti($array[0]);
            echo ' (deve restituire false perché la mail è fittizia)';
            return $i->inviaMail(234567);
        }

        $test = new UnitTest('testInviaMail', array("M. Ing. Elettronica"), false);
        $test->runTest();

        ?>
    </body>
</html>
