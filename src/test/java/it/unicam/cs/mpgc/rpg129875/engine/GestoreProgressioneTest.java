package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.DatiDiProva;
import it.unicam.cs.mpgc.rpg129875.model.Abilita;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import it.unicam.cs.mpgc.rpg129875.model.StileEquilibrato;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GestoreProgressioneTest {

    private Pilota nuovoPilotaDiProva() {
        return new Pilota("Test", new StileEquilibrato(), DatiDiProva.motoLeggera());
    }

    @Test
    void assegnandoAbbastanzaEsperienzaIlPilotaSaleDiLivello() {
        Pilota pilota = nuovoPilotaDiProva();
        GestoreProgressione gestore = new GestoreProgressione();

        gestore.assegnaEsperienza(pilota, 100);

        assertEquals(2, pilota.getLivello());
        assertEquals(0, pilota.getEsperienza());
    }

    @Test
    void lEsperienzaInEccessoRimaneDopoIlLivello() {
        Pilota pilota = nuovoPilotaDiProva();
        GestoreProgressione gestore = new GestoreProgressione();

        gestore.assegnaEsperienza(pilota, 130);

        assertEquals(2, pilota.getLivello());
        assertEquals(30, pilota.getEsperienza());
    }

    @Test
    void alLivelloDueSiSbloccaSangueFreddo() {
        Pilota pilota = nuovoPilotaDiProva();
        GestoreProgressione gestore = new GestoreProgressione();

        List<Abilita> nuoveAbilita = gestore.assegnaEsperienza(pilota, 100);

        assertEquals(1, nuoveAbilita.size());
        assertEquals("Sangue Freddo", nuoveAbilita.get(0).nome());
        assertTrue(pilota.getAbilitaSbloccate().contains(nuoveAbilita.get(0)));
    }

    @Test
    void nonAbbastanzaEsperienzaNonFaSalireDiLivello() {
        Pilota pilota = nuovoPilotaDiProva();
        GestoreProgressione gestore = new GestoreProgressione();

        List<Abilita> nuoveAbilita = gestore.assegnaEsperienza(pilota, 50);

        assertEquals(1, pilota.getLivello());
        assertEquals(50, pilota.getEsperienza());
        assertTrue(nuoveAbilita.isEmpty());
    }
}
