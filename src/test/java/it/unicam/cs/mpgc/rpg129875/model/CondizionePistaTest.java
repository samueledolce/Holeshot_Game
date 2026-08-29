package it.unicam.cs.mpgc.rpg129875.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CondizionePistaTest {

    @Test
    void unTempoNegativoEUnaFalsaPartenzaSuQualsiasiPista() {
        CondizionePista pista = new PistaAsciutta();

        assertEquals(QualitaPartenza.FALSA_PARTENZA, pista.classifica(-1));
    }

    @Test
    void laPistaAsciuttaClassificaCorrettamenteLeSoglie() {
        CondizionePista pista = new PistaAsciutta();

        assertEquals(QualitaPartenza.PERFETTA, pista.classifica(140));
        assertEquals(QualitaPartenza.OTTIMA, pista.classifica(200));
        assertEquals(QualitaPartenza.BUONA, pista.classifica(350));
        assertEquals(QualitaPartenza.LENTA, pista.classifica(500));
    }

    @Test
    void laPistaFangosaHaSoglePiuLarghe() {
        CondizionePista fangosa = new PistaFangosa();
        CondizionePista asciutta = new PistaAsciutta();

        // 200 ms è già "ottima" su asciutto, ma resta "perfetta" su fango:
        // conferma che le due implementazioni si comportano davvero in modo diverso.
        assertEquals(QualitaPartenza.OTTIMA, asciutta.classifica(200));
        assertEquals(QualitaPartenza.PERFETTA, fangosa.classifica(200));
    }
}
