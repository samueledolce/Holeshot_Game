package it.unicam.cs.mpgc.rpg129875.persistence;

import it.unicam.cs.mpgc.rpg129875.model.Campionato;
import it.unicam.cs.mpgc.rpg129875.model.Moto;
import it.unicam.cs.mpgc.rpg129875.model.ObiettivoSponsor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogoJsonTest {

    private static final String CARTELLA_DI_PROVA = "/dati-di-prova/";

    /** Collauda i veri file dei contenuti: se qualcuno li modifica sbagliando, questo test se ne accorge. */
    @Test
    void iContenutiDelGiocoSiCaricanoESonoCoerenti() {
        CatalogoJson catalogo = new CatalogoJson();

        assertFalse(catalogo.diSerie().isEmpty());
        assertTrue(catalogo.tutte().size() > catalogo.diSerie().size(), "il negozio deve avere moto da vendere");
        for (Moto moto : catalogo.tutte()) {
            assertTrue(moto.punteggioComplessivo() > 0);
        }

        int campionati = 0;
        Optional<Campionato> campionato = Optional.of(catalogo.primo());
        while (campionato.isPresent()) {
            assertFalse(campionato.get().getTappe().isEmpty());
            campionati++;
            campionato = catalogo.successivoA(campionato.get().getNome());
        }
        assertTrue(campionati >= 2, "servono più campionati per dare un seguito alla carriera");
    }

    @Test
    void ogniRichiestaRestituisceUnCampionatoNuovo() {
        CatalogoJson catalogo = new CatalogoJson();
        Campionato giaIniziato = catalogo.primo();
        giaIniziato.registraPiazzamento(1);

        Campionato nuovo = catalogo.primo();

        assertEquals(0, nuovo.getIndiceTappaCorrente());
        assertEquals(0, nuovo.getPuntiGiocatore());
    }

    @Test
    void ogniCarrieraRiceveObiettiviPropriConIProgressiAZero() {
        CatalogoJson catalogo = new CatalogoJson();
        List<ObiettivoSponsor> diUnaCarriera = catalogo.nuoviObiettivi();
        diUnaCarriera.get(0).impostaProgresso(1);

        List<ObiettivoSponsor> diUnAltraCarriera = catalogo.nuoviObiettivi();

        assertFalse(diUnAltraCarriera.isEmpty());
        assertEquals(0, diUnAltraCarriera.get(0).getProgresso());
    }

    @Test
    void dopoUnCampionatoSconosciutoNonCeNeSonoAltri() {
        CatalogoJson catalogo = new CatalogoJson();

        assertTrue(catalogo.successivoA("Campionato che non esiste").isEmpty());
    }

    @Test
    void unaCartellaSenzaFileVieneSegnalata() {
        assertThrows(CatalogoNonValidoException.class, () -> new CatalogoJson("/cartella/che/non/esiste/"));
    }

    @Test
    void unFileConJsonNonValidoVieneSegnalato() {
        assertThrows(CatalogoNonValidoException.class, () -> new CatalogoJson(CARTELLA_DI_PROVA + "json-rotto/"));
    }

    @Test
    void unaPistaSconosciutaNelFileVieneSegnalata() {
        CatalogoNonValidoException errore = assertThrows(CatalogoNonValidoException.class,
                () -> new CatalogoJson(CARTELLA_DI_PROVA + "pista-sconosciuta/"));

        assertTrue(errore.getMessage().contains("GHIACCIATA"), "il messaggio deve dire quale codice è sbagliato");
    }
}
