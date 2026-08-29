package it.unicam.cs.mpgc.rpg129875.model;

/** Studia la partenza nei dettagli: alta concentrazione e tecnica, riflessi nella media. */
public final class StileTecnico implements StileDiGuida {

    @Override
    public String nome() {
        return "Tecnico";
    }

    @Override
    public String descrizione() {
        return "Studia la partenza nei minimi dettagli: alta concentrazione e tecnica di "
                + "partenza, riflessi nella media.";
    }

    @Override
    public Statistiche statisticheIniziali() {
        return new Statistiche(55, 75, 70, 55);
    }
}
