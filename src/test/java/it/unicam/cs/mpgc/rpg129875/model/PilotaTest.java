package it.unicam.cs.mpgc.rpg129875.model;

import it.unicam.cs.mpgc.rpg129875.DatiDiProva;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PilotaTest {

    private static Pilota pilotaConCrediti(int crediti) {
        Pilota pilota = new Pilota("Ada", new StileTecnico(), DatiDiProva.motoMedia());
        pilota.aggiungiDenaro(crediti);
        return pilota;
    }

    @Test
    void unNuovoPilotaParteDalleStatisticheDelSuoStile() {
        Pilota pilota = pilotaConCrediti(0);

        assertEquals(new StileTecnico().statisticheIniziali().toString(), pilota.getStatistiche().toString());
        assertEquals(1, pilota.getLivello());
    }

    @Test
    void spendereScalaICrediti() {
        Pilota pilota = pilotaConCrediti(300);

        pilota.spendiDenaro(120);

        assertEquals(180, pilota.getDenaro());
    }

    @Test
    void siPuoSpendereFinoAllUltimoCredito() {
        Pilota pilota = pilotaConCrediti(300);

        assertTrue(pilota.puoSpendere(300));
        assertFalse(pilota.puoSpendere(301));
    }

    @Test
    void spenderePiuDiQuantoSiHaVieneRifiutatoSenzaToccareICrediti() {
        Pilota pilota = pilotaConCrediti(100);

        assertThrows(IllegalStateException.class, () -> pilota.spendiDenaro(101));
        assertEquals(100, pilota.getDenaro());
    }

    @Test
    void leAbilitaSbloccateRiduconoIlTempoDiReazione() {
        Pilota pilota = pilotaConCrediti(0);
        pilota.sbloccaAbilita(new SangueFreddo());

        assertEquals(175, pilota.applicaAbilitaAlTempo(200));
    }
}
