package it.unicam.cs.mpgc.rpg129875.model;

/**
 * Una singola gara: la pista su cui si corre, il numero di avversari
 * attesi e la difficoltà, cioè quanto sono forti gli avversari rispetto a
 * una gara normale. Non contiene la logica di risoluzione (calcolo del
 * piazzamento): quella è responsabilità del motore di gara (principio di
 * Singola Responsabilità).
 */
public final class Gara {

    private final String nomeTappa;
    private final CondizionePista pista;
    private final int numeroAvversari;
    private final int difficolta;

    /** Crea una gara di difficoltà normale. */
    public Gara(String nomeTappa, CondizionePista pista, int numeroAvversari) {
        this(nomeTappa, pista, numeroAvversari, 0);
    }

    public Gara(String nomeTappa, CondizionePista pista, int numeroAvversari, int difficolta) {
        this.nomeTappa = nomeTappa;
        this.pista = pista;
        this.numeroAvversari = numeroAvversari;
        this.difficolta = difficolta;
    }

    public String getNomeTappa() {
        return nomeTappa;
    }

    public CondizionePista getPista() {
        return pista;
    }

    public int getNumeroAvversari() {
        return numeroAvversari;
    }

    /** Punti di prestazione in più (o in meno, se negativa) degli avversari rispetto a una gara normale. */
    public int getDifficolta() {
        return difficolta;
    }
}
