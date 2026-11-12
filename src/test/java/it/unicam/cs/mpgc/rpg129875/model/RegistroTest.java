package it.unicam.cs.mpgc.rpg129875.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistroTest {

    @Test
    void unElementoSiRitrovaTramiteIlSuoCodice() {
        Registro<CondizionePista> registro = Registri.condizioniPista();

        assertEquals("Pista fangosa", registro.perCodice("FANGOSA").nome());
    }

    @Test
    void ogniElementoRegistratoSiRitrovaConIlProprioCodice() {
        for (StileDiGuida stile : Registri.stiliDiGuida().tutti()) {
            assertEquals(stile.nome(), Registri.stiliDiGuida().perCodice(stile.codice()).nome());
        }
        for (CondizionePista pista : Registri.condizioniPista().tutti()) {
            assertEquals(pista.nome(), Registri.condizioniPista().perCodice(pista.codice()).nome());
        }
    }

    @Test
    void unCodiceSconosciutoVieneRifiutato() {
        Registro<StileDiGuida> registro = Registri.stiliDiGuida();

        assertTrue(registro.cerca("INESISTENTE").isEmpty());
        assertThrows(IllegalArgumentException.class, () -> registro.perCodice("INESISTENTE"));
    }

    @Test
    void dueElementiConLoStessoCodiceNonPossonoConvivere() {
        List<StileDiGuida> doppioni = List.of(new StileTecnico(), new StileTecnico());

        assertThrows(IllegalArgumentException.class, () -> new Registro<>(doppioni));
    }

    @Test
    void lOrdineDiRegistrazioneVieneMantenuto() {
        List<StileDiGuida> stili = Registri.stiliDiGuida().tutti();

        assertEquals("AGGRESSIVO", stili.get(0).codice());
        assertEquals("TECNICO", stili.get(1).codice());
        assertEquals("EQUILIBRATO", stili.get(2).codice());
    }
}
