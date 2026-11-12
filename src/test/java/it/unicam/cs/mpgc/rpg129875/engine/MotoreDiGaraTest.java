package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.DatiDiProva;
import it.unicam.cs.mpgc.rpg129875.model.Campionato;
import it.unicam.cs.mpgc.rpg129875.model.Avversario;
import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.CriterioQualitaPartenza;
import it.unicam.cs.mpgc.rpg129875.model.Gara;
import it.unicam.cs.mpgc.rpg129875.model.Moto;
import it.unicam.cs.mpgc.rpg129875.model.ObiettivoSponsor;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import it.unicam.cs.mpgc.rpg129875.model.QualitaPartenza;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoPartenza;
import it.unicam.cs.mpgc.rpg129875.model.StileAggressivo;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MotoreDiGaraTest {

    @Test
    void disputareUnaGaraAggiornaCarrieraECampionato() {
        Moto moto = DatiDiProva.motoPotente();
        Pilota pilota = new Pilota("Campione", new StileAggressivo(), moto);
        Campionato campionato = DatiDiProva.campionatoDiCinqueTappe();
        Carriera carriera = new Carriera(pilota, campionato);

        MotoreDiGara motore = new MotoreDiGara(
                new GeneratoreAvversariCasuale(new Random(7)),
                new CalcolatorePiazzamentoStandard(new Random(7)),
                new GestoreProgressione(),
                new GestoreObiettivi()
        );

        Gara primaGara = campionato.tappaCorrente();
        RisultatoPartenza partenza = new RisultatoPartenza(120, QualitaPartenza.PERFETTA);

        EsitoGara esito = motore.disputa(carriera, primaGara, partenza);

        assertEquals(1, campionato.getIndiceTappaCorrente());
        assertTrue(pilota.getDenaro() > 0);
        assertEquals(1, carriera.getStoricoRisultati().size());
        assertTrue(esito.getRisultatoGara().getPosizioneGiocatore() >= 1);
        assertTrue(esito.getRisultatoGara().getPosizioneGiocatore() <= esito.getRisultatoGara().getNumeroPartecipanti());
    }

    @Test
    void laGaraFaAvanzareGliObiettiviSponsorEPagaLaRicompensa() {
        Pilota pilota = new Pilota("Campione", new StileAggressivo(), DatiDiProva.motoPotente());
        ObiettivoSponsor unaPartenzaPerfetta = new ObiettivoSponsor("Fai una partenza perfetta",
                new CriterioQualitaPartenza("PARTENZA_PERFETTA", QualitaPartenza.PERFETTA), 1, 300);
        Carriera carriera = new Carriera(pilota, DatiDiProva.campionatoDiCinqueTappe(), List.of(unaPartenzaPerfetta));

        // Un solo avversario debolissimo: il giocatore vince di sicuro, quindi il premio di gara è noto.
        GeneratoreAvversari unSoloAvversarioDebole = (gara, livello) -> List.of(new Avversario("Ultimo", 1));
        MotoreDiGara motore = new MotoreDiGara(
                unSoloAvversarioDebole,
                new CalcolatorePiazzamentoStandard(new Random(7)),
                new GestoreProgressione(),
                new GestoreObiettivi()
        );

        EsitoGara esito = motore.disputa(carriera, carriera.getCampionatoCorrente().tappaCorrente(),
                new RisultatoPartenza(120, QualitaPartenza.PERFETTA));

        assertEquals(1, esito.getObiettiviCompletati().size());
        assertTrue(unaPartenzaPerfetta.isCompletato());
        assertEquals(esito.getDenaroGuadagnato() + 300, pilota.getDenaro());
        assertEquals(2, carriera.getStoricoRisultati().size());
    }
}
