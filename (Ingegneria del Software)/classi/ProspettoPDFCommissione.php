<?php

namespace classi;

use FPDF;

class ProspettoPDFCommissione
{
    private array $prospettiLaureandi;
    private FPDF $pdf;
    private string $cdl;

    public function __construct(array $prospettiLaureandi, string $cdl)
    {
        $this->prospettiLaureandi = $prospettiLaureandi;
        $this->cdl = $cdl;
        $this->pdf = new FPDF();
        $this->generaProspettoPDFCommissione();
        $url = __DIR__ . "/../pdfs/commissione/" . $this->cdl . ".pdf";
        $this->pdf->Output($url, 'F');
    }

    public function generaProspettoPDFCommissione(): void
    {
        $this->pdf->AddPage();

        $this->popolaCopertina();

        foreach ($this->prospettiLaureandi as $prospettoLaureando) {
            $prospettoLaureando->generaProspettoPDFLaureando($this->pdf);
            $this->pdf->Cell(0, 4, "", 0, 1, 'L');
            $this->aggiungiSimulazione($prospettoLaureando->getLaureando());
        }
    }

    public function popolaCopertina(): void
    {
        $this->pdf->SetFont("Arial", "", "9");

        $this->pdf->Cell(0, 4, $this->cdl, 0, 1, 'C');

        $this->pdf->SetFont("Arial", "", "10");
        $this->pdf->Cell(0, 4, "", 0, 1, 'C');
        $this->pdf->Cell(0, 4, "LAUREANDOSI 2.0 - Progetto Ingegneria del Software: Alex Mongelluzzi", 0, 1, 'C');
        $this->pdf->Cell(0, 8, "", 0, 1, 'C');
        $this->pdf->Cell(0, 4, "LISTA LAUREANDI", 0, 1, 'C');
        $this->pdf->Cell(0, 4, "", 0, 1, 'C');
        $this->pdf->Cell(47, 6, "COGNOME", "RTBL", 0, 'C');
        $this->pdf->Cell(47, 6, "NOME", "RTBL", 0, 'C');
        $this->pdf->Cell(47, 6, "CDL", "RTBL", 0, 'C');
        $this->pdf->Cell(47, 6, "VOTO LAUREA", "RTBL", 1, 'C');

        $this->pdf->SetFont("Arial", "", "9");

        foreach ($this->prospettiLaureandi as $prospettoLaureando) {
            $this->pdf->Cell(47, 6, $prospettoLaureando->getLaureando()->getCognome(), "RTBL", 0, 'C');
            $this->pdf->Cell(47, 6, $prospettoLaureando->getLaureando()->getNome(), "RTBL", 0, 'C');
            $this->pdf->Cell(47, 6, "", "RTBL", 0, 'C');
            $this->pdf->Cell(47, 6, " /110", "RTBL", 1, 'C');
        }
    }

    public function calcolaVoto(Laureando $l, SimulazioneVoto $sv, float $valore): float
    {
        $espressione = $sv->getFormula();
        if ($sv->getTipoP() == "VOTO COMMISSIONE (C)") {
            $espressione = str_replace(["M", "T", "CFU", "C"],
                [$l->restituisciMedia(), 0, $l->restituisciCFUMedia(false), $valore],
                $espressione);
        } else {
            $espressione = str_replace(["M", "T", "CFU", "C"],
                [$l->restituisciMedia(), $valore, $l->restituisciCFUMedia(false), 0],
                $espressione);
        }

        return eval("return $espressione;");
    }

    public function aggiungiSimulazione(Laureando $l): void
    {
        $this->pdf->Cell(0, 7, "SIMULAZIONE DI VOTO DI LAUREA", "RTBL", 1, 'C');
        $simulazione = new SimulazioneVoto($this->cdl);

        $result = $simulazione->getPmax();
        $counter = 0;
        while ($result > $simulazione->getPmin()) {
            $result -= $simulazione->getPstep();
            $counter++;
        }

        if ($counter <= 7) {
            $this->pdf->Cell(95, 7, $simulazione->getTipoP(), "RTBL", 0, 'C');
            $this->pdf->Cell(95, 7, "VOTO LAUREA", "RTBL", 1, 'C');

            for ($i = 0; $i <= $counter; $i++) {
                $valore = $simulazione->getPmin() + ($simulazione->getPstep() * $i);
                $this->pdf->Cell(95, 7, $valore, "RTBL", 0, 'C');
                $this->pdf->Cell(95, 7, round($this->calcolaVoto($l, $simulazione, $valore), 3), "RTBL", 1, 'C');
            }
        } else {
            $counter = round($counter / 2);
            $this->pdf->Cell(47, 7, $simulazione->getTipoP(), "RTBL", 0, 'C');
            $this->pdf->Cell(48, 7, "VOTO LAUREA", "RTBL", 0, 'C');
            $this->pdf->Cell(47, 7, $simulazione->getTipoP(), "RTBL", 0, 'C');
            $this->pdf->Cell(48, 7, "VOTO LAUREA", "RTBL", 1, 'C');
            for ($i = 0; $i <= $counter; $i++) {
                $valore = $simulazione->getPmin() + ($simulazione->getPstep() * $i);
                if ($valore + $counter + 1 > $simulazione->getPmax()) {
                    $this->pdf->Cell(47, 7, $valore, "RTBL", 0, 'C');
                    $this->pdf->Cell(48, 7, round($this->calcolaVoto($l, $simulazione, $valore), 3), "RTBL", 1, 'C');
                    break;
                } else {
                    $this->pdf->Cell(47, 7, $valore, "RTBL", 0, 'C');
                    $this->pdf->Cell(48, 7, round($this->calcolaVoto($l, $simulazione, $valore), 3), "RTBL", 0, 'C');
                    $this->pdf->Cell(47, 7, $valore + $counter + 1, "RTBL", 0, 'C');
                    $this->pdf->Cell(
                        48,
                        7,
                        round($this->calcolaVoto($l, $simulazione, $valore + $counter + 1), 3),
                        "RTBL",
                        1,
                        'C'
                    );
                }
            }
        }

        $this->pdf->Cell(0, 6, "", 0, 1, 'L');
        if ($this->cdl == "T. Ing. Informatica") {
            $this->pdf->Cell(
                0,
                3,
                "VOTO DI LAUREA FINALE: scegli voto commissione, prendi il corrispondente voto di laurea e somma il voto di tesi tra 1",
                0,
                1,
                'L'
            );
            $this->pdf->Cell(0, 3, "e 3, quindi arrotonda", 0, 1, 'L');
        } else {
            $this->pdf->Cell(
                0,
                4,
                "VOTO DI LAUREA FINALE: scegli voto di tesi, prendi il corrispondente voto di laurea ed arrotonda",
                0,
                1,
                'L'
            );
        }
    }

}