package it.unicam.cs.mpgc.rpg129875.persistence;

import java.util.List;

/** Rappresentazione "piatta" di un {@code Campionato} per la serializzazione. */
final class CampionatoDTO {

    String nome;
    List<GaraDTO> tappe;
    int tappaCorrente;
    int puntiGiocatore;
}
