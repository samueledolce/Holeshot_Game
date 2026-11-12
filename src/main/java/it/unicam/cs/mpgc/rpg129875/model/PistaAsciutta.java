package it.unicam.cs.mpgc.rpg129875.model;

/** Condizioni di pista standard, terreno asciutto e compatto. */
public final class PistaAsciutta implements CondizionePista {

    @Override
    public String codice() {
        return "ASCIUTTA";
    }

    @Override
    public String nome() {
        return "Pista asciutta";
    }

    @Override
    public long attesaMinimaMs() {
        return 1500;
    }

    @Override
    public long attesaMassimaMs() {
        return 4500;
    }

    @Override
    public long sogliaPerfettaMs() {
        return 150;
    }

    @Override
    public long sogliaOttimaMs() {
        return 250;
    }

    @Override
    public long sogliaBuonaMs() {
        return 400;
    }
}
