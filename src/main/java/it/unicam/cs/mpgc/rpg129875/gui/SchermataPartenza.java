package it.unicam.cs.mpgc.rpg129875.gui;

import it.unicam.cs.mpgc.rpg129875.engine.RisolutorePartenza;
import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.CondizionePista;
import it.unicam.cs.mpgc.rpg129875.model.Gara;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoPartenza;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Il minigioco della partenza: un cancelletto che si apre dopo un'attesa
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

    private SchermataPartenza() {
    }

    static Parent crea(Navigatore navigatore, Carriera carriera, Gara gara, RisolutorePartenza risolutore) {
        CondizionePista pista = gara.getPista();

        Label titolo = new Label("Ai blocchi di partenza...");
        titolo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label stato = new Label("Preparati.");
        stato.setStyle("-fx-font-size: 16px;");

        Rectangle cancelletto = new Rectangle(300, 60);
        cancelletto.setFill(Color.web("#c0392b"));

        Button scatta = new Button("SCATTA!");
        scatta.setStyle("-fx-font-size: 20px; -fx-min-width: 180px; -fx-min-height: 60px;");

        VBox box = new VBox(20, titolo, cancelletto, stato, scatta);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));

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
            mostraEsitoEProsegui(navigatore, gara, risultato, stato, scatta, cancelletto, box);
        });

        long attesaMs = ThreadLocalRandom.current().nextLong(pista.attesaMinimaMs(), pista.attesaMassimaMs() + 1);

        PauseTransition attesaCancelletto = new PauseTransition(Duration.millis(attesaMs));
        attesaCancelletto.setOnFinished(evento -> {
            if (esitoDeciso.get()) {
                return; // il giocatore ha già fatto falsa partenza
            }
            cancellettoAperto.set(true);
            istanteApertura[0] = System.nanoTime();
            cancelletto.setFill(Color.web("#27ae60"));
            stato.setText("VIA!!!");

            PauseTransition timeout = new PauseTransition(TIMEOUT_DOPO_APERTURA);
            timeout.setOnFinished(eventoTimeout -> {
                if (esitoDeciso.getAndSet(true)) {
                    return;
                }
                long tempoReazioneMs = (long) TIMEOUT_DOPO_APERTURA.toMillis();
                RisultatoPartenza risultato = risolutore.risolvi(carriera.getPilota(), pista, tempoReazioneMs);
                mostraEsitoEProsegui(navigatore, gara, risultato, stato, scatta, cancelletto, box);
            });
            timeout.play();
        });
        attesaCancelletto.play();

        return box;
    }

    private static void mostraEsitoEProsegui(Navigatore navigatore, Gara gara, RisultatoPartenza risultato,
                                              Label stato, Button scatta, Rectangle cancelletto, VBox box) {
        scatta.setDisable(true);
        cancelletto.setFill(Color.web("#7f8c8d"));

        String messaggio = risultato.isFalsaPartenza()
                ? "Falsa partenza!"
                : risultato.getQualita().getDescrizione() + " (" + risultato.getTempoReazioneMs() + " ms)";
        stato.setText(messaggio);

        Button continua = new Button("Vedi il risultato della gara");
        continua.setOnAction(evento -> navigatore.completaGara(gara, risultato));
        box.getChildren().add(continua);
    }
}
