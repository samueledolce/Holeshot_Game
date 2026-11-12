package it.unicam.cs.mpgc.rpg129875.model;

/** Parte forte e rischia tutto al cancelletto: riflessi e tecnica elevati, poca resistenza. */
public final class StileAggressivo implements StileDiGuida {

    @Override
    public String codice() {
        return "AGGRESSIVO";
    }

    @Override
    public String nome() {
        return "Aggressivo";
    }

    @Override
    public String descrizione() {
        return "Parte forte e rischia tutto al cancelletto: riflessi e tecnica di partenza "
                + "elevati, ma meno resistenza sulla distanza.";
    }

    @Override
    public Statistiche statisticheIniziali() {
        return new Statistiche(75, 55, 70, 45);
    }
}
