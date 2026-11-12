package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.DatiDiProva;
import it.unicam.cs.mpgc.rpg129875.model.Campionato;
import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.CatalogoCampionati;
import it.unicam.cs.mpgc.rpg129875.model.StileTecnico;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GestoreCarrieraTest {

    /** Catalogo finto con due soli campionati di una tappa: "Primo" e poi "Secondo". */
    private static final class DueCampionati implements CatalogoCampionati {

        @Override
        public Campionato primo() {
            return DatiDiProva.campionatoDiUnaTappa("Primo");
        }

        @Override
        public Optional<Campionato> successivoA(String nomeCampionato) {
            return nomeCampionato.equals("Primo")
                    ? Optional.of(DatiDiProva.campionatoDiUnaTappa("Secondo"))
                    : Optional.empty();
        }
    }

    private final GestoreCarriera gestore = new GestoreCarriera(new DueCampionati());

    private Carriera nuovaCarriera() {
        return gestore.nuovaCarriera("Ada", new StileTecnico(), DatiDiProva.motoMedia());
    }

    @Test
    void unaNuovaCarrieraParteDalPrimoCampionato() {
        Carriera carriera = nuovaCarriera();

        assertEquals("Ada", carriera.getPilota().getNome());
        assertEquals("Primo", carriera.getCampionatoCorrente().getNome());
        assertEquals(0, carriera.getCampionatoCorrente().getIndiceTappaCorrente());
    }

    @Test
    void ilNomeDelPilotaVieneRipulitoDagliSpazi() {
        Carriera carriera = gestore.nuovaCarriera("  Ada  ", new StileTecnico(), DatiDiProva.motoMedia());

        assertEquals("Ada", carriera.getPilota().getNome());
    }

    @Test
    void unPilotaSenzaNomeVieneRifiutato() {
        assertThrows(IllegalArgumentException.class,
                () -> gestore.nuovaCarriera("   ", new StileTecnico(), DatiDiProva.motoMedia()));
    }

    @Test
    void finoAllaFineDelCampionatoNonSiPuoPassareAlSuccessivo() {
        Carriera carriera = nuovaCarriera();

        assertTrue(gestore.prossimoCampionato(carriera).isEmpty());
        assertFalse(gestore.passaAlProssimoCampionato(carriera));
        assertEquals("Primo", carriera.getCampionatoCorrente().getNome());
    }

    @Test
    void conclusoUnCampionatoSiPassaAlSuccessivoEloStoricoLoRicorda() {
        Carriera carriera = nuovaCarriera();
        carriera.getCampionatoCorrente().registraPiazzamento(1);

        assertTrue(gestore.passaAlProssimoCampionato(carriera));

        assertEquals("Secondo", carriera.getCampionatoCorrente().getNome());
        assertEquals(0, carriera.getCampionatoCorrente().getPuntiGiocatore());
        assertEquals("Primo concluso con 25 punti", carriera.getStoricoRisultati().get(0));
    }

    @Test
    void dopoLUltimoCampionatoNonCeNeSonoAltri() {
        Carriera carriera = nuovaCarriera();
        carriera.getCampionatoCorrente().registraPiazzamento(1);
        gestore.passaAlProssimoCampionato(carriera);
        carriera.getCampionatoCorrente().registraPiazzamento(2);

        assertTrue(gestore.prossimoCampionato(carriera).isEmpty());
        assertFalse(gestore.passaAlProssimoCampionato(carriera));
        assertEquals("Secondo", carriera.getCampionatoCorrente().getNome());
    }
}
