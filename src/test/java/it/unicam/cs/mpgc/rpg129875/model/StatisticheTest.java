package it.unicam.cs.mpgc.rpg129875.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticheTest {

    @Test
    void iValoriVengonoLimitatiTraUnoECento() {
        Statistiche statistiche = new Statistiche(150, -20, 50, 50);

        assertEquals(100, statistiche.getRiflessi());
        assertEquals(1, statistiche.getConcentrazione());
    }

    @Test
    void aumentaModificaIValoriRispettandoITetti() {
        Statistiche statistiche = new Statistiche(50, 50, 50, 50);

        statistiche.aumenta(10, -10, 60, 0);

        assertEquals(60, statistiche.getRiflessi());
        assertEquals(40, statistiche.getConcentrazione());
        assertEquals(100, statistiche.getTecnicaPartenza());
        assertEquals(50, statistiche.getResistenza());
    }

    @Test
    void punteggioComplessivoELaMediaDeiQuattroValori() {
        Statistiche statistiche = new Statistiche(100, 100, 100, 100);

        assertEquals(100, statistiche.punteggioComplessivo());
    }
}
