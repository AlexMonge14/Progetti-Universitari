<?php

namespace classi;

use PHPMailer\PHPMailer\Exception;
use PHPMailer\PHPMailer\PHPMailer;

class InviaProspetti
{

    private string $cdl;

    public function __construct(string $cdl)
    {
        $this->cdl = $cdl;
    }

    public function generaMail(string $matricola): ?PHPMailer
    {
        try {
            $mail = new PHPMailer();
            $mail->IsSMTP();
            $mail->Host = "mixer.unipi.it";
            $mail->SMTPSecure = "tls";
            $mail->SMTPAuth = false;
            $mail->Port = 25;
            $mail->From = "no-reply-laureandosi@ing.unipi.it";

            $dati1 = GestioneCarrieraStudente::restituisciAnagraficaStudente($matricola);
            $mail->AddAddress($dati1["email_ate"]);
            $mail->AddAttachment(__DIR__ . "/../pdfs/laureandi/" . $this->cdl . "/" . $matricola . ".pdf", "ProspettoLaurea.pdf");

            $mail->Subject = "Appello di laurea in ".$this->cdl." - indicatori per voto di laurea";
            $dati2 = GestioneCdL::restituisciConfigurazioneCdL();
            foreach ($dati2 as $dato) {
                if ($dato["cdl"] == $this->cdl) {
                    $mail->Body = $dato["txt-email"];
                    break;
                }
            }
            return $mail;
        } catch (Exception $e) {
            error_log("Errore nella generazione della mail: ".$e->getMessage());
            return null;
        }
    }

    public function inviaMail(string $matricola): bool
    {
        $boolean = true;
        $mail = $this->generaMail($matricola);
        try {
            if($mail->send()) {
                throw new Exception(); // eccezione lanciata di default per notificare che la mail è fittizia
            }
        }
        catch (Exception $e) {
            $boolean = false;
        }
        return $boolean;
    }

}

?>