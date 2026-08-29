package it.unicam.cs.mpgc.rpg129875.model;

/**
 * Esito della partenza del giocatore: tempo di reazione (in millisecondi,
 * già corretto dalle eventuali abilità) e qualità corrispondente.
 */
public final class RisultatoPartenza {

    private final long tempoReazioneMs;
    private final QualitaPartenza qualita;

    public RisultatoPartenza(long tempoReazioneMs, QualitaPartenza qualita) {
        this.tempoReazioneMs = tempoReazioneMs;
        this.qualita = qualita;
    }

    public long getTempoReazioneMs() {
        return tempoReazioneMs;
    }

    public QualitaPartenza getQualita() {
        return qualita;
    }

    public boolean isFalsaPartenza() {
        return qualita == QualitaPartenza.FALSA_PARTENZA;
    }
}
