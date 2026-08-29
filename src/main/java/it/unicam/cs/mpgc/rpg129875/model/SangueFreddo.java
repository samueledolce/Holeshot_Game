package it.unicam.cs.mpgc.rpg129875.model;

/** Mantiene la calma anche partendo un po' in ritardo. */
public final class SangueFreddo implements Abilita {

    @Override
    public String nome() {
        return "Sangue Freddo";
    }

    @Override
    public String descrizione() {
        return "Mantiene la calma anche partendo un po' in ritardo: riduce il tempo di "
                + "reazione percepito di 25 ms.";
    }

    @Override
    public int livelloRichiesto() {
        return 2;
    }

    @Override
    public long applica(long tempoReazioneMs) {
        return Math.max(0, tempoReazioneMs - 25);
    }
}
