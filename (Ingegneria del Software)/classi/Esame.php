<?php

namespace classi;

class Esame
{
    private string $nome;
    private int $voto;
    private int $cfu;
    private string $data;
    private bool $valevoleMedia;
    private bool $informatico;

    public function __construct(
        string $nome,
        int $voto,
        int $cfu,
        string $data,
        bool $valevoleMedia,
        bool $informatico
    ) {
        $this->nome = $nome;
        $this->voto = $voto;
        $this->cfu = $cfu;
        $this->data = $data;
        $this->valevoleMedia = $valevoleMedia;
        $this->informatico = $informatico;
    }

    public function getNome(): string
    {
        return $this->nome;
    }

    public function getVoto(): int
    {
        return $this->voto;
    }

    public function getCFU(): int
    {
        return $this->cfu;
    }

    public function getData(): string
    {
        return $this->data;
    }

    public function isValevoleMedia(): bool
    {
        return $this->valevoleMedia;
    }

    public function isInformatico(): bool
    {
        return $this->informatico;
    }

    public function setValevoleMedia(bool $valevoleMedia): void
    {
        $this->valevoleMedia = $valevoleMedia;
    }

    public function setInformatico(bool $informatico): void
    {
        $this->informatico = $informatico;
    }

}

?>