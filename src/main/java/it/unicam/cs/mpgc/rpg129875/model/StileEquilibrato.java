package it.unicam.cs.mpgc.rpg129875.model;

/** Nessun punto debole evidente: valori bilanciati su tutte le statistiche. */
public final class StileEquilibrato implements StileDiGuida {

    @Override
    public String codice() {
        return "EQUILIBRATO";
    }

    @Override
    public String nome() {
        return "Equilibrato";
    }

    @Override
    public String descrizione() {
        return "Nessun punto debole evidente: valori bilanciati su tutte le statistiche.";
    }

    @Override
    public Statistiche statisticheIniziali() {
        return new Statistiche(60, 60, 60, 60);
    }
}
