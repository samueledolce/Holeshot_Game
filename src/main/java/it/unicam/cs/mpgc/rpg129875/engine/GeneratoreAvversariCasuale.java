package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.model.Avversario;
import it.unicam.cs.mpgc.rpg129875.model.Gara;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Genera avversari con nomi e prestazioni casuali.
 *
 * <p>La prestazione media è tarata perché un pilota alle prime armi, con
 * una moto di serie, arrivi a metà gruppo con una buona partenza e possa
 * vincere solo con una partenza ottima o perfetta. Cresce poi con la
 * difficoltà della gara e, in misura minore, con il livello del
 * giocatore.</p>
 */
public final class GeneratoreAvversariCasuale implements GeneratoreAvversari {

    private static final String[] NOMI = {
            "Rossi", "Herlings", "Cairoli", "Prado", "Vialle", "Coldenhoff",
            "Febvre", "Renaux", "Gajser", "Seewer", "Jonass", "Paulin",
            "Fernandez", "Longhi", "Bianchi", "Romano"
    };

    private static final int PRESTAZIONE_MEDIA_INIZIALE = 110;
    private static final int CRESCITA_PER_LIVELLO_GIOCATORE = 1;
    private static final int VARIAZIONE_MASSIMA = 15;
    private static final int PRESTAZIONE_MINIMA = 10;

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
        int prestazioneMedia = prestazioneMedia(gara, livelloGiocatore);

        for (int i = 0; i < gara.getNumeroAvversari() && !nomiDisponibili.isEmpty(); i++) {
            String nome = nomiDisponibili.remove(random.nextInt(nomiDisponibili.size()));
            int variazione = random.nextInt(2 * VARIAZIONE_MASSIMA + 1) - VARIAZIONE_MASSIMA;
            avversari.add(new Avversario(nome, Math.max(PRESTAZIONE_MINIMA, prestazioneMedia + variazione)));
        }

        return avversari;
    }

    private static int prestazioneMedia(Gara gara, int livelloGiocatore) {
        return PRESTAZIONE_MEDIA_INIZIALE
                + gara.getDifficolta()
                + (livelloGiocatore - 1) * CRESCITA_PER_LIVELLO_GIOCATORE;
    }
}
