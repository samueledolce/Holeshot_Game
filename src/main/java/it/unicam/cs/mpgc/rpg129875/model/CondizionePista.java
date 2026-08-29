package it.unicam.cs.mpgc.rpg129875.model;

/**
 * Le condizioni della pista al cancelletto di partenza: definiscono le
 * soglie (in millisecondi) che separano le varie qualità di partenza.
 *
 * <p>Piste diverse (asciutta, fangosa, sabbiosa...) possono avere soglie
 * diverse senza che il motore di gioco debba conoscerle: dipende solo da
 * questa interfaccia (Dependency Inversion). Aggiungere una nuova
 * condizione significa aggiungere una nuova classe (Open/Closed).</p>
 */
public interface CondizionePista {

    String nome();

    /** Attesa minima, in millisecondi, prima che il cancelletto si apra. */
    long attesaMinimaMs();

    /** Attesa massima, in millisecondi, prima che il cancelletto si apra. */
    long attesaMassimaMs();

    /** Soglia (ms) entro la quale una partenza è considerata perfetta. */
    long sogliaPerfettaMs();

    /** Soglia (ms) entro la quale una partenza è considerata ottima. */
    long sogliaOttimaMs();

    /** Soglia (ms) entro la quale una partenza è considerata buona; oltre è lenta. */
    long sogliaBuonaMs();

    /** Classifica un tempo di reazione (già corretto dalle abilità) in una {@link QualitaPartenza}. */
    default QualitaPartenza classifica(long tempoReazioneMs) {
        if (tempoReazioneMs < 0) {
            return QualitaPartenza.FALSA_PARTENZA;
        }
        if (tempoReazioneMs <= sogliaPerfettaMs()) {
            return QualitaPartenza.PERFETTA;
        }
        if (tempoReazioneMs <= sogliaOttimaMs()) {
            return QualitaPartenza.OTTIMA;
        }
        if (tempoReazioneMs <= sogliaBuonaMs()) {
            return QualitaPartenza.BUONA;
        }
        return QualitaPartenza.LENTA;
    }
}
