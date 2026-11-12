package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.DatiDiProva;
import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.CriterioPiazzamento;
import it.unicam.cs.mpgc.rpg129875.model.CriterioQualitaPartenza;
import it.unicam.cs.mpgc.rpg129875.model.ObiettivoSponsor;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import it.unicam.cs.mpgc.rpg129875.model.QualitaPartenza;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoGara;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoPartenza;
import it.unicam.cs.mpgc.rpg129875.model.StileEquilibrato;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GestoreObiettiviTest {

    private static final RisultatoPartenza PARTENZA_PERFETTA = new RisultatoPartenza(100, QualitaPartenza.PERFETTA);
    private static final RisultatoPartenza PARTENZA_LENTA = new RisultatoPartenza(500, QualitaPartenza.LENTA);

    private final GestoreObiettivi gestore = new GestoreObiettivi();

    private static Carriera carrieraConObiettivi() {
        Pilota pilota = new Pilota("Ada", new StileEquilibrato(), DatiDiProva.motoMedia());
        return new Carriera(pilota, DatiDiProva.campionatoDiCinqueTappe(), List.of(
                new ObiettivoSponsor("Vinci una gara", new CriterioPiazzamento("VITTORIA", 1), 1, 200),
                new ObiettivoSponsor("Fai 2 partenze perfette",
                        new CriterioQualitaPartenza("PARTENZA_PERFETTA", QualitaPartenza.PERFETTA), 2, 300)));
    }

    private static RisultatoGara arrivoIn(int posizione) {
        return new RisultatoGara(List.of("A", "B", "C", "D"), posizione);
    }

    @Test
    void completareUnObiettivoAccreditaLaRicompensaELoAnnotaNelloStorico() {
        Carriera carriera = carrieraConObiettivi();

        List<ObiettivoSponsor> completati = gestore.registraGara(carriera, PARTENZA_LENTA, arrivoIn(1));

        assertEquals(1, completati.size());
        assertEquals("Vinci una gara", completati.get(0).getDescrizione());
        assertEquals(200, carriera.getPilota().getDenaro());
        assertTrue(carriera.getStoricoRisultati().get(0).contains("Vinci una gara"));
    }

    @Test
    void unaGaraPuoFarAvanzarePiuObiettiviSenzaCompletarli() {
        Carriera carriera = carrieraConObiettivi();

        List<ObiettivoSponsor> completati = gestore.registraGara(carriera, PARTENZA_PERFETTA, arrivoIn(3));

        assertTrue(completati.isEmpty());
        assertEquals(0, carriera.getPilota().getDenaro());
        assertEquals(1, carriera.getObiettivi().get(1).getProgresso());
    }

    @Test
    void laRicompensaVienePagataUnaVoltaSola() {
        Carriera carriera = carrieraConObiettivi();
        gestore.registraGara(carriera, PARTENZA_LENTA, arrivoIn(1));

        List<ObiettivoSponsor> completati = gestore.registraGara(carriera, PARTENZA_LENTA, arrivoIn(1));

        assertTrue(completati.isEmpty());
        assertEquals(200, carriera.getPilota().getDenaro());
    }

    @Test
    void unaGaraPuoCompletarePiuObiettiviInsieme() {
        Carriera carriera = carrieraConObiettivi();
        gestore.registraGara(carriera, PARTENZA_PERFETTA, arrivoIn(4));

        List<ObiettivoSponsor> completati = gestore.registraGara(carriera, PARTENZA_PERFETTA, arrivoIn(1));

        assertEquals(2, completati.size());
        assertEquals(500, carriera.getPilota().getDenaro());
    }
}
