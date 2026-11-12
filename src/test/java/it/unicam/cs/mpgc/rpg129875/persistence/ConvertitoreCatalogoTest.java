package it.unicam.cs.mpgc.rpg129875.persistence;

import it.unicam.cs.mpgc.rpg129875.model.CategoriaMoto;
import it.unicam.cs.mpgc.rpg129875.model.Moto;
import it.unicam.cs.mpgc.rpg129875.model.ObiettivoSponsor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConvertitoreCatalogoTest {

    private final ConvertitoreCatalogo convertitore = new ConvertitoreCatalogo(new ConvertitoreCarriera());

    private static MotoDTO moto(String nome, String categoria, int prezzo) {
        MotoDTO dto = new MotoDTO();
        dto.nome = nome;
        dto.categoria = categoria;
        dto.potenza = 50;
        dto.maneggevolezza = 60;
        dto.bonusPartenza = 70;
        dto.prezzo = prezzo;
        return dto;
    }

    private static CampionatoDTO campionato(String nome, String codicePista, int numeroAvversari) {
        GaraDTO tappa = new GaraDTO();
        tappa.nomeTappa = "Tappa di prova";
        tappa.pistaChiave = codicePista;
        tappa.numeroAvversari = numeroAvversari;
        CampionatoDTO dto = new CampionatoDTO();
        dto.nome = nome;
        dto.tappe = List.of(tappa);
        return dto;
    }

    @Test
    void unaMotoValidaVieneConvertitaConTuttiISuoiDati() {
        List<Moto> moto = convertitore.motoVersoDominio(List.of(
                moto("Di serie", "MX_125", 0), moto("Da negozio", "MX_450", 400)));

        assertEquals(2, moto.size());
        assertEquals(CategoriaMoto.MX_450, moto.get(1).getCategoria());
        assertEquals(400, moto.get(1).getPrezzo());
        assertTrue(moto.get(0).isDiSerie());
    }

    @Test
    void unaCategoriaSconosciutaVieneRifiutata() {
        assertThrows(CatalogoNonValidoException.class,
                () -> convertitore.motoVersoDominio(List.of(moto("Strana", "MX_999", 0))));
    }

    @Test
    void unPrezzoNegativoVieneRifiutato() {
        assertThrows(CatalogoNonValidoException.class,
                () -> convertitore.motoVersoDominio(List.of(moto("Di serie", "MX_125", 0), moto("Regalo", "MX_250", -10))));
    }

    @Test
    void dueMotoConLoStessoNomeVengonoRifiutate() {
        assertThrows(CatalogoNonValidoException.class,
                () -> convertitore.motoVersoDominio(List.of(moto("Doppia", "MX_125", 0), moto("Doppia", "MX_250", 100))));
    }

    @Test
    void senzaUnaMotoDiSerieNonSiPuoIniziareUnaCarriera() {
        assertThrows(CatalogoNonValidoException.class,
                () -> convertitore.motoVersoDominio(List.of(moto("Costosa", "MX_450", 500))));
    }

    @Test
    void unCampionatoValidoSuperaIControlli() {
        convertitore.controllaCampionati(List.of(campionato("Valido", "SABBIOSA", 4)));

        assertEquals("Pista sabbiosa", convertitore.campionatoVersoDominio(campionato("Valido", "SABBIOSA", 4))
                .tappaCorrente().getPista().nome());
    }

    @Test
    void unaPistaConCodiceSconosciutoVieneRifiutata() {
        assertThrows(CatalogoNonValidoException.class,
                () -> convertitore.controllaCampionati(List.of(campionato("Polare", "GHIACCIATA", 4))));
    }

    @Test
    void unaTappaSenzaAvversariVieneRifiutata() {
        assertThrows(CatalogoNonValidoException.class,
                () -> convertitore.controllaCampionati(List.of(campionato("Deserto", "ASCIUTTA", 0))));
    }

    @Test
    void unElencoDiCampionatiVuotoVieneRifiutato() {
        assertThrows(CatalogoNonValidoException.class, () -> convertitore.controllaCampionati(List.of()));
    }

    private static ObiettivoDTO obiettivo(String descrizione, String codiceCriterio, int traguardo) {
        ObiettivoDTO dto = new ObiettivoDTO();
        dto.descrizione = descrizione;
        dto.criterioChiave = codiceCriterio;
        dto.traguardo = traguardo;
        dto.ricompensa = 100;
        return dto;
    }

    @Test
    void gliObiettiviDelCatalogoPartonoSempreDaZero() {
        ObiettivoDTO modello = obiettivo("Sali sul podio 3 volte", "PODIO", 3);
        modello.progresso = 2;

        List<ObiettivoSponsor> obiettivi = convertitore.obiettiviVersoDominio(List.of(modello));

        assertEquals(0, obiettivi.get(0).getProgresso());
        assertEquals(3, obiettivi.get(0).getTraguardo());
    }

    @Test
    void unObiettivoConCriterioSconosciutoVieneRifiutato() {
        assertThrows(CatalogoNonValidoException.class,
                () -> convertitore.obiettiviVersoDominio(List.of(obiettivo("Fai un salto mortale", "SALTO_MORTALE", 1))));
    }

    @Test
    void unObiettivoSenzaTraguardoVieneRifiutato() {
        assertThrows(CatalogoNonValidoException.class,
                () -> convertitore.obiettiviVersoDominio(List.of(obiettivo("Sali sul podio", "PODIO", 0))));
    }
}
