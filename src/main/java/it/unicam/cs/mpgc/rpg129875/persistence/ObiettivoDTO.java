package it.unicam.cs.mpgc.rpg129875.persistence;

/**
 * Un obiettivo sponsor così come è scritto nei file JSON: nel file dei
 * contenuti {@code obiettivi.json} (dove i progressi non compaiono e
 * valgono zero) e nei salvataggi (dove riportano i progressi del pilota).
 */
final class ObiettivoDTO {

    String descrizione;
    String criterioChiave;
    int traguardo;
    int ricompensa;
    int progresso;
}
