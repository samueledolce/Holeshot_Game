package it.unicam.cs.mpgc.rpg129875.model;

/** Terreno fangoso: aderenza ridotta, soglie di reazione più larghe. */
public final class PistaFangosa implements CondizionePista {

    @Override
    public String nome() {
        return "Pista fangosa";
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
        return 200;
    }

    @Override
    public long sogliaOttimaMs() {
        return 320;
    }

    @Override
    public long sogliaBuonaMs() {
        return 480;
    }
}
