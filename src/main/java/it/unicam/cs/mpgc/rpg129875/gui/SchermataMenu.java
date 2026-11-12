package it.unicam.cs.mpgc.rpg129875.gui;

import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.persistence.RepositorySalvataggi;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

/** Schermata iniziale: nuova carriera oppure caricamento di una esistente. */
final class SchermataMenu {

    private static final double LARGHEZZA_PANNELLO = 380;
    private static final double LARGHEZZA_COLONNA = 460;
    private static final double ALTEZZA_ELENCO_SALVATAGGI = 140;
    private static final Insets MARGINE_DAL_BORDO = new Insets(20, 0, 20, 36);

    private SchermataMenu() {
    }

    static Parent crea(Navigatore navigatore, RepositorySalvataggi<Carriera> repository) {
        Label titolo = new Label("HOLESHOT");
        titolo.getStyleClass().add("titolo-gioco");
        titolo.setMinWidth(Region.USE_PREF_SIZE);
        Label motto = new Label("La partenza vince la gara.");
        motto.getStyleClass().add("motto");
        motto.setMinWidth(Region.USE_PREF_SIZE);

        Button nuovaCarriera = Componenti.pulsantePrincipale("Nuova carriera", navigatore::mostraCreazionePilota);
        nuovaCarriera.setMaxWidth(Double.MAX_VALUE);

        List<String> salvataggi = repository.elencaSalvataggi();
        ListView<String> elencoSalvataggi = new ListView<>();
        elencoSalvataggi.getItems().addAll(salvataggi);
        elencoSalvataggi.setPrefHeight(ALTEZZA_ELENCO_SALVATAGGI);
        elencoSalvataggi.setPlaceholder(Componenti.nota("Nessuna carriera salvata."));

        Button carica = Componenti.pulsante("Carica la carriera selezionata", () -> {
            String selezionata = elencoSalvataggi.getSelectionModel().getSelectedItem();
            if (selezionata != null) {
                navigatore.caricaCarriera(selezionata);
            }
        });
        carica.setDisable(salvataggi.isEmpty());
        carica.setMaxWidth(Double.MAX_VALUE);

        VBox pannello = Componenti.pannello(LARGHEZZA_PANNELLO,
                nuovaCarriera,
                Componenti.intestazione("Carriere salvate"),
                elencoSalvataggi,
                carica);

        // Titolo e menu stanno a sinistra: a destra lo sfondo mostra il pilota in salto.
        VBox colonna = new VBox(6, titolo, motto, pannello);
        VBox.setMargin(pannello, new Insets(22, 0, 0, 0));
        colonna.setAlignment(Pos.CENTER_LEFT);
        colonna.setMaxWidth(LARGHEZZA_COLONNA);

        StackPane radice = Componenti.schermata(Componenti.SFONDO_MENU, colonna);
        StackPane.setAlignment(colonna, Pos.CENTER_LEFT);
        StackPane.setMargin(colonna, MARGINE_DAL_BORDO);
        return radice;
    }
}
