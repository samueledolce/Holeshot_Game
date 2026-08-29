package it.unicam.cs.mpgc.rpg129875.model;

/**
 * Un avversario semplificato: non è un {@link Pilota} completo, ma un
 * pacchetto di dati sufficiente a determinare il suo piazzamento in gara
 * (nome e prestazione complessiva, già comprensiva di partenza e ritmo
 * gara).
 */
public final class Avversario {

    private final String nome;
    private final int prestazione;

    public Avversario(String nome, int prestazione) {
        this.nome = nome;
        this.prestazione = prestazione;
    }

    public String getNome() {
        return nome;
    }

    public int getPrestazione() {
        return prestazione;
    }
}
