package it.unicam.cs.mpgc.rpg129875.gui;

import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.Gara;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoPartenza;

/**
 * Contratto per la navigazione tra le schermate dell'applicazione.
 *
 * <p>Le singole schermate dipendono solo da questa interfaccia, non
 * dall'applicazione JavaFX concreta: in futuro sarebbe possibile fornire
 * un'implementazione diversa (es. per un front-end differente) senza
 * riscrivere le schermate stesse (Dependency Inversion Principle).</p>
 */
public interface Navigatore {

    void mostraMenu();

    void mostraCreazionePilota();

    void avviaCarriera(Carriera carriera);

    void mostraCarriera();

    void passaAlProssimoCampionato();

    void mostraGara();

    void mostraPartenza(Gara gara);

    void completaGara(Gara gara, RisultatoPartenza esitoPartenza);

    void salvaCarriera(String nomeSalvataggio, Runnable alTermine);

    void caricaCarriera(String nomeSalvataggio);

    void mostraErrore(String messaggio);
}
