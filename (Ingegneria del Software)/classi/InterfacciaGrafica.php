<?php

namespace classi;

class InterfacciaGrafica
{
    private string $dataLaurea;
    private string $cdl;
    private string $elencoMatricole;

    public function __construct(
        string $dataLaurea,
        string $cdl,
        string $elencoMatricole
    ) {
        $this->dataLaurea = $dataLaurea;
        $this->cdl = $cdl;
        $this->elencoMatricole = $elencoMatricole;
    }

    public function creaProspetti(): void
    {
        $prospetti = [];
        $matricole = explode(" ", $this->elencoMatricole);

        foreach ($matricole as $matricola) {
            $prospetti[] = new ProspettoPDFLaureando(new Laureando($matricola, $this->dataLaurea, $this->cdl));
        }

        new ProspettoPDFCommissione($prospetti, $this->cdl);
    }

    public function accediProspetti(): void
    {
        $file = __DIR__ . "/../pdfs/commissione/" . $this->cdl . ".pdf";
        header("Content-Type: application/pdf");
        header("Content-Disposition: inline; filename=\"".basename($file)."\"");
        readfile($file);
    }

    public function inviaProspetti(): void
    {
        if ($this->elencoMatricole != "") {
            $matricole = explode(" ", $this->elencoMatricole);
        } else {
            $directory = __DIR__."/../pdfs/laureandi/".$this->cdl."/*.pdf";
            $matricole = [];

            foreach (glob($directory) as $file) {
                $matricole[] = pathinfo($file, PATHINFO_FILENAME);
            }
        }

        $oggetto = new InviaProspetti($this->cdl);
        $emailInviate = 0;
        $maxEmail = count($matricole);
        foreach ($matricole as $matricola) {
            sleep(3);
            if ($oggetto->inviaMail($matricola)) {
                $emailInviate++;
                echo "<script>
                      window.onload = function() {
                          aggiornaStatus(".$emailInviate . "," . $maxEmail . ", true);
                      };
                      </script>";
            }
            else {
                echo "<script>
                      window.onload = function() {
                          aggiornaStatus(".$emailInviate . "," . $maxEmail . ", false);
                      };
                      </script>";
                break;
            }
        }
    }

}

?>