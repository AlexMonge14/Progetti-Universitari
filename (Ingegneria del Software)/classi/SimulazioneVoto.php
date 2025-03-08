<?php

namespace classi;

class SimulazioneVoto
{
    private string $formula;
    private float $Pmin;
    private float $Pmax;
    private float $Pstep;
    private string $tipoP;

    public function __construct(string $cdl)
    {
        $data = GestioneCdL::restituisciConfigurazioneCdL();
        foreach ($data as $corso) {
            if ($cdl == $corso["cdl"]) {
                $this->formula = $corso["voto-laurea"];
                if ($corso["par-C"]["step"] == 0) {
                    $this->Pmin = $corso["par-T"]["min"];
                    $this->Pmax = $corso["par-T"]["max"];
                    $this->Pstep = $corso["par-T"]["step"];
                    $this->tipoP = "VOTO TESI (T)";
                } else {
                    $this->Pmin = $corso["par-C"]["min"];
                    $this->Pmax = $corso["par-C"]["max"];
                    $this->Pstep = $corso["par-C"]["step"];
                    $this->tipoP = "VOTO COMMISSIONE (C)";
                }
            }
        }
    }

    public function getFormula(): string
    {
        return $this->formula;
    }

    public function getPmin(): float
    {
        return $this->Pmin;
    }

    public function getPmax(): float
    {
        return $this->Pmax;
    }

    public function getPstep(): float
    {
        return $this->Pstep;
    }

    public function getTipoP(): string
    {
        return $this->tipoP;
    }

}

?>