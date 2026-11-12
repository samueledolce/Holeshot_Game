package it.unicam.cs.mpgc.rpg129875.model;

/**
 * Un elemento di gioco riconoscibile tramite un codice testuale.
 *
 * <p>Il codice è il "nome tecnico" dell'elemento: è quello che viene scritto
 * nei file JSON (salvataggi e contenuti di gioco) al posto dell'oggetto
 * vero e proprio, e che permette di ritrovarlo in un {@link Registro}.
 * Deve quindi essere univoco e non cambiare nel tempo, a differenza del
 * nome mostrato al giocatore, che può essere modificato liberamente.</p>
 */
public interface Identificabile {

    String codice();
}
