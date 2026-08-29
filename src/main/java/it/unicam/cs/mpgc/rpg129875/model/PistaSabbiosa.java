package it.unicam.cs.mpgc.rpg129875.model;

/** Terreno sabbioso: partenza più lunga e imprevedibile. */
public final class PistaSabbiosa implements CondizionePista {

    @Override
    public String nome() {
        return "Pista sabbiosa";
    }

    @Override
    public long attesaMinimaMs() {
        return 1800;
    }

    @Override
    public long attesaMassimaMs() {
        return 5000;
    }

    @Override
    public long sogliaPerfettaMs() {
        return 180;
    }

    @Override
    public long sogliaOttimaMs() {
        return 300;
    }

    @Override
    public long sogliaBuonaMs() {
        return 450;
    }
}
