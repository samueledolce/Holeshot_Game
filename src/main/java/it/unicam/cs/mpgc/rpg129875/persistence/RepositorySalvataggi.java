package it.unicam.cs.mpgc.rpg129875.persistence;

import java.util.List;
import java.util.Optional;

/**
 * Contratto generico per la persistenza di un aggregato di gioco.
 *
 * <p>Il resto dell'applicazione dipende solo da questa interfaccia:
 * sostituire il file JSON con un database o con una chiamata REST (per una
 * futura versione mobile/web) significa scrivere una nuova implementazione,
 * senza toccare il motore di gioco né la interfaccia grafica (Dependency
 * Inversion Principle).</p>
 *
 * @param <T> il tipo di aggregato salvato/caricato
 */
public interface RepositorySalvataggi<T> {

    void salva(String nomeSalvataggio, T dato) throws PersistenzaException;

    Optional<T> carica(String nomeSalvataggio) throws PersistenzaException;

    List<String> elencaSalvataggi();

    void elimina(String nomeSalvataggio) throws PersistenzaException;
}
