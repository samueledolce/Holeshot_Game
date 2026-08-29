package it.unicam.cs.mpgc.rpg129875.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Un campionato: sequenza di gare (tappe) da affrontare in ordine. Tiene
 * traccia della tappa corrente e dei punti conquistati dal giocatore, ma
 * non sa come si svolge una gara (responsabilità del motore di gara).
 */
public final class Campionato {

    private final String nome;
    private final List<Gara> tappe;
    private int tappaCorrente;
    private int puntiGiocatore;

    public Campionato(String nome, List<Gara> tappe) {
        this.nome = nome;
        this.tappe = new ArrayList<>(tappe);
        this.tappaCorrente = 0;
        this.puntiGiocatore = 0;
    }

    public String getNome() {
        return nome;
    }

    public List<Gara> getTappe() {
        return List.copyOf(tappe);
    }

    public int getIndiceTappaCorrente() {
        return tappaCorrente;
    }

    public boolean isConcluso() {
        return tappaCorrente >= tappe.size();
    }

    public Gara tappaCorrente() {
        if (isConcluso()) {
            throw new IllegalStateException("Il campionato \"" + nome + "\" è già concluso.");
        }
        return tappe.get(tappaCorrente);
    }

    public int getPuntiGiocatore() {
        return puntiGiocatore;
    }

    /** Registra il piazzamento del giocatore nella tappa corrente e avanza alla successiva. */
    public void registraPiazzamento(int posizione) {
        puntiGiocatore += puntiPerPosizione(posizione);
        tappaCorrente++;
    }

    /** Riservato alla ricostruzione da un salvataggio: impone direttamente lo stato di avanzamento. */
    public void impostaProgressione(int tappaCorrente, int puntiGiocatore) {
        this.tappaCorrente = tappaCorrente;
        this.puntiGiocatore = puntiGiocatore;
    }

    private int puntiPerPosizione(int posizione) {
        return switch (posizione) {
            case 1 -> 25;
            case 2 -> 20;
            case 3 -> 16;
            case 4 -> 13;
            case 5 -> 11;
            default -> Math.max(0, 10 - posizione);
        };
    }
}
