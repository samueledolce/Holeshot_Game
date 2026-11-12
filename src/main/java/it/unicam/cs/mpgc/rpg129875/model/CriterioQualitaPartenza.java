package it.unicam.cs.mpgc.rpg129875.model;

/** Conta le gare in cui la partenza è stata almeno della qualità indicata. */
public final class CriterioQualitaPartenza implements CriterioObiettivo {

    private final String codice;
    private final QualitaPartenza qualitaMinima;

    public CriterioQualitaPartenza(String codice, QualitaPartenza qualitaMinima) {
        this.codice = codice;
        this.qualitaMinima = qualitaMinima;
    }

    @Override
    public String codice() {
        return codice;
    }

    @Override
    public boolean soddisfattoDa(RisultatoPartenza partenza, RisultatoGara gara) {
        return partenza.getQualita().getPunteggio() >= qualitaMinima.getPunteggio();
    }
}
