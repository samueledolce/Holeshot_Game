package it.unicam.cs.mpgc.rpg129875.model;

/**
 * Una moto da cross utilizzabile dal pilota. Incide sulla partenza tramite
 * {@link #getBonusPartenza()} (elettronica/frizione più o meno pronta) e sul
 * resto della gara tramite potenza e maneggevolezza.
 */
public final class Moto {

    private final String nome;
    private final CategoriaMoto categoria;
    private final int potenza;
    private final int maneggevolezza;
    private final int bonusPartenza;

    public Moto(String nome, CategoriaMoto categoria, int potenza, int maneggevolezza, int bonusPartenza) {
        this.nome = nome;
        this.categoria = categoria;
        this.potenza = potenza;
        this.maneggevolezza = maneggevolezza;
        this.bonusPartenza = bonusPartenza;
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

    public int punteggioComplessivo() {
        return (potenza + maneggevolezza + bonusPartenza) / 3;
    }

    @Override
    public String toString() {
        return nome + " (" + categoria.getEtichetta() + ")";
    }
}
