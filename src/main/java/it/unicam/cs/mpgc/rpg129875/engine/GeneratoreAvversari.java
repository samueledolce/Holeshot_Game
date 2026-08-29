package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.model.Avversario;
import it.unicam.cs.mpgc.rpg129875.model.Gara;

import java.util.List;

/**
 * Genera gli avversari per una gara. Interfaccia pensata per poter variare
 * in futuro la strategia di generazione (es. avversari legati a un vero
 * campionato o caricati da file) senza toccare il motore di gara che la
 * utilizza (Dependency Inversion).
 */
public interface GeneratoreAvversari {

    List<Avversario> generaPer(Gara gara, int livelloGiocatore);
}
