package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.model.Avversario;
import it.unicam.cs.mpgc.rpg129875.model.CatalogoMoto;
import it.unicam.cs.mpgc.rpg129875.model.Moto;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import it.unicam.cs.mpgc.rpg129875.model.QualitaPartenza;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoGara;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoPartenza;
import it.unicam.cs.mpgc.rpg129875.model.StileAggressivo;
import it.unicam.cs.mpgc.rpg129875.model.StileEquilibrato;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalcolatorePiazzamentoStandardTest {

    @Test
    void unaPartenzaPerfettaPortaAUnaPosizioneMiglioreDiUnaFalsaPartenza() {
        Moto moto = CatalogoMoto.disponibiliPerNuovaCarriera().get(0);
        Pilota pilota = new Pilota("Test", new StileEquilibrato(), moto);
        CalcolatorePiazzamento calcolatore = new CalcolatorePiazzamentoStandard(new Random(42));

        RisultatoPartenza partenzaPerfetta = new RisultatoPartenza(100, QualitaPartenza.PERFETTA);
        RisultatoPartenza falsaPartenza = new RisultatoPartenza(-1, QualitaPartenza.FALSA_PARTENZA);

        // Prestazione fissa scelta apposta a metà tra le due fasce di prestazione
        // del giocatore (con e senza il bonus/penalità di partenza), così il test
        // è deterministico anche con la piccola componente casuale del calcolatore.
        List<Avversario> avversari = List.of(new Avversario("Avversario", 90));

        RisultatoGara conPartenzaPerfetta = calcolatore.calcola(pilota, partenzaPerfetta, avversari);
        RisultatoGara conFalsaPartenza = calcolatore.calcola(pilota, falsaPartenza, avversari);

        assertEquals(1, conPartenzaPerfetta.getPosizioneGiocatore());
        assertEquals(2, conFalsaPartenza.getPosizioneGiocatore());
    }

    @Test
    void ilVincitoreCompareInCimaAllaClassifica() {
        Moto moto = CatalogoMoto.disponibiliPerNuovaCarriera().get(0);
        Pilota pilota = new Pilota("Campione", new StileAggressivo(), moto);
        CalcolatorePiazzamento calcolatore = new CalcolatorePiazzamentoStandard(new Random(1));

        RisultatoPartenza partenza = new RisultatoPartenza(80, QualitaPartenza.PERFETTA);
        List<Avversario> avversari = List.of(new Avversario("Rivale1", 5), new Avversario("Rivale2", 10));

        RisultatoGara risultato = calcolatore.calcola(pilota, partenza, avversari);

        assertEquals("Campione", risultato.getClassificaOrdinata().get(0));
        assertEquals(1, risultato.getPosizioneGiocatore());
        assertEquals(3, risultato.getNumeroPartecipanti());
    }
}
