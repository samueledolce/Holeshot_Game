package it.unicam.cs.mpgc.rpg129875.model;

/** Anticipa istintivamente il cancelletto. */
public final class SestoSenso implements Abilita {

    @Override
    public String nome() {
        return "Sesto Senso";
    }

    @Override
    public String descrizione() {
        return "Anticipa istintivamente il cancelletto: riduce il tempo di reazione "
                + "percepito di 40 ms.";
    }

    @Override
    public int livelloRichiesto() {
        return 3;
    }

    @Override
    public long applica(long tempoReazioneMs) {
        return Math.max(0, tempoReazioneMs - 40);
    }
}
