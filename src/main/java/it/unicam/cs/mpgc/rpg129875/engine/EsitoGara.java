package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.model.Abilita;
import it.unicam.cs.mpgc.rpg129875.model.ObiettivoSponsor;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoGara;

import java.util.List;

/** Esito completo di una gara disputata: piazzamento, ricompense e obiettivi sponsor completati. */
public final class EsitoGara {

    private final RisultatoGara risultatoGara;
    private final int esperienzaGuadagnata;
    private final int denaroGuadagnato;
    private final List<Abilita> nuoveAbilitaSbloccate;
    private final List<ObiettivoSponsor> obiettiviCompletati;

    public EsitoGara(RisultatoGara risultatoGara, int esperienzaGuadagnata, int denaroGuadagnato,
                     List<Abilita> nuoveAbilitaSbloccate, List<ObiettivoSponsor> obiettiviCompletati) {
        this.risultatoGara = risultatoGara;
        this.esperienzaGuadagnata = esperienzaGuadagnata;
        this.denaroGuadagnato = denaroGuadagnato;
        this.nuoveAbilitaSbloccate = List.copyOf(nuoveAbilitaSbloccate);
        this.obiettiviCompletati = List.copyOf(obiettiviCompletati);
    }

    public RisultatoGara getRisultatoGara() {
        return risultatoGara;
    }

    public int getEsperienzaGuadagnata() {
        return esperienzaGuadagnata;
    }

    /** I crediti del premio di gara; le ricompense degli obiettivi sono indicate in ciascun obiettivo. */
    public int getDenaroGuadagnato() {
        return denaroGuadagnato;
    }

    public List<Abilita> getNuoveAbilitaSbloccate() {
        return nuoveAbilitaSbloccate;
    }

    public List<ObiettivoSponsor> getObiettiviCompletati() {
        return obiettiviCompletati;
    }
}
