package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.model.Avversario;
import it.unicam.cs.mpgc.rpg129875.model.Gara;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Genera avversari con nomi e prestazioni casuali, scalate sul livello del giocatore. */
public final class GeneratoreAvversariCasuale implements GeneratoreAvversari {

    private static final String[] NOMI = {
            "Rossi", "Herlings", "Cairoli", "Prado", "Vialle", "Coldenhoff",
            "Febvre", "Renaux", "Gajser", "Seewer", "Jonass", "Paulin",
            "Fernandez", "Longhi", "Bianchi", "Romano"
    };

    private final Random random;

    public GeneratoreAvversariCasuale() {
        this(new Random());
    }

    /** Costruttore che accetta un generatore di numeri casuali esterno, utile per test deterministici. */
    public GeneratoreAvversariCasuale(Random random) {
        this.random = random;
    }

    @Override
    public List<Avversario> generaPer(Gara gara, int livelloGiocatore) {
        List<Avversario> avversari = new ArrayList<>();
        List<String> nomiDisponibili = new ArrayList<>(List.of(NOMI));

        int prestazioneBase = 40 + livelloGiocatore * 3;

        for (int i = 0; i < gara.getNumeroAvversari() && !nomiDisponibili.isEmpty(); i++) {
            String nome = nomiDisponibili.remove(random.nextInt(nomiDisponibili.size()));
            int variazione = random.nextInt(31) - 15; // tra -15 e +15
            int prestazione = Math.max(10, prestazioneBase + variazione);
            avversari.add(new Avversario(nome, prestazione));
        }

        return avversari;
    }
}
