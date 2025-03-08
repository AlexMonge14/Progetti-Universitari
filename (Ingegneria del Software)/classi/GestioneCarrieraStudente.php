<?php

namespace classi;

class GestioneCarrieraStudente
{

    public static function restituisciCarrieraStudente(int $matricola): array
    {
        $string = (string)$matricola;
        $url = __DIR__ . "/../jsons/carriera/".$string."_esami.json";
        $jsonData = file_get_contents($url);
        $data = json_decode($jsonData, true);

        return $data["Esami"]["Esame"];
    }

    public static function restituisciAnagraficaStudente(int $matricola): array
    {
        $string = (string)$matricola;
        $url = __DIR__ . "/../jsons/anagrafica/".$string."_anagrafica.json";
        $jsonData = file_get_contents($url);
        $data = json_decode($jsonData, true);

        return $data["Entries"]["Entry"];
    }


}

?>