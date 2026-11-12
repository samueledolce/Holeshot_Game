package it.unicam.cs.mpgc.rpg129875.gui;

import it.unicam.cs.mpgc.rpg129875.engine.RisolutorePartenza;
import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.CondizionePista;
import it.unicam.cs.mpgc.rpg129875.model.Gara;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoPartenza;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Il minigioco della partenza: un cancelletto che cade dopo un'attesa
 * casuale; il giocatore deve reagire il prima possibile premendo il
 * pulsante "SCATTA!".
 *
 * <p>Premere il pulsante prima dell'apertura è una falsa partenza. Se il
 * giocatore non reagisce entro pochi secondi dall'apertura, la partenza
 * viene comunque risolta (con il tempo peggiore) per non bloccare il
 * gioco.</p>
 */
final class SchermataPartenza {

    private static final Duration TIMEOUT_DOPO_APERTURA = Duration.seconds(3);
    private static final Duration DURATA_CADUTA_CANCELLETTO = Duration.millis(90);

    private static final double LARGHEZZA_PANNELLO = 560;
    private static final double LARGHEZZA_SBARRA = 400;
    private static final double ALTEZZA_SBARRA = 18;
    private static final double ALTEZZA_MONTANTE = 64;
    private static final double CADUTA_SBARRA = ALTEZZA_MONTANTE - ALTEZZA_SBARRA;
    private static final double RAGGIO_LUCE = 20;

    private SchermataPartenza() {
    }

    static Parent crea(Navigatore navigatore, Carriera carriera, Gara gara, RisolutorePartenza risolutore) {
        CondizionePista pista = gara.getPista();

        Circle luce = new Circle(RAGGIO_LUCE);
        luce.getStyleClass().setAll("luce", "luce-attesa");

        Rectangle sbarra = new Rectangle(LARGHEZZA_SBARRA, ALTEZZA_SBARRA);
        sbarra.setArcWidth(8);
        sbarra.setArcHeight(8);
        sbarra.getStyleClass().setAll("cancelletto");
        HBox cancelletto = new HBox(montante(), sbarra, montante());
        cancelletto.setAlignment(Pos.TOP_CENTER);

        Label stato = Componenti.testo("Preparati...", "stato-partenza");

        Button scatta = new Button("SCATTA!");
        scatta.getStyleClass().add("pulsante-partenza");

        VBox pannello = Componenti.pannello(LARGHEZZA_PANNELLO,
                Componenti.sottotitolo(gara.getNomeTappa() + "  ·  " + pista.nome()),
                luce, cancelletto, stato, scatta);
        pannello.setAlignment(Pos.CENTER);
        pannello.setSpacing(20);

        AtomicBoolean cancellettoAperto = new AtomicBoolean(false);
        AtomicBoolean esitoDeciso = new AtomicBoolean(false);
        long[] istanteApertura = new long[1];

        scatta.setOnAction(evento -> {
            if (esitoDeciso.getAndSet(true)) {
                return;
            }
            long tempoReazioneMs = cancellettoAperto.get()
                    ? (System.nanoTime() - istanteApertura[0]) / 1_000_000
                    : -1; // falsa partenza: premuto prima dell'apertura

            RisultatoPartenza risultato = risolutore.risolvi(carriera.getPilota(), pista, tempoReazioneMs);
            mostraEsitoEProsegui(navigatore, gara, risultato, stato, scatta, luce, sbarra, pannello);
        });

        long attesaMs = ThreadLocalRandom.current().nextLong(pista.attesaMinimaMs(), pista.attesaMassimaMs() + 1);

        PauseTransition attesaCancelletto = new PauseTransition(Duration.millis(attesaMs));
        attesaCancelletto.setOnFinished(evento -> {
            if (esitoDeciso.get()) {
                return; // il giocatore ha già fatto falsa partenza
            }
            cancellettoAperto.set(true);
            istanteApertura[0] = System.nanoTime();
            apriCancelletto(luce, sbarra);
            stato.setText("VIA!!!");

            PauseTransition timeout = new PauseTransition(TIMEOUT_DOPO_APERTURA);
            timeout.setOnFinished(eventoTimeout -> {
                if (esitoDeciso.getAndSet(true)) {
                    return;
                }
                long tempoReazioneMs = (long) TIMEOUT_DOPO_APERTURA.toMillis();
                RisultatoPartenza risultato = risolutore.risolvi(carriera.getPilota(), pista, tempoReazioneMs);
                mostraEsitoEProsegui(navigatore, gara, risultato, stato, scatta, luce, sbarra, pannello);
            });
            timeout.play();
        });
        attesaCancelletto.play();

        return Componenti.schermata(Componenti.sfondoPer(pista), pannello);
    }

    private static Rectangle montante() {
        Rectangle montante = new Rectangle(10, ALTEZZA_MONTANTE);
        montante.getStyleClass().setAll("montante");
        return montante;
    }

    /** La luce diventa verde e la sbarra cade a terra, come un vero cancelletto da motocross. */
    private static void apriCancelletto(Circle luce, Rectangle sbarra) {
        luce.getStyleClass().setAll("luce", "luce-via");
        sbarra.getStyleClass().setAll("cancelletto", "cancelletto-aperto");
        TranslateTransition caduta = new TranslateTransition(DURATA_CADUTA_CANCELLETTO, sbarra);
        caduta.setToY(CADUTA_SBARRA);
        caduta.play();
    }

    private static void mostraEsitoEProsegui(Navigatore navigatore, Gara gara, RisultatoPartenza risultato,
                                             Label stato, Button scatta, Circle luce, Rectangle sbarra,
                                             VBox pannello) {
        scatta.setDisable(true);
        luce.getStyleClass().setAll("luce", "luce-spenta");
        sbarra.getStyleClass().setAll("cancelletto", "cancelletto-fermo");

        String messaggio = risultato.isFalsaPartenza()
                ? "Falsa partenza!"
                : risultato.getQualita().getDescrizione() + " (" + risultato.getTempoReazioneMs() + " ms)";
        stato.setText(messaggio);

        pannello.getChildren().add(Componenti.pulsantePrincipale("Vedi il risultato della gara",
                () -> navigatore.completaGara(gara, risultato)));
    }
}
