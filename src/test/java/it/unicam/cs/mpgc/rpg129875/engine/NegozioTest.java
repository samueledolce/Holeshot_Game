package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.model.CatalogoMoto;
import it.unicam.cs.mpgc.rpg129875.model.CategoriaMoto;
import it.unicam.cs.mpgc.rpg129875.model.Moto;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import it.unicam.cs.mpgc.rpg129875.model.StileEquilibrato;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NegozioTest {

    private static final Moto DI_SERIE = new Moto("Di serie", CategoriaMoto.MX_125, 40, 80, 65);
    private static final Moto RACING = new Moto("Racing", CategoriaMoto.MX_250, 76, 76, 68, 400);
    private static final Moto FACTORY = new Moto("Factory", CategoriaMoto.MX_450, 100, 74, 78, 900);

    private final CatalogoMoto catalogo = () -> List.of(DI_SERIE, RACING, FACTORY);
    private final Negozio negozio = new Negozio(catalogo);

    private static Pilota pilotaCon(Moto moto, int crediti) {
        Pilota pilota = new Pilota("Ada", new StileEquilibrato(), moto);
        pilota.aggiungiDenaro(crediti);
        return pilota;
    }

    private List<String> nomiInVendita(Pilota pilota) {
        return negozio.offertePer(pilota).stream()
                .map(offerta -> offerta.moto().getNome())
                .collect(Collectors.toList());
    }

    @Test
    void sonoInVenditaSoloLeMotoAPagamentoDiverseDaQuellaInUso() {
        assertEquals(List.of("Racing", "Factory"), nomiInVendita(pilotaCon(DI_SERIE, 0)));
        assertEquals(List.of("Factory"), nomiInVendita(pilotaCon(RACING, 0)));
    }

    @Test
    void unaMotoDiSerieNonValeNullaInPermuta() {
        assertEquals(0, negozio.valorePermuta(DI_SERIE));
        assertEquals(400, negozio.costoPer(pilotaCon(DI_SERIE, 0), RACING));
    }

    @Test
    void laMotoCedutaInPermutaValeMetaDelSuoPrezzo() {
        assertEquals(200, negozio.valorePermuta(RACING));
        assertEquals(700, negozio.costoPer(pilotaCon(RACING, 0), FACTORY));
    }

    @Test
    void passandoAUnaMotoMenoCostosaNonSiPagaENonSiVieneRimborsati() {
        Pilota pilota = pilotaCon(FACTORY, 30);

        assertEquals(0, negozio.costoPer(pilota, RACING));
        assertEquals(EsitoAcquisto.COMPLETATO, negozio.acquista(pilota, RACING));
        assertEquals(30, pilota.getDenaro());
    }

    @Test
    void lOffertaDiceSeIlPilotaPuoPermettersela() {
        List<OffertaMoto> offerte = negozio.offertePer(pilotaCon(DI_SERIE, 500));

        assertTrue(offerte.get(0).allaPortata());
        assertFalse(offerte.get(1).allaPortata());
    }

    @Test
    void conCreditiSufficientiSiPagaIlCostoESiCambiaMoto() {
        Pilota pilota = pilotaCon(RACING, 750);

        assertEquals(EsitoAcquisto.COMPLETATO, negozio.acquista(pilota, FACTORY));

        assertEquals(FACTORY, pilota.getMotoAttuale());
        assertEquals(50, pilota.getDenaro());
    }

    @Test
    void senzaCreditiSufficientiNonCambiaNulla() {
        Pilota pilota = pilotaCon(DI_SERIE, 399);

        assertEquals(EsitoAcquisto.CREDITI_INSUFFICIENTI, negozio.acquista(pilota, RACING));

        assertEquals(DI_SERIE, pilota.getMotoAttuale());
        assertEquals(399, pilota.getDenaro());
    }

    @Test
    void nonSiPuoComprareLaMotoCheSiStaGiaUsando() {
        Pilota pilota = pilotaCon(RACING, 1000);

        assertEquals(EsitoAcquisto.MOTO_GIA_IN_USO, negozio.acquista(pilota, RACING));
        assertEquals(1000, pilota.getDenaro());
    }

    @Test
    void laMotoRicaricataDaUnSalvataggioVieneRiconosciutaComeQuellaInUso() {
        Moto copiaDellaRacing = new Moto("Racing", CategoriaMoto.MX_250, 76, 76, 68, 400);

        assertEquals(List.of("Factory"), nomiInVendita(pilotaCon(copiaDellaRacing, 0)));
    }

    @Test
    void unaMotoFuoriCatalogoNonSiPuoComprare() {
        Moto sconosciuta = new Moto("Sconosciuta", CategoriaMoto.MX_450, 99, 99, 99, 1);

        assertThrows(IllegalArgumentException.class, () -> negozio.acquista(pilotaCon(DI_SERIE, 1000), sconosciuta));
    }
}
