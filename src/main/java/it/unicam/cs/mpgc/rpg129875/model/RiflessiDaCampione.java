package it.unicam.cs.mpgc.rpg129875.model;

/** Il culmine dell'esperienza in gara. */
public final class RiflessiDaCampione implements Abilita {

    @Override
    public String nome() {
        return "Riflessi da Campione";
    }

    @Override
    public String descrizione() {
        return "Il culmine dell'esperienza in gara: riduce il tempo di reazione percepito "
                + "di 80 ms.";
    }

    @Override
    public int livelloRichiesto() {
        return 8;
    }

    @Override
    public long applica(long tempoReazioneMs) {
        return Math.max(0, tempoReazioneMs - 80);
    }
}
