package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.model.Abilita;
import it.unicam.cs.mpgc.rpg129875.model.Avversario;
import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.Gara;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoGara;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoPartenza;

import java.util.List;

/**
 * Orchestra lo svolgimento di una gara una volta nota la partenza del
 * giocatore: genera gli avversari, calcola il piazzamento, assegna
 * ricompense e aggiorna il campionato.
 *
 * <p>Dipende solo dalle astrazioni {@link GeneratoreAvversari},
 * {@link CalcolatorePiazzamento} e {@link GestoreProgressione}, non dalle
 * loro implementazioni concrete (Dependency Inversion Principle).</p>
 */
public final class MotoreDiGara {

    private static final int DENARO_BASE_PER_GARA = 50;
    private static final int BONUS_DENARO_PODIO = 100;
    private static final int ESPERIENZA_BASE_PER_GARA = 20;
    private static final int ESPERIENZA_PER_POSIZIONE_SUPERATA = 8;

    private final GeneratoreAvversari generatoreAvversari;
    private final CalcolatorePiazzamento calcolatorePiazzamento;
    private final GestoreProgressione gestoreProgressione;

    public MotoreDiGara(GeneratoreAvversari generatoreAvversari,
                         CalcolatorePiazzamento calcolatorePiazzamento,
                         GestoreProgressione gestoreProgressione) {
        this.generatoreAvversari = generatoreAvversari;
        this.calcolatorePiazzamento = calcolatorePiazzamento;
        this.gestoreProgressione = gestoreProgressione;
    }

    public EsitoGara disputa(Carriera carriera, Gara gara, RisultatoPartenza partenza) {
        Pilota pilota = carriera.getPilota();
        List<Avversario> avversari = generatoreAvversari.generaPer(gara, pilota.getLivello());

        RisultatoGara risultato = calcolatorePiazzamento.calcola(pilota, partenza, avversari);

        int esperienzaGuadagnata = calcolaEsperienza(risultato);
        int denaroGuadagnato = calcolaDenaro(risultato);

        pilota.aggiungiDenaro(denaroGuadagnato);
        List<Abilita> nuoveAbilita = gestoreProgressione.assegnaEsperienza(pilota, esperienzaGuadagnata);

        carriera.getCampionatoCorrente().registraPiazzamento(risultato.getPosizioneGiocatore());
        carriera.aggiungiRisultato(gara.getNomeTappa() + ": " + risultato.getPosizioneGiocatore()
                + "° su " + risultato.getNumeroPartecipanti());

        return new EsitoGara(risultato, esperienzaGuadagnata, denaroGuadagnato, nuoveAbilita);
    }

    private int calcolaEsperienza(RisultatoGara risultato) {
        int posizioniDietro = risultato.getNumeroPartecipanti() - risultato.getPosizioneGiocatore();
        return ESPERIENZA_BASE_PER_GARA + posizioniDietro * ESPERIENZA_PER_POSIZIONE_SUPERATA;
    }

    private int calcolaDenaro(RisultatoGara risultato) {
        int bonusPodio = risultato.isPodio() ? BONUS_DENARO_PODIO : 0;
        return DENARO_BASE_PER_GARA + bonusPodio;
    }
}
