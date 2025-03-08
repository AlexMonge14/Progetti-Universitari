<?php

namespace classi;

use DateTime;
class LaureandoIngegneriaInformatica extends Laureando
{
    private string $dataImmatricolazione;

    public function __construct(
        int $matricola,
        string $dataLaurea,
        string $cdl
    ) {
        parent::__construct($matricola, $dataLaurea, $cdl);

        $dati = GestioneCarrieraStudente::restituisciCarrieraStudente($matricola);
        $this->dataImmatricolazione = $dati[0]["INIZIO_CARRIERA"];

        $data = GestioneCdL::restituisciEsamiInformatici();
        foreach ($this->getEsami() as $esame) {
            foreach ($data as $infesame) {
                if ($esame->getNome() == $infesame) {
                    $esame->setInformatico(true);
                }
            }
        }
    }

    public function getDataImmatricolazione(): string
    {
        return $this->dataImmatricolazione;
    }

    public function calcolaBonus(): bool
    {
        $immatricolazione = DateTime::createFromFormat('d/m/Y', $this->getDataImmatricolazione());
        $appello = DateTime::createFromFormat('Y-m-d', $this->getDataLaurea());

        $dataLimite = clone $immatricolazione;
        $dataLimite->modify('+4 years')->setDate($dataLimite->format('Y'), 4, 30);

        return $appello <= $dataLimite;
    }

    public function applicaBonus(): void
    {
        $esameMinore = $this->getEsami()[0];
        foreach ($this->getEsami() as $esame) {
            if ($esame->getVoto() < $esameMinore->getVoto() && $esame->isValevoleMedia()) {
                $esameMinore = $esame;
            }
            if ($esame->getVoto() == $esameMinore->getVoto() && $esame->getCFU() > $esameMinore->getCFU()) {
                $esameMinore = $esame;
            }
        }

        $esameMinore->setValevoleMedia(false);
    }

    public function restituisciMediaInformatica(): float
    {
        $totaleCFU = 0;
        $parzialeSomma = 0;

        foreach ($this->getEsami() as $esame) {
            if ($esame->isValevoleMedia() && $esame->isInformatico()) {
                $totaleCFU += $esame->getCFU();
                $parzialeSomma += $esame->getVoto() * $esame->getCFU();
            }
        }

        return $parzialeSomma / $totaleCFU;
    }

}

?>