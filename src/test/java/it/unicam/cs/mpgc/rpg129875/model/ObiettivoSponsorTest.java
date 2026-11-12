package it.unicam.cs.mpgc.rpg129875.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObiettivoSponsorTest {

    private static final RisultatoPartenza PARTENZA_BUONA = new RisultatoPartenza(300, QualitaPartenza.BUONA);
    private static final RisultatoPartenza PARTENZA_OTTIMA = new RisultatoPartenza(200, QualitaPartenza.OTTIMA);
    private static final RisultatoPartenza PARTENZA_PERFETTA = new RisultatoPartenza(100, QualitaPartenza.PERFETTA);

    private static RisultatoGara arrivoIn(int posizione) {
        return new RisultatoGara(List.of("A", "B", "C", "D", "E"), posizione);
    }

    private static ObiettivoSponsor treVittorie() {
        return new ObiettivoSponsor("Vinci 3 gare", new CriterioPiazzamento("VITTORIA", 1), 3, 300);
    }

    @Test
    void ilCriterioDiPiazzamentoContaSoloLeGareEntroLaPosizioneRichiesta() {
        CriterioObiettivo podio = new CriterioPiazzamento("PODIO", 3);

        assertTrue(podio.soddisfattoDa(PARTENZA_BUONA, arrivoIn(1)));
        assertTrue(podio.soddisfattoDa(PARTENZA_BUONA, arrivoIn(3)));
        assertFalse(podio.soddisfattoDa(PARTENZA_BUONA, arrivoIn(4)));
    }

    @Test
    void ilCriterioSullaPartenzaAccettaAncheUnaQualitaSuperioreAQuellaRichiesta() {
        CriterioObiettivo holeshot = new CriterioQualitaPartenza("HOLESHOT", QualitaPartenza.OTTIMA);

        assertFalse(holeshot.soddisfattoDa(PARTENZA_BUONA, arrivoIn(1)));
        assertTrue(holeshot.soddisfattoDa(PARTENZA_OTTIMA, arrivoIn(5)));
        assertTrue(holeshot.soddisfattoDa(PARTENZA_PERFETTA, arrivoIn(5)));
    }

    @Test
    void ogniCriterioDelRegistroSiRitrovaConIlProprioCodice() {
        for (CriterioObiettivo criterio : Registri.criteriObiettivo().tutti()) {
            assertEquals(criterio.codice(), Registri.criteriObiettivo().perCodice(criterio.codice()).codice());
        }
    }

    @Test
    void leGareCheNonSoddisfanoIlCriterioNonFannoAvanzareLObiettivo() {
        ObiettivoSponsor obiettivo = treVittorie();

        assertFalse(obiettivo.registraGara(PARTENZA_PERFETTA, arrivoIn(2)));

        assertEquals(0, obiettivo.getProgresso());
    }

    @Test
    void lObiettivoSiCompletaSoloQuandoSiRaggiungeIlTraguardo() {
        ObiettivoSponsor obiettivo = treVittorie();

        assertFalse(obiettivo.registraGara(PARTENZA_BUONA, arrivoIn(1)));
        assertFalse(obiettivo.registraGara(PARTENZA_BUONA, arrivoIn(1)));
        assertFalse(obiettivo.isCompletato());
        assertTrue(obiettivo.registraGara(PARTENZA_BUONA, arrivoIn(1)));
        assertTrue(obiettivo.isCompletato());
    }

    @Test
    void unObiettivoGiaCompletatoNonSiCompletaUnaSecondaVolta() {
        ObiettivoSponsor obiettivo = new ObiettivoSponsor("Vinci una gara", new CriterioPiazzamento("VITTORIA", 1), 1, 200);
        obiettivo.registraGara(PARTENZA_BUONA, arrivoIn(1));

        assertFalse(obiettivo.registraGara(PARTENZA_BUONA, arrivoIn(1)));
        assertEquals(1, obiettivo.getProgresso());
    }

    @Test
    void iProgressiRipristinatiDaUnSalvataggioRestanoEntroIlTraguardo() {
        ObiettivoSponsor obiettivo = treVittorie();

        obiettivo.impostaProgresso(99);
        assertEquals(3, obiettivo.getProgresso());

        obiettivo.impostaProgresso(-5);
        assertEquals(0, obiettivo.getProgresso());
    }

    @Test
    void unTraguardoNulloVieneRifiutato() {
        assertThrows(IllegalArgumentException.class,
                () -> new ObiettivoSponsor("Senza senso", new CriterioPiazzamento("PODIO", 3), 0, 100));
    }
}
