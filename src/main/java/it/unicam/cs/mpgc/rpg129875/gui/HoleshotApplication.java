package it.unicam.cs.mpgc.rpg129875.gui;

import it.unicam.cs.mpgc.rpg129875.engine.CalcolatorePiazzamentoStandard;
import it.unicam.cs.mpgc.rpg129875.engine.EsitoGara;
import it.unicam.cs.mpgc.rpg129875.engine.GeneratoreAvversariCasuale;
import it.unicam.cs.mpgc.rpg129875.engine.GestoreCarriera;
import it.unicam.cs.mpgc.rpg129875.engine.GestoreObiettivi;
import it.unicam.cs.mpgc.rpg129875.engine.GestoreProgressione;
import it.unicam.cs.mpgc.rpg129875.engine.MotoreDiGara;
import it.unicam.cs.mpgc.rpg129875.engine.Negozio;
import it.unicam.cs.mpgc.rpg129875.engine.RisolutorePartenza;
import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.CatalogoMoto;
import it.unicam.cs.mpgc.rpg129875.model.Gara;
import it.unicam.cs.mpgc.rpg129875.model.Registri;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoPartenza;
import it.unicam.cs.mpgc.rpg129875.persistence.CatalogoJson;
import it.unicam.cs.mpgc.rpg129875.persistence.PersistenzaException;
import it.unicam.cs.mpgc.rpg129875.persistence.RepositorySalvataggi;
import it.unicam.cs.mpgc.rpg129875.persistence.RepositorySalvataggiJson;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Punto di ingresso dell'applicazione JavaFX. Fa anche da {@link Navigatore}
 * centrale: tiene l'unica finestra (Stage) dell'applicazione e ne sostituisce
 * il contenuto a seconda della schermata richiesta.
 *
 * <p>In questo modo ogni schermata è una semplice funzione che costruisce un
 * {@link Parent}, senza dover gestire finestre proprie. Le operazioni di
 * salvataggio/caricamento vengono eseguite su un thread dedicato per non
 * bloccare il thread grafico di JavaFX durante l'accesso al file system.</p>
 */
public final class HoleshotApplication extends Application implements Navigatore {

    private static final double LARGHEZZA_FINESTRA = 1080;
    private static final double ALTEZZA_FINESTRA = 660;
    private static final double LARGHEZZA_MINIMA = 1000;
    private static final double ALTEZZA_MINIMA = 640;
    private static final Duration DURATA_DISSOLVENZA = Duration.millis(220);
    /** Colore visibile per un istante durante la dissolvenza: lo stesso fondo scuro del foglio di stile. */
    private static final Color COLORE_DI_FONDO = Color.web("#11151c");

    private Stage stage;
    private Carriera carrieraCorrente;

    private RepositorySalvataggi<Carriera> repository;
    private CatalogoMoto catalogoMoto;
    private GestoreCarriera gestoreCarriera;
    private GestoreProgressione gestoreProgressione;
    private Negozio negozio;
    private MotoreDiGara motoreDiGara;
    private RisolutorePartenza risolutorePartenza;

    private final ExecutorService esecutoreIO = Executors.newSingleThreadExecutor(compito -> {
        Thread thread = new Thread(compito, "holeshot-io");
        thread.setDaemon(true);
        return thread;
    });

    /**
     * Crea i servizi dell'applicazione e li collega tra loro. È l'unico
     * punto in cui vengono scelte le implementazioni concrete: tutto il
     * resto del codice lavora sulle interfacce. JavaFX esegue questo
     * metodo prima di {@link #start(Stage)} e fuori dal thread grafico,
     * quindi la lettura dei file dei contenuti non rallenta la finestra.
     */
    @Override
    public void init() {
        CatalogoJson catalogo = new CatalogoJson();
        this.catalogoMoto = catalogo;
        this.gestoreCarriera = new GestoreCarriera(catalogo, catalogo);
        this.gestoreProgressione = new GestoreProgressione();
        this.negozio = new Negozio(catalogo);
        this.repository = new RepositorySalvataggiJson(Path.of("saves"));
        this.motoreDiGara = new MotoreDiGara(
                new GeneratoreAvversariCasuale(),
                new CalcolatorePiazzamentoStandard(),
                gestoreProgressione,
                new GestoreObiettivi()
        );
        this.risolutorePartenza = new RisolutorePartenza();
    }

