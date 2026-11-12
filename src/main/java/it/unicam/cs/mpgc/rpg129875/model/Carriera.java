package it.unicam.cs.mpgc.rpg129875.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregato che rappresenta l'intera carriera del giocatore: il pilota, il
 * campionato in corso, gli obiettivi dello sponsor e lo storico dei
 * risultati. È l'unico oggetto che viene salvato e caricato dal sistema di
 * persistenza.
 */
public final class Carriera {

    private final Pilota pilota;
    private Campionato campionatoCorrente;
    private final List<ObiettivoSponsor> obiettivi;
    private final List<String> storicoRisultati = new ArrayList<>();

    /** Crea una carriera senza obiettivi sponsor. */
    public Carriera(Pilota pilota, Campionato campionatoCorrente) {
        this(pilota, campionatoCorrente, List.of());
    }

    public Carriera(Pilota pilota, Campionato campionatoCorrente, List<ObiettivoSponsor> obiettivi) {
        this.pilota = pilota;
        this.campionatoCorrente = campionatoCorrente;
        this.obiettivi = new ArrayList<>(obiettivi);
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

    public List<ObiettivoSponsor> getObiettivi() {
        return List.copyOf(obiettivi);
    }

    public List<String> getStoricoRisultati() {
        return List.copyOf(storicoRisultati);
    }

    public void aggiungiRisultato(String descrizione) {
        storicoRisultati.add(descrizione);
    }
}
