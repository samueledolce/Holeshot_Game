package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.model.CondizionePista;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import it.unicam.cs.mpgc.rpg129875.model.QualitaPartenza;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoPartenza;

/**
 * Trasforma il tempo di reazione misurato dall'interfaccia grafica (in
 * millisecondi) in un {@link RisultatoPartenza}, applicando le abilità del
 * pilota e le soglie della pista corrente.
 */
public final class RisolutorePartenza {

    /**
     * @param pilota          il pilota che ha effettuato la partenza
     * @param pista           le condizioni della pista corrente
     * @param tempoReazioneMs tempo misurato tra apertura del cancelletto e
     *                        pressione del pilota; un valore negativo
     *                        indica una falsa partenza (pressione prima
     *                        dell'apertura)
     */
    public RisultatoPartenza risolvi(Pilota pilota, CondizionePista pista, long tempoReazioneMs) {
        if (tempoReazioneMs < 0) {
            return new RisultatoPartenza(tempoReazioneMs, QualitaPartenza.FALSA_PARTENZA);
        }
        long tempoCorretto = pilota.applicaAbilitaAlTempo(tempoReazioneMs);
        return new RisultatoPartenza(tempoCorretto, pista.classifica(tempoCorretto));
    }
}
