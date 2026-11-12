package it.unicam.cs.mpgc.rpg129875.gui;

import it.unicam.cs.mpgc.rpg129875.model.CondizionePista;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Locale;

/**
 * Costruisce i componenti grafici che si ripetono in tutte le schermate
 * (sfondo, pannello, titoli, pulsanti), assegnando a ciascuno la sua classe
 * di stile.
 *
 * <p>Colori, caratteri e immagini non compaiono mai nel codice Java: sono
 * definiti nel foglio di stile {@code holeshot.css}. Le schermate si
 * occupano così solo di cosa mostrare, non di come appare.</p>
 */
final class Componenti {

    static final String FOGLIO_DI_STILE = "/it/unicam/cs/mpgc/rpg129875/grafica/holeshot.css";

    static final String SFONDO_MENU = "sfondo-menu";
    static final String SFONDO_BOX = "sfondo-box";

    private static final String PREFISSO_SFONDO_PISTA = "sfondo-pista-";
    private static final double SPAZIO_TRA_ELEMENTI = 12;

    private Componenti() {
    }

    /** La radice di una schermata: lo sfondo indicato, con sopra i contenuti. */
    static StackPane schermata(String classeSfondo, Node... contenuti) {
        StackPane radice = new StackPane(contenuti);
        radice.getStyleClass().addAll("schermata", classeSfondo);
        return radice;
    }

    /**
     * La classe di stile dello sfondo per una condizione di pista, ricavata
     * dal suo codice: per dare uno sfondo a una nuova condizione basta
     * aggiungere una regola al foglio di stile.
     */
    static String sfondoPer(CondizionePista pista) {
        return PREFISSO_SFONDO_PISTA + pista.codice().toLowerCase(Locale.ROOT);
    }

    /** Un riquadro scuro semitrasparente, alto quanto il suo contenuto. */
    static VBox pannello(double larghezzaMassima, Node... contenuti) {
        VBox pannello = new VBox(SPAZIO_TRA_ELEMENTI, contenuti);
        pannello.getStyleClass().add("pannello");
        pannello.setMaxSize(larghezzaMassima, Region.USE_PREF_SIZE);
        return pannello;
    }

    /** Un riquadro più chiaro per raggruppare elementi all'interno di un pannello. */
    static VBox scheda(Node... contenuti) {
        VBox scheda = new VBox(8, contenuti);
        scheda.getStyleClass().add("scheda");
        return scheda;
    }

    static Label testo(String contenuto) {
        Label etichetta = new Label(contenuto);
        etichetta.setWrapText(true);
        return etichetta;
    }

    static Label testo(String contenuto, String classeDiStile) {
        Label etichetta = testo(contenuto);
        etichetta.getStyleClass().add(classeDiStile);
        return etichetta;
    }

    static Label titolo(String contenuto) {
        return testo(contenuto, "titolo");
    }

    static Label sottotitolo(String contenuto) {
        return testo(contenuto, "sottotitolo");
    }

    static Label intestazione(String contenuto) {
        return testo(contenuto.toUpperCase(Locale.ITALIAN), "intestazione");
    }

    static Label nota(String contenuto) {
        return testo(contenuto, "nota");
    }

    static Button pulsante(String etichetta, Runnable azione) {
        Button pulsante = new Button(etichetta);
        pulsante.setOnAction(evento -> azione.run());
        return pulsante;
    }

    static Button pulsantePrincipale(String etichetta, Runnable azione) {
        Button pulsante = pulsante(etichetta, azione);
        pulsante.getStyleClass().add("pulsante-principale");
        return pulsante;
    }

    /**
     * Una barra che mostra una quantità rispetto al suo massimo, larga
     * quanto lo spazio disponibile.
     */
    static ProgressBar barra(int valore, int massimo) {
        double frazione = massimo <= 0 ? 0 : Math.min(1.0, (double) valore / massimo);
        ProgressBar barra = new ProgressBar(frazione);
        barra.setMaxWidth(Double.MAX_VALUE);
        return barra;
    }
}
