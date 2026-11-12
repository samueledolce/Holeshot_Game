package it.unicam.cs.mpgc.rpg129875.model;

import java.util.List;

/** Gli obiettivi che lo sponsor propone a ogni pilota all'inizio della carriera. */
public interface CatalogoObiettivi {

    /**
     * Restituisce obiettivi nuovi, con i progressi a zero: ogni carriera
     * deve avere i propri, perché ognuno tiene il conto dei suoi progressi.
     */
    List<ObiettivoSponsor> nuoviObiettivi();
}
