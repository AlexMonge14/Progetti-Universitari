<?php

namespace classi;

class GestioneCdL
{

    public static function restituisciEsamiInformatici(): array
    {
        $url = __DIR__ . "/../jsons/config/infesami.json";
        $jsonData = file_get_contents($url);

        return json_decode($jsonData, true);
    }

    public static function restituisciConfigurazioneCdL(): array
    {
        $url = __DIR__ . "/../jsons/config/configurazione.json";
        $jsonData = file_get_contents($url);

        return json_decode($jsonData, true);
    }
}