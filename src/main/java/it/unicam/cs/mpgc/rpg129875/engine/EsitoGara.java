package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.model.Abilita;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoGara;

import java.util.List;

/** Esito completo di una gara disputata: piazzamento e ricompense ottenute. */
public final class EsitoGara {

    private final RisultatoGara risultatoGara;
    private final int esperienzaGuadagnata;
    private final int denaroGuadagnato;
    private final List<Abilita> nuoveAbilitaSbloccate;

    public EsitoGara(RisultatoGara risultatoGara, int esperienzaGuadagnata, int denaroGuadagnato,
                      List<Abilita> nuoveAbilitaSbloccate) {
        this.risultatoGara = risultatoGara;
        this.esperienzaGuadagnata = esperienzaGuadagnata;
        this.denaroGuadagnato = denaroGuadagnato;
        this.nuoveAbilitaSbloccate = List.copyOf(nuoveAbilitaSbloccate);
    }

    public RisultatoGara getRisultatoGara() {
        return risultatoGara;
    }

    public int getEsperienzaGuadagnata() {
        return esperienzaGuadagnata;
    }

    public int getDenaroGuadagnato() {
        return denaroGuadagnato;
    }

    public List<Abilita> getNuoveAbilitaSbloccate() {
        return nuoveAbilitaSbloccate;
    }
}
