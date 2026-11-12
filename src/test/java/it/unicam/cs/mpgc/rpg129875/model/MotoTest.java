package it.unicam.cs.mpgc.rpg129875.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MotoTest {

    @Test
    void dueMotoConLoStessoNomeSonoLaStessaMoto() {
        Moto originale = new Moto("Tempesta 250", CategoriaMoto.MX_250, 65, 65, 55);
        Moto ricaricata = new Moto("Tempesta 250", CategoriaMoto.MX_250, 65, 65, 55);

        assertEquals(originale, ricaricata);
        assertEquals(originale.hashCode(), ricaricata.hashCode());
    }

    @Test
    void dueMotoConNomiDiversiSonoDiverse() {
        Moto leggera = new Moto("Falco 125", CategoriaMoto.MX_125, 40, 80, 65);
        Moto potente = new Moto("Titano 450", CategoriaMoto.MX_450, 85, 45, 40);

        assertNotEquals(leggera, potente);
    }

    @Test
    void soloLeMotoGratuiteSonoDiSerie() {
        assertTrue(new Moto("Gratuita", CategoriaMoto.MX_125, 40, 80, 65).isDiSerie());
        assertFalse(new Moto("A pagamento", CategoriaMoto.MX_125, 52, 90, 78, 350).isDiSerie());
    }

    @Test
    void ilPunteggioComplessivoELaMediaDelleTreCaratteristiche() {
        assertEquals(60, new Moto("Media", CategoriaMoto.MX_250, 50, 60, 70).punteggioComplessivo());
    }
}
