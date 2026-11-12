package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.model.Avversario;
import it.unicam.cs.mpgc.rpg129875.model.Gara;
import it.unicam.cs.mpgc.rpg129875.model.PistaAsciutta;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeneratoreAvversariCasualeTest {

    private static final long SEME = 11;
    private static final int LIVELLO_INIZIALE = 1;

    /** Lo stesso seme produce la stessa sequenza di numeri casuali: così due generazioni sono confrontabili. */
    private static List<Avversario> genera(Gara gara, int livelloGiocatore) {
        return new GeneratoreAvversariCasuale(new Random(SEME)).generaPer(gara, livelloGiocatore);
    }

    @Test
    void generaTantiAvversariQuantiNeRichiedeLaGaraTuttiConNomiDiversi() {
        List<Avversario> avversari = genera(new Gara("Prova", new PistaAsciutta(), 12), LIVELLO_INIZIALE);

        assertEquals(12, avversari.size());
        assertEquals(12, avversari.stream().map(Avversario::getNome).collect(Collectors.toSet()).size());
    }

    @Test
    void inUnaGaraPiuDifficileOgniAvversarioEPiuForteDiQuantoIndicaLaDifficolta() {
        List<Avversario> normali = genera(new Gara("Normale", new PistaAsciutta(), 6), LIVELLO_INIZIALE);
        List<Avversario> difficili = genera(new Gara("Difficile", new PistaAsciutta(), 6, 20), LIVELLO_INIZIALE);

        for (int i = 0; i < normali.size(); i++) {
            assertEquals(normali.get(i).getPrestazione() + 20, difficili.get(i).getPrestazione());
        }
    }

    @Test
    void gliAvversariCresconoInsiemeAlLivelloDelGiocatore() {
        Gara gara = new Gara("Prova", new PistaAsciutta(), 6);

        List<Avversario> controUnEsordiente = genera(gara, LIVELLO_INIZIALE);
        List<Avversario> controUnVeterano = genera(gara, LIVELLO_INIZIALE + 5);

        for (int i = 0; i < controUnEsordiente.size(); i++) {
            assertEquals(controUnEsordiente.get(i).getPrestazione() + 5, controUnVeterano.get(i).getPrestazione());
        }
    }
}
