package it.unicam.cs.mpgc.rpg129875.model;

import java.util.Objects;

/**
 * Una moto da cross utilizzabile dal pilota: potenza, maneggevolezza e
 * prontezza in partenza ne determinano la qualità complessiva, che incide
 * sul piazzamento in gara. Il prezzo è espresso in crediti sponsor; le moto
 * di serie non costano nulla e sono quelle tra cui si sceglie a inizio
 * carriera.
 */
public final class Moto {

    private final String nome;
    private final CategoriaMoto categoria;
    private final int potenza;
    private final int maneggevolezza;
    private final int bonusPartenza;
    private final int prezzo;

    /** Crea una moto di serie, cioè gratuita. */
    public Moto(String nome, CategoriaMoto categoria, int potenza, int maneggevolezza, int bonusPartenza) {
        this(nome, categoria, potenza, maneggevolezza, bonusPartenza, 0);
    }

    public Moto(String nome, CategoriaMoto categoria, int potenza, int maneggevolezza, int bonusPartenza,
                int prezzo) {
        this.nome = nome;
        this.categoria = categoria;
        this.potenza = potenza;
        this.maneggevolezza = maneggevolezza;
        this.bonusPartenza = bonusPartenza;
        this.prezzo = prezzo;
    }

    public String getNome() {
        return nome;
    }

    public CategoriaMoto getCategoria() {
        return categoria;
    }

    public int getPotenza() {
        return potenza;
    }

    public int getManeggevolezza() {
        return maneggevolezza;
    }

    public int getBonusPartenza() {
        return bonusPartenza;
    }

    public int getPrezzo() {
        return prezzo;
    }

    public boolean isDiSerie() {
        return prezzo == 0;
    }

    public int punteggioComplessivo() {
        return (potenza + maneggevolezza + bonusPartenza) / 3;
    }

    /**
     * Due moto sono la stessa moto se hanno lo stesso nome, che identifica
     * il modello. Serve per riconoscere la moto del pilota tra quelle del
     * catalogo anche dopo averla ricaricata da un salvataggio, quando è un
     * oggetto diverso con gli stessi dati.
     */
    @Override
    public boolean equals(Object altro) {
        if (this == altro) {
            return true;
        }
        if (!(altro instanceof Moto)) {
            return false;
        }
        return nome.equals(((Moto) altro).nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }

    @Override
    public String toString() {
        return nome + " (" + categoria.getEtichetta() + ")";
    }
}
