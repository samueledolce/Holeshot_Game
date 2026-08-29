package it.unicam.cs.mpgc.rpg129875.model;

import java.util.stream.IntStream;

/**
 * Le statistiche di un pilota: quattro valori, indicativamente tra 1 e 100,
 * che ne descrivono le capacità in pista.
 *
 * <p>Responsabilità unica: contenere e far crescere i valori numerici delle
 * capacità di un pilota. Non conosce nulla di gare, partenze o abilità.</p>
 */
public final class Statistiche {

    private static final int VALORE_MINIMO = 1;
    private static final int VALORE_MASSIMO = 100;

    private int riflessi;
    private int concentrazione;
    private int tecnicaPartenza;
    private int resistenza;

    public Statistiche(int riflessi, int concentrazione, int tecnicaPartenza, int resistenza) {
        this.riflessi = limita(riflessi);
        this.concentrazione = limita(concentrazione);
        this.tecnicaPartenza = limita(tecnicaPartenza);
        this.resistenza = limita(resistenza);
    }

    private static int limita(int valore) {
        return Math.max(VALORE_MINIMO, Math.min(VALORE_MASSIMO, valore));
    }

    public int getRiflessi() {
        return riflessi;
    }

    public int getConcentrazione() {
        return concentrazione;
    }

    public int getTecnicaPartenza() {
        return tecnicaPartenza;
    }

    public int getResistenza() {
        return resistenza;
    }

    /** Applica un incremento (positivo o negativo) a ciascuna statistica, rispettando i limiti. */
    public void aumenta(int deltaRiflessi, int deltaConcentrazione, int deltaTecnica, int deltaResistenza) {
        riflessi = limita(riflessi + deltaRiflessi);
        concentrazione = limita(concentrazione + deltaConcentrazione);
        tecnicaPartenza = limita(tecnicaPartenza + deltaTecnica);
        resistenza = limita(resistenza + deltaResistenza);
    }

    /** Punteggio complessivo (media dei quattro valori): usato per confrontare piloti e avversari. */
    public int punteggioComplessivo() {
        return (int) Math.round(
                IntStream.of(riflessi, concentrazione, tecnicaPartenza, resistenza)
                        .average()
                        .orElse(0)
        );
    }

    public Statistiche copia() {
        return new Statistiche(riflessi, concentrazione, tecnicaPartenza, resistenza);
    }

    @Override
    public String toString() {
        return "Riflessi=" + riflessi + ", Concentrazione=" + concentrazione
                + ", Tecnica partenza=" + tecnicaPartenza + ", Resistenza=" + resistenza;
    }
}
