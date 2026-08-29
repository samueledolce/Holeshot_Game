package it.unicam.cs.mpgc.rpg129875.model;

/** Le possibili qualità di una partenza, dalla peggiore alla migliore. */
public enum QualitaPartenza {

    FALSA_PARTENZA("Falsa partenza! Penalità in griglia.", 0),
    LENTA("Partenza lenta", 1),
    BUONA("Buona partenza", 2),
    OTTIMA("Ottima partenza", 3),
    PERFETTA("Partenza perfetta!", 4);

    private final String descrizione;
    private final int punteggio;

    QualitaPartenza(String descrizione, int punteggio) {
        this.descrizione = descrizione;
        this.punteggio = punteggio;
    }

    public String getDescrizione() {
        return descrizione;
    }

    /** Contributo della qualità di partenza al piazzamento finale: più alto è meglio. */
    public int getPunteggio() {
        return punteggio;
    }
}
