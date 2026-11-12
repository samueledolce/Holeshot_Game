package it.unicam.cs.mpgc.rpg129875.gui;

import it.unicam.cs.mpgc.rpg129875.engine.CalcolatorePiazzamentoStandard;
import it.unicam.cs.mpgc.rpg129875.engine.EsitoGara;
import it.unicam.cs.mpgc.rpg129875.engine.GeneratoreAvversariCasuale;
import it.unicam.cs.mpgc.rpg129875.engine.GestoreCarriera;
import it.unicam.cs.mpgc.rpg129875.engine.GestoreProgressione;
import it.unicam.cs.mpgc.rpg129875.engine.MotoreDiGara;
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
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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

    private static final double LARGHEZZA_FINESTRA = 900;
    private static final double ALTEZZA_FINESTRA = 640;

    private Stage stage;
    private Carriera carrieraCorrente;

    private RepositorySalvataggi<Carriera> repository;
    private CatalogoMoto catalogoMoto;
    private GestoreCarriera gestoreCarriera;
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
        this.gestoreCarriera = new GestoreCarriera(catalogo);
        this.repository = new RepositorySalvataggiJson(Path.of("saves"));
        this.motoreDiGara = new MotoreDiGara(
                new GeneratoreAvversariCasuale(),
                new CalcolatorePiazzamentoStandard(),
                new GestoreProgressione()
        );
        this.risolutorePartenza = new RisolutorePartenza();
    }

    @Override
    public void start(Stage stagePrimario) {
        this.stage = stagePrimario;
        stage.setTitle("Holeshot - Carriera Motocross");
        stage.setScene(new Scene(new StackPane(), LARGHEZZA_FINESTRA, ALTEZZA_FINESTRA));
        mostraMenu();
        stage.show();
    }

    @Override
    public void stop() {
        esecutoreIO.shutdownNow();
    }

    private void mostra(Parent vista) {
        stage.getScene().setRoot(vista);
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
        mostra(SchermataCarriera.crea(this, carrieraCorrente, gestoreCarriera));
    }

    @Override
    public void passaAlProssimoCampionato() {
        gestoreCarriera.passaAlProssimoCampionato(carrieraCorrente);
        mostraCarriera();
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
        mostra(SchermataRisultato.crea(this, esito));
    }

    @Override
    public void salvaCarriera(String nomeSalvataggio, Runnable alTermine) {
        esecutoreIO.submit(() -> {
            try {
                repository.salva(nomeSalvataggio, carrieraCorrente);
                Platform.runLater(alTermine);
            } catch (PersistenzaException e) {
                Platform.runLater(() -> mostraErrore("Salvataggio non riuscito: " + e.getMessage()));
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
