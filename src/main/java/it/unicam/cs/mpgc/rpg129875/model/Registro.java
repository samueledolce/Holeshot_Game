package it.unicam.cs.mpgc.rpg129875.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Raccolta di elementi {@link Identificabile} ritrovabili tramite il loro
 * codice. Mantiene l'ordine in cui gli elementi sono stati registrati.
 *
 * <p>Responsabilità unica: associare un codice all'elemento corrispondente.
 * È generico, quindi la stessa classe serve per gli stili di guida, per le
 * condizioni di pista e per qualunque altro elemento identificabile
 * aggiunto in futuro.</p>
 *
 * @param <T> il tipo degli elementi registrati
 */
public final class Registro<T extends Identificabile> {

    private final Map<String, T> elementiPerCodice = new LinkedHashMap<>();

    public Registro(List<T> elementi) {
        for (T elemento : elementi) {
            registra(elemento);
        }
    }

    private void registra(T elemento) {
        T precedente = elementiPerCodice.putIfAbsent(elemento.codice(), elemento);
        if (precedente != null) {
            throw new IllegalArgumentException("Codice duplicato nel registro: " + elemento.codice());
        }
    }

    public List<T> tutti() {
        return List.copyOf(elementiPerCodice.values());
    }

    public Optional<T> cerca(String codice) {
        return Optional.ofNullable(elementiPerCodice.get(codice));
    }

    /**
     * Restituisce l'elemento con il codice indicato.
     *
     * @throws IllegalArgumentException se nessun elemento ha quel codice
     */
    public T perCodice(String codice) {
        return cerca(codice).orElseThrow(() -> new IllegalArgumentException(
                "Codice sconosciuto: \"" + codice + "\". Codici validi: " + elementiPerCodice.keySet()));
    }
}
