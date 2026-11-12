package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.ObiettivoSponsor;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoGara;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoPartenza;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggiorna gli obiettivi sponsor dopo ogni gara e paga le ricompense.
 *
 * <p>È separato dal motore di gara perché le due cose cambiano per motivi
 * diversi: come si disputa una gara da una parte, come lo sponsor premia
 * il pilota dall'altra (Singola Responsabilità).</p>
 */
public final class GestoreObiettivi {

    /**
     * Registra la gara su tutti gli obiettivi della carriera. Per quelli
     * completati proprio ora accredita la ricompensa al pilota e lo annota
     * nello storico.
     *
     * @return gli obiettivi completati con questa gara (può essere vuoto)
     */
    public List<ObiettivoSponsor> registraGara(Carriera carriera, RisultatoPartenza partenza, RisultatoGara gara) {
        List<ObiettivoSponsor> appenaCompletati = new ArrayList<>();
        for (ObiettivoSponsor obiettivo : carriera.getObiettivi()) {
            if (obiettivo.registraGara(partenza, gara)) {
                premia(carriera, obiettivo);
                appenaCompletati.add(obiettivo);
            }
        }
        return appenaCompletati;
    }

    private static void premia(Carriera carriera, ObiettivoSponsor obiettivo) {
        carriera.getPilota().aggiungiDenaro(obiettivo.getRicompensa());
        carriera.aggiungiRisultato("Obiettivo sponsor completato: " + obiettivo.getDescrizione()
                + " (+" + obiettivo.getRicompensa() + " crediti)");
    }
}
