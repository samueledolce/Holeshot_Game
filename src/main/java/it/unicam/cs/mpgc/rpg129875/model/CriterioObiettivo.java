package it.unicam.cs.mpgc.rpg129875.model;

/**
 * La condizione che una gara deve soddisfare per contare ai fini di un
 * obiettivo sponsor (es. "salire sul podio", "fare una partenza perfetta").
 *
 * <p>Ogni criterio è una piccola classe indipendente: un nuovo tipo di
 * obiettivo si aggiunge scrivendo un nuovo criterio e registrandolo in
 * {@link Registri}, senza modificare {@link ObiettivoSponsor} né chi
 * aggiorna gli obiettivi (Open/Closed).</p>
 */
public interface CriterioObiettivo extends Identificabile {

    boolean soddisfattoDa(RisultatoPartenza partenza, RisultatoGara gara);
}
