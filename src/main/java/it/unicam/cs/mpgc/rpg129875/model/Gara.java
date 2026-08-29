package it.unicam.cs.mpgc.rpg129875.model;

/**
 * Una singola gara: la pista su cui si corre e il numero di avversari
 * attesi. Non contiene la logica di risoluzione (calcolo del piazzamento):
 * quella è responsabilità del motore di gara (principio di Singola
 * Responsabilità).
 */
public final class Gara {

    private final String nomeTappa;
    private final CondizionePista pista;
    private final int numeroAvversari;

    public Gara(String nomeTappa, CondizionePista pista, int numeroAvversari) {
        this.nomeTappa = nomeTappa;
        this.pista = pista;
        this.numeroAvversari = numeroAvversari;
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
}
