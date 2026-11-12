package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.DatiDiProva;
import it.unicam.cs.mpgc.rpg129875.model.Gara;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import it.unicam.cs.mpgc.rpg129875.model.PistaAsciutta;
import it.unicam.cs.mpgc.rpg129875.model.QualitaPartenza;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoPartenza;
import it.unicam.cs.mpgc.rpg129875.model.StileEquilibrato;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica l'idea alla base del gioco: è la partenza a decidere la gara.
 * Disputa molte gare con lo stesso tipo di partenza e controlla quante ne
 * vince un pilota appena creato, con una moto di serie.
 */
class BilanciamentoGaraTest {

    private static final int GARE_SIMULATE = 500;
    private static final int TEMPO_QUALSIASI_MS = 200;

    private static double percentualeVittorie(QualitaPartenza qualita) {
        Random random = new Random(2026);
        GeneratoreAvversari generatore = new GeneratoreAvversariCasuale(random);
        CalcolatorePiazzamento calcolatore = new CalcolatorePiazzamentoStandard(random);
        Gara gara = new Gara("Gara di prova", new PistaAsciutta(), 6);
        Pilota esordiente = new Pilota("Esordiente", new StileEquilibrato(), DatiDiProva.motoMedia());
        RisultatoPartenza partenza = new RisultatoPartenza(TEMPO_QUALSIASI_MS, qualita);

        int vittorie = 0;
        for (int i = 0; i < GARE_SIMULATE; i++) {
            if (calcolatore.calcola(esordiente, partenza, generatore.generaPer(gara, 1)).isVittoria()) {
                vittorie++;
            }
        }
        return 100.0 * vittorie / GARE_SIMULATE;
    }

    @Test
    void conUnaPartenzaLentaNonSiVinceQuasiMai() {
        assertTrue(percentualeVittorie(QualitaPartenza.LENTA) < 5);
    }

    @Test
    void conUnaPartenzaPerfettaSiVinceQuasiSempre() {
        assertTrue(percentualeVittorie(QualitaPartenza.PERFETTA) > 80);
    }

    @Test
    void unaPartenzaMiglioreFaVincerePiuSpesso() {
        double conBuona = percentualeVittorie(QualitaPartenza.BUONA);
        double conOttima = percentualeVittorie(QualitaPartenza.OTTIMA);
        double conPerfetta = percentualeVittorie(QualitaPartenza.PERFETTA);

        assertTrue(conBuona < conOttima);
        assertTrue(conOttima < conPerfetta);
    }
}
