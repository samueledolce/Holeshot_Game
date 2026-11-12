package it.unicam.cs.mpgc.rpg129875.model;

/** Conta le gare concluse entro la posizione indicata (1 = solo le vittorie, 3 = i podi). */
public final class CriterioPiazzamento implements CriterioObiettivo {

    private final String codice;
    private final int posizioneMassima;

    public CriterioPiazzamento(String codice, int posizioneMassima) {
        this.codice = codice;
        this.posizioneMassima = posizioneMassima;
    }

    @Override
    public String codice() {
        return codice;
    }

    @Override
    public boolean soddisfattoDa(RisultatoPartenza partenza, RisultatoGara gara) {
        return gara.getPosizioneGiocatore() <= posizioneMassima;
    }
}
