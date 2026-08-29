package it.unicam.cs.mpgc.rpg129875.model;

/** Anni di allenamento al cancelletto. */
public final class PartenzaFulminea implements Abilita {

    @Override
    public String nome() {
        return "Partenza Fulminea";
    }

    @Override
    public String descrizione() {
        return "Anni di allenamento al cancelletto: riduce il tempo di reazione percepito "
                + "di 60 ms.";
    }

    @Override
    public int livelloRichiesto() {
        return 5;
    }

    @Override
    public long applica(long tempoReazioneMs) {
        return Math.max(0, tempoReazioneMs - 60);
    }
}
