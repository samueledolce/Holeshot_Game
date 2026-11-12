package it.unicam.cs.mpgc.rpg129875.model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * L'elenco delle moto esistenti nel gioco.
 *
 * <p>È un'interfaccia perché il resto dell'applicazione non deve sapere da
 * dove arrivano le moto: oggi vengono lette da un file JSON, in futuro
 * potrebbero arrivare da un database o da un servizio remoto senza
 * modificare chi le usa (Dependency Inversion).</p>
 */
public interface CatalogoMoto {

    /** Tutte le moto, nell'ordine in cui vanno presentate al giocatore. */
    List<Moto> tutte();

    /** Le moto gratuite tra cui scegliere all'inizio di una carriera. */
    default List<Moto> diSerie() {
        return tutte().stream().filter(Moto::isDiSerie).collect(Collectors.toList());
    }
}
