package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.model.Abilita;
import it.unicam.cs.mpgc.rpg129875.model.Avversario;
import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.Gara;
import it.unicam.cs.mpgc.rpg129875.model.ObiettivoSponsor;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoGara;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoPartenza;

import java.util.List;

/**
 * Orchestra lo svolgimento di una gara una volta nota la partenza del
 * giocatore: genera gli avversari, calcola il piazzamento, assegna
 * ricompense, aggiorna il campionato e gli obiettivi sponsor.
 *
 * <p>Non contiene nessuna di queste regole: le delega ai collaboratori
 * ricevuti nel costruttore. Avversari e piazzamento sono dietro le
 * interfacce {@link GeneratoreAvversari} e {@link CalcolatorePiazzamento},
 * quindi si possono sostituire senza toccare questa classe (Dependency
 * Inversion Principle).</p>
 */
public final class MotoreDiGara {

    private static final int DENARO_BASE_PER_GARA = 50;
    private static final int BONUS_DENARO_PODIO = 100;
    private static final int ESPERIENZA_BASE_PER_GARA = 20;
    private static final int ESPERIENZA_PER_POSIZIONE_SUPERATA = 8;

    private final GeneratoreAvversari generatoreAvversari;
    private final CalcolatorePiazzamento calcolatorePiazzamento;
    private final GestoreProgressione gestoreProgressione;
    private final GestoreObiettivi gestoreObiettivi;

    public MotoreDiGara(GeneratoreAvversari generatoreAvversari,
                         CalcolatorePiazzamento calcolatorePiazzamento,
                         GestoreProgressione gestoreProgressione,
                         GestoreObiettivi gestoreObiettivi) {
        this.generatoreAvversari = generatoreAvversari;
        this.calcolatorePiazzamento = calcolatorePiazzamento;
        this.gestoreProgressione = gestoreProgressione;
        this.gestoreObiettivi = gestoreObiettivi;
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
        List<ObiettivoSponsor> obiettiviCompletati = gestoreObiettivi.registraGara(carriera, partenza, risultato);

        return new EsitoGara(risultato, esperienzaGuadagnata, denaroGuadagnato, nuoveAbilita, obiettiviCompletati);
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
