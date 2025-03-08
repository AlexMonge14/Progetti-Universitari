<?php

namespace classi;

use DateTime;
class Laureando
{
    private string $nome;
    private string $cognome;
    private int $matricola;
    private string $cdl;
    private string $email;
    private string $dataLaurea;
    private array $esami;

    public function __construct(
        int $matricola,
        string $dataLaurea,
        string $cdl
    ) {
        $this->matricola = $matricola;
        $this->dataLaurea = $dataLaurea;
        $this->cdl = $cdl;

        $dati1 = GestioneCarrieraStudente::restituisciAnagraficaStudente($matricola);
        $this->nome = $dati1["nome"];
        $this->cognome = $dati1["cognome"];
        $this->email = $dati1["email_ate"];

        $dati2 = GestioneCarrieraStudente::restituisciCarrieraStudente($matricola);

        foreach ($dati2 as $dato) {
            if ($dato['DES'] == "PROVA FINALE" || $dato['DES'] == "TESI" ||
                $dato["SOVRAN_FLG"] == 1 || is_array($dato["SOVRAN_FLG"])) {
                continue;
            }

            $voto = $dato["VOTO"];
            if ($voto === null) {
                $voto = 0;
            }

            if ($voto == '30  e lode') {
                $voto = 33;
            }

            $this->esami[] = new Esame(
                $dato["DES"],
                $voto,
                $dato["PESO"],
                $dato["DATA_ESAME"],
                ($voto != 0),
                false
            );
        }
        usort($this->esami, function ($a, $b) {
            return (DateTime::createFromFormat('d/m/Y', $a->getData()) <=>
                (DateTime::createFromFormat('d/m/Y', $b->getData())));
        });
    }

    public function getNome(): string
    {
        return $this->nome;
    }

    public function getCognome(): string
    {
        return $this->cognome;
    }

    public function getMatricola(): int
    {
        return $this->matricola;
    }

    public function getCdl(): string
    {
        return $this->cdl;
    }

    public function getEmail(): string
    {
        return $this->email;
    }

    public function getDataLaurea(): string
    {
        return $this->dataLaurea;
    }

    public function getEsami(): array
    {
        return $this->esami;
    }


    public function restituisciMedia(): float
    {
        $totaleCFU = 0;
        $parzialeSomma = 0;

        foreach ($this->esami as $esame) {
            if ($esame->isValevoleMedia()) {
                $totaleCFU += $esame->getCFU();
                $parzialeSomma += $esame->getVoto() * $esame->getCFU();
            }
        }

        return $parzialeSomma / $totaleCFU;
    }

    public function restituisciCFUMedia(bool $media): int
    {
        $totaleCFU = 0;
        foreach ($this->esami as $esame) {
            if ($media) {
                if ($esame->isValevoleMedia()) {
                    $totaleCFU += $esame->getCFU();
                }
            } else {
                $totaleCFU += $esame->getCFU();
            }
        }

        return $totaleCFU;
    }

}

?>