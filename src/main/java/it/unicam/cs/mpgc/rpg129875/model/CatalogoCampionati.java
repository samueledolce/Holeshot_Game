package it.unicam.cs.mpgc.rpg129875.model;

import java.util.List;

/** Campionati precostituiti proposti al giocatore. */
public final class CatalogoCampionati {

    private CatalogoCampionati() {
    }

    public static Campionato nuovoCampionatoRookie() {
        return new Campionato("Campionato Rookie", List.of(
                new Gara("GP di Maggiora", new PistaAsciutta(), 5),
                new Gara("GP di Loket", new PistaFangosa(), 6),
                new Gara("GP di Lacapelle", new PistaSabbiosa(), 6),
                new Gara("GP di Assen", new PistaAsciutta(), 7),
                new Gara("GP di Ottobiano", new PistaFangosa(), 7)
        ));
    }
}
