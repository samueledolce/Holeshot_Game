package it.unicam.cs.mpgc.rpg129875.model;

import java.util.Optional;

/**
 * La sequenza dei campionati che compongono una carriera, dal primo
 * all'ultimo.
 *
 * <p>Ogni chiamata restituisce un campionato nuovo, ancora da disputare:
 * un {@link Campionato} tiene traccia dell'avanzamento del giocatore,
 * quindi non può essere condiviso tra carriere diverse.</p>
 */
public interface CatalogoCampionati {

    /** Il campionato con cui inizia ogni nuova carriera. */
    Campionato primo();

    /** Il campionato che viene dopo quello con il nome indicato, se ce n'è uno. */
    Optional<Campionato> successivoA(String nomeCampionato);
}