    @Override
    public void start(Stage stagePrimario) {
        this.stage = stagePrimario;
        Scene scena = new Scene(new StackPane(), LARGHEZZA_FINESTRA, ALTEZZA_FINESTRA);
        scena.setFill(COLORE_DI_FONDO);
        applicaFoglioDiStile(scena);

        stage.setTitle("Holeshot - Carriera Motocross");
        stage.setMinWidth(LARGHEZZA_MINIMA);
        stage.setMinHeight(ALTEZZA_MINIMA);
        stage.setScene(scena);
        mostraMenu();
        stage.show();
    }

    /** Senza foglio di stile il gioco funziona lo stesso, con l'aspetto predefinito di JavaFX. */
    private static void applicaFoglioDiStile(Scene scena) {
        URL foglioDiStile = HoleshotApplication.class.getResource(Componenti.FOGLIO_DI_STILE);
        if (foglioDiStile != null) {
            scena.getStylesheets().add(foglioDiStile.toExternalForm());
        }
    }

    @Override
    public void stop() {
        esecutoreIO.shutdownNow();
    }

    /** Sostituisce il contenuto della finestra, facendo comparire la nuova schermata in dissolvenza. */
    private void mostra(Parent vista) {
        vista.setOpacity(0);
        stage.getScene().setRoot(vista);
        FadeTransition dissolvenza = new FadeTransition(DURATA_DISSOLVENZA, vista);
        dissolvenza.setFromValue(0);
        dissolvenza.setToValue(1);
        dissolvenza.play();
    }

    // ---- Navigatore ----

    @Override
    public void mostraMenu() {
        mostra(SchermataMenu.crea(this, repository));
    }

    @Override
    public void mostraCreazionePilota() {
        mostra(SchermataCreazionePilota.crea(this, Registri.stiliDiGuida().tutti(), catalogoMoto.diSerie(),
                gestoreCarriera));
    }

    @Override
    public void avviaCarriera(Carriera carriera) {
        this.carrieraCorrente = carriera;
        mostraCarriera();
    }

    @Override
    public void mostraCarriera() {
        mostra(SchermataCarriera.crea(this, carrieraCorrente, gestoreCarriera, gestoreProgressione));
    }

    @Override
    public void passaAlProssimoCampionato() {
        gestoreCarriera.passaAlProssimoCampionato(carrieraCorrente);
        mostraCarriera();
    }

    @Override
    public void mostraNegozio() {
        mostra(SchermataNegozio.crea(this, carrieraCorrente.getPilota(), negozio));
    }

    @Override
    public void mostraGara() {
        mostra(SchermataGara.crea(this, carrieraCorrente));
    }

    @Override
    public void mostraPartenza(Gara gara) {
        mostra(SchermataPartenza.crea(this, carrieraCorrente, gara, risolutorePartenza));
    }

    @Override
    public void completaGara(Gara gara, RisultatoPartenza esitoPartenza) {
        EsitoGara esito = motoreDiGara.disputa(carrieraCorrente, gara, esitoPartenza);
        mostra(SchermataRisultato.crea(this, gara, esito));
    }

    @Override
    public void salvaCarriera(String nomeSalvataggio, Runnable seRiesce, Runnable seFallisce) {
        esecutoreIO.submit(() -> {
            try {
                repository.salva(nomeSalvataggio, carrieraCorrente);
                Platform.runLater(seRiesce);
            } catch (PersistenzaException e) {
                Platform.runLater(() -> {
                    seFallisce.run();
                    mostraErrore("Salvataggio non riuscito: " + e.getMessage());
                });
            }
        });
    }

    @Override
    public void caricaCarriera(String nomeSalvataggio) {
        esecutoreIO.submit(() -> {
            try {
                repository.carica(nomeSalvataggio).ifPresentOrElse(
                        carriera -> Platform.runLater(() -> avviaCarriera(carriera)),
                        () -> Platform.runLater(() ->
                                mostraErrore("Nessun salvataggio trovato con nome \"" + nomeSalvataggio + "\"."))
                );
            } catch (PersistenzaException e) {
                Platform.runLater(() -> mostraErrore("Caricamento non riuscito: " + e.getMessage()));
            }
        });
    }

    @Override
    public void mostraErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR, messaggio);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
