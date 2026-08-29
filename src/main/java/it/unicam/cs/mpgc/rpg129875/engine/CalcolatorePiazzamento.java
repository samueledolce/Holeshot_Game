package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.model.Avversario;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoGara;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoPartenza;

import java.util.List;

/**
 * Calcola il piazzamento finale di una gara combinando l'esito della
 * partenza del giocatore, le sue statistiche e gli avversari.
 *
 * <p>Isolare questa logica dietro un'interfaccia permette in futuro di
 * sostituirla con una simulazione più realistica (o con più fasi di gara)
 * senza toccare il resto del motore (Open/Closed, Dependency Inversion).</p>
 */
public interface CalcolatorePiazzamento {

    RisultatoGara calcola(Pilota pilota, RisultatoPartenza partenza, List<Avversario> avversari);
}
