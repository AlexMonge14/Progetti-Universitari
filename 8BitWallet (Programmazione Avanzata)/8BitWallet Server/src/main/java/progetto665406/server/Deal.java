package progetto665406.server;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;

// Classe associata alla tabella "deals" tramite JPA

@Entity
@Table(name="deals", schema="665406")
public class Deal implements Serializable {
    
    @EmbeddedId
    private DealID id; // Si riferisce alla classe "DealID"
    @Column(name="Scontato")
    private double scontato;
    @Column(name="Pieno")
    private double pieno;
    @Column(name="Risparmio")
    private int risparmio;

    public Deal() {
    }

    public Deal(DealID id, double scontato, double pieno, int risparmio) {
        this.id = id;
        this.scontato = scontato;
        this.pieno = pieno;
        this.risparmio = risparmio;
    }

    public DealID getId() {
        return id;
    }

    public double getScontato() {
        return scontato;
    }

    public double getPieno() {
        return pieno;
    }

    public int getRisparmio() {
        return risparmio;
    }

    public void setId(DealID id) {
        this.id = id;
    }

    public void setScontato(double scontato) {
        this.scontato = scontato;
    }

    public void setPieno(double pieno) {
        this.pieno = pieno;
    }

    public void setRisparmio(int risparmio) {
        this.risparmio = risparmio;
    }
    
    
}
