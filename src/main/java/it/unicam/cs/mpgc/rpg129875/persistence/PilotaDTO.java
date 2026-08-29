package it.unicam.cs.mpgc.rpg129875.persistence;

import it.unicam.cs.mpgc.rpg129875.model.Moto;
import it.unicam.cs.mpgc.rpg129875.model.Statistiche;

import java.util.List;

/**
 * Rappresentazione "piatta" di un {@code Pilota} per la serializzazione:
 * niente interfacce (Gson non saprebbe quale implementazione concreta
 * ricreare), solo tipi semplici.
 */
final class PilotaDTO {

    String nome;
    String stileChiave;
    Statistiche statistiche;
    Moto moto;
    int livello;
    int esperienza;
    int denaro;
    List<String> abilitaChiavi;
}
