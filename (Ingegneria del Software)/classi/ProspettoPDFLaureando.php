<?php

namespace classi;

use FPDF;

class ProspettoPDFLaureando
{
    private Laureando $laureando;
    private FPDF $pdf;

    public function __construct(Laureando $laureando)
    {
        $this->laureando = $laureando;
        $this->pdf = new FPDF();
        $this->generaProspettoPDFLaureando($this->pdf);
        $string = (string)$this->laureando->getMatricola();
        $directory = __DIR__ . "/../pdfs/laureandi/" . $this->laureando->getCdl() . "/";
        if (!is_dir($directory)) {
            mkdir($directory, 0777, true);
        }
        $url = $directory.$string.".pdf";
        $this->pdf->Output($url, 'F');
    }

    public function getLaureando(): Laureando
    {
        return $this->laureando;
    }

    public function convertiIngegneriaInformatica(): void
    {
        $this->laureando = new LaureandoIngegneriaInformatica(
            $this->laureando->getMatricola(),
            $this->laureando->getDataLaurea(),
            $this->laureando->getCdl()
        );
    }

    public function generaProspettoPDFLaureando(FPDF $pdf): void
    {
        if ($this->laureando->getCdl() == "T. Ing. Informatica") {
            $this->convertiIngegneriaInformatica();
        }

        $pdf->AddPage();

        $this->popolaIntestazione($pdf);
        $this->popolaLista($pdf);
        $this->popolaFormula($pdf);
    }

    public function popolaIntestazione(FPDF $pdf): void
    {
        $pdf->SetFont("Arial", "", "16");

        $pdf->Cell(0, 9, $this->laureando->getCdl(), 0, 1, 'C');
        $pdf->Cell(0, 13, "CARRIERA E SIMULAZIONE DEL VOTO DI LAUREA", 0, 1, 'C');
        $pdf->SetFont("Arial", "", "11");
        $pdf->Cell(55, 5, "Matricola:", "LT", 0, 'L');
        $pdf->Cell(0, 5, $this->laureando->getMatricola(), "RT", 1, 'L');
        $pdf->Cell(55, 5, "Nome:", "L", 0, 'L');
        $pdf->Cell(0, 5, $this->laureando->getNome(), "R", 1, 'L');
        $pdf->Cell(55, 5, "Cognome:", "L", 0, 'L');
        $pdf->Cell(0, 5, $this->laureando->getCognome(), "R", 1, 'L');
        $pdf->Cell(55, 5, "Email:", "L", 0, 'L');
        $pdf->Cell(0, 5, $this->laureando->getEmail(), "R", 1, 'L');

        if ($this->laureando instanceof LaureandoIngegneriaInformatica) {
            $pdf->Cell(55, 5, "Data:", "L", 0, 'L');
            $pdf->Cell(0, 5, $this->laureando->getDataLaurea(), "R", 1, 'L');
            $pdf->Cell(55, 5, "Bonus:", "LB", 0, 'L');
            $pdf->Cell(0, 5, ($this->laureando->calcolaBonus()) ? "SI" : "NO", "RB", 1, 'L');
            $pdf->Cell(0, 2, "", 0, 1, 'L');

            if ($this->laureando->calcolaBonus()) {
                $this->laureando->applicaBonus();
            }
        } else {
            $pdf->Cell(55, 5, "Data:", "LB", 0, 'L');
            $pdf->Cell(0, 5, $this->laureando->getDataLaurea(), "RB", 1, 'L');
            $pdf->Cell(0, 2, "", 0, 1, 'L');
        }
    }

