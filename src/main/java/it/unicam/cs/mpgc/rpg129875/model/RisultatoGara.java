package it.unicam.cs.mpgc.rpg129875.model;

import java.util.List;

/** Classifica finale di una gara: ordine di arrivo e posizione ottenuta dal giocatore. */
public final class RisultatoGara {

    private final List<String> classificaOrdinata;
    private final int posizioneGiocatore;

    public RisultatoGara(List<String> classificaOrdinata, int posizioneGiocatore) {
        this.classificaOrdinata = List.copyOf(classificaOrdinata);
        this.posizioneGiocatore = posizioneGiocatore;
    }

    public List<String> getClassificaOrdinata() {
        return classificaOrdinata;
    }

    public int getPosizioneGiocatore() {
        return posizioneGiocatore;
    }

    public int getNumeroPartecipanti() {
        return classificaOrdinata.size();
    }

    public boolean isVittoria() {
        return posizioneGiocatore == 1;
    }

    public boolean isPodio() {
        return posizioneGiocatore <= 3;
    }
}
