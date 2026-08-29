package it.unicam.cs.mpgc.rpg129875.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregato che rappresenta l'intera carriera del giocatore: il pilota, il
 * campionato in corso e lo storico dei risultati. È l'unico oggetto che
 * viene salvato e caricato dal sistema di persistenza.
 */
public final class Carriera {

    private final Pilota pilota;
    private Campionato campionatoCorrente;
    private final List<String> storicoRisultati = new ArrayList<>();

    public Carriera(Pilota pilota, Campionato campionatoCorrente) {
        this.pilota = pilota;
        this.campionatoCorrente = campionatoCorrente;
    }

    public Pilota getPilota() {
        return pilota;
    }

    public Campionato getCampionatoCorrente() {
        return campionatoCorrente;
    }

    public void iniziaNuovoCampionato(Campionato campionato) {
        this.campionatoCorrente = campionato;
    }

    public List<String> getStoricoRisultati() {
        return List.copyOf(storicoRisultati);
    }

    public void aggiungiRisultato(String descrizione) {
        storicoRisultati.add(descrizione);
    }
}
