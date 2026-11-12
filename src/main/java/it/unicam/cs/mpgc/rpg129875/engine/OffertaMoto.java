package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.model.Moto;

/**
 * Una moto in vendita, vista da un determinato pilota.
 *
 * @param moto       la moto offerta
 * @param costo      i crediti che il pilota deve pagare, già tolto il
 *                   valore di permuta della sua moto attuale
 * @param allaPortata {@code true} se il pilota ha crediti sufficienti
 */
public record OffertaMoto(Moto moto, int costo, boolean allaPortata) {
}