    public function popolaLista(FPDF $pdf): void
    {
        if ($this->laureando instanceof LaureandoIngegneriaInformatica) {
            $pdf->Cell(150, 7, "ESAME", "LTRB", 0, 'C');
            $pdf->Cell(10, 7, "CFU", "LTRB", 0, 'C');
            $pdf->Cell(10, 7, "VOT", "LTRB", 0, 'C');
            $pdf->Cell(10, 7, "MED", "LTRB", 0, 'C');
            $pdf->Cell(10, 7, "INF", "LTRB", 1, 'C');
            $pdf->SetFont("Arial", "", "8");
            foreach ($this->laureando->getEsami() as $esame) {
                $pdf->Cell(150, 4, $esame->getNome(), "LTRB", 0, 'L');
                $pdf->Cell(10, 4, $esame->getCFU(), "LTRB", 0, 'C');
                $pdf->Cell(10, 4, $esame->getVoto(), "LTRB", 0, 'C');
                $pdf->Cell(10, 4, ($esame->isValevoleMedia()) ? "X" : "", "LTRB", 0, 'C');
                $pdf->Cell(10, 4, ($esame->isInformatico()) ? "X" : "", "LTRB", 1, 'C');
            }
        } else {
            $pdf->Cell(160, 7, "ESAME", "LTRB", 0, 'C');
            $pdf->Cell(10, 7, "CFU", "LTRB", 0, 'C');
            $pdf->Cell(10, 7, "VOT", "LTRB", 0, 'C');
            $pdf->Cell(10, 7, "MED", "LTRB", 1, 'C');
            $pdf->SetFont("Arial", "", "8");
            foreach ($this->laureando->getEsami() as $esame) {
                $pdf->Cell(160, 4, $esame->getNome(), "LTRB", 0, 'L');
                $pdf->Cell(10, 4, $esame->getCFU(), "LTRB", 0, 'C');
                $pdf->Cell(10, 4, $esame->getVoto(), "LTRB", 0, 'C');
                $pdf->Cell(10, 4, ($esame->isValevoleMedia()) ? "X" : "", "LTRB", 1, 'C');
            }
        }
    }

    public function popolaFormula(FPDF $pdf): void
    {
        $pdf->SetFont("Arial", "", "10");

        $data = GestioneCdL::restituisciConfigurazioneCdL();
        $cfu = 0;
        $formula = 0;
        foreach ($data as $cdl) {
            if ($this->laureando->getCdl() == $cdl["cdl"]) {
                $cfu = $cdl["tot-CFU"];
                $formula = $cdl['voto-laurea'];
                break;
            }
        }

        $pdf->Cell(0, 4, "", 0, 1, 'L');
        $pdf->Cell(80, 5, "Media Pesata (M):", "LT", 0, 'L');
        $pdf->Cell(0, 5, round($this->laureando->restituisciMedia(), 3), "RT", 1, 'L');
        $pdf->Cell(80, 5, "Crediti che fanno media (CFU):", "L", 0, 'L');
        $pdf->Cell(0, 5, $this->laureando->restituisciCFUMedia(true), "R", 1, 'L');
        $pdf->Cell(80, 5, "Crediti curriculari conseguiti:", "L", 0, 'L');
        $pdf->Cell(0, 5, $this->laureando->restituisciCFUMedia(false)."/".$cfu, "R", 1, 'L');

        if ($this->laureando instanceof LaureandoIngegneriaInformatica) {
            $pdf->Cell(80, 5, "Voto di Tesi (T):", "L", 0, 'L');
            $pdf->Cell(0, 5, 0, "R", 1, 'L');
            $pdf->Cell(80, 5, "Formula calcolo voto di laurea:", "L", 0, 'L');
            $pdf->Cell(0, 5, $formula, "R", 1, 'L');
            $pdf->Cell(80, 5, "Media pesata esami INF:", "LB", 0, 'L');
            $pdf->Cell(0, 5, round($this->laureando->restituisciMediaInformatica(), 3), "RB", 1, 'L');
        } else {
            $pdf->Cell(80, 5, "Formula calcolo voto di laurea:", "LB", 0, 'L');
            $pdf->Cell(0, 5, $formula, "RB", 1, 'L');
        }
    }

}

?>