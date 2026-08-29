package it.unicam.cs.mpgc.rpg129875.model;

/**
 * Un'abilità sbloccabile dal pilota salendo di livello. Ogni abilità
 * modifica in modo diverso l'esito della partenza.
 *
 * <p>Implementarne una nuova non richiede modifiche al motore di gioco:
 * il motore dipende solo da questa astrazione, non dalle singole abilità
 * (principi Open/Closed e Dependency Inversion).</p>
 */
public interface Abilita {

    String nome();

    String descrizione();

    int livelloRichiesto();

    /**
     * Applica l'effetto dell'abilità al tempo di reazione misurato,
     * restituendo il tempo (in millisecondi) da usare per classificare la
     * partenza. Un valore minore è sempre migliore.
     */
    long applica(long tempoReazioneMs);
}
