package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.model.Avversario;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoGara;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoPartenza;
import it.unicam.cs.mpgc.rpg129875.model.Statistiche;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Implementazione "essenziale" del piazzamento: la partenza pesa in modo
 * significativo, il resto della gara è riassunto da statistiche del pilota,
 * qualità della moto e una piccola componente casuale.
 */
public final class CalcolatorePiazzamentoStandard implements CalcolatorePiazzamento {

    private static final int PENALITA_FALSA_PARTENZA = 30;
    private static final int PUNTI_PER_LIVELLO_QUALITA_PARTENZA = 10;

    private final Random random;

    public CalcolatorePiazzamentoStandard() {
        this(new Random());
    }

    /** Costruttore che accetta un generatore di numeri casuali esterno, utile per test deterministici. */
    public CalcolatorePiazzamentoStandard(Random random) {
        this.random = random;
    }

    @Override
    public RisultatoGara calcola(Pilota pilota, RisultatoPartenza partenza, List<Avversario> avversari) {
        int prestazioneGiocatore = prestazioneGiocatore(pilota, partenza);

        record Concorrente(String nome, int prestazione) {
        }

        List<Concorrente> concorrenti = new ArrayList<>();
        concorrenti.add(new Concorrente(pilota.getNome(), prestazioneGiocatore));
        for (Avversario avversario : avversari) {
            concorrenti.add(new Concorrente(avversario.getNome(), avversario.getPrestazione()));
        }

        List<String> classificaOrdinata = concorrenti.stream()
                .sorted(Comparator.comparingInt(Concorrente::prestazione).reversed())
                .map(Concorrente::nome)
                .collect(Collectors.toList());

        int posizioneGiocatore = classificaOrdinata.indexOf(pilota.getNome()) + 1;

        return new RisultatoGara(classificaOrdinata, posizioneGiocatore);
    }

    private int prestazioneGiocatore(Pilota pilota, RisultatoPartenza partenza) {
        Statistiche statistiche = pilota.getStatistiche();
        int base = statistiche.punteggioComplessivo() + pilota.getMotoAttuale().punteggioComplessivo() / 2;
        int bonusPartenza = partenza.getQualita().getPunteggio() * PUNTI_PER_LIVELLO_QUALITA_PARTENZA;
        int rumore = random.nextInt(17) - 8; // tra -8 e +8

        int prestazione = base + bonusPartenza + rumore;

        if (partenza.isFalsaPartenza()) {
            prestazione -= PENALITA_FALSA_PARTENZA;
        }

        return Math.max(1, prestazione);
    }
}
