package it.unicam.cs.mpgc.rpg129875.model;

/**
 * Contratto per uno stile di guida: definisce le statistiche iniziali di un
 * pilota e come si presenta.
 *
 * <p>Ogni implementazione rappresenta un diverso archetipo di pilota
 * (aggressivo, tecnico, equilibrato...). Aggiungere un nuovo stile significa
 * aggiungere una nuova classe, senza modificare {@link Pilota} né le
 * implementazioni già esistenti (principio Open/Closed).</p>
 */
public interface StileDiGuida {

    String nome();

    String descrizione();

    Statistiche statisticheIniziali();
}
