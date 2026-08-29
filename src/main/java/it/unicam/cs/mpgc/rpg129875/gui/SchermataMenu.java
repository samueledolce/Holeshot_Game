package it.unicam.cs.mpgc.rpg129875.gui;

import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.persistence.RepositorySalvataggi;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.List;

/** Schermata iniziale: nuova carriera oppure caricamento di una esistente. */
final class SchermataMenu {

    private SchermataMenu() {
    }

    static Parent crea(Navigatore navigatore, RepositorySalvataggi<Carriera> repository) {
        Label titolo = new Label("HOLESHOT");
        titolo.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");

        Label sottotitolo = new Label("La partenza vince la gara.");
        sottotitolo.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

        Button nuovaCarriera = new Button("Nuova carriera");
        nuovaCarriera.setOnAction(evento -> navigatore.mostraCreazionePilota());

        List<String> salvataggi = repository.elencaSalvataggi();

        Label etichettaSalvataggi = new Label("Carriere salvate:");
        ListView<String> listaSalvataggi = new ListView<>();
        listaSalvataggi.getItems().addAll(salvataggi);
        listaSalvataggi.setPrefHeight(150);

        Button carica = new Button("Carica carriera selezionata");
        carica.setDisable(salvataggi.isEmpty());
        carica.setOnAction(evento -> {
            String selezionato = listaSalvataggi.getSelectionModel().getSelectedItem();
            if (selezionato != null) {
                navigatore.caricaCarriera(selezionato);
            }
        });

        VBox box = new VBox(14, titolo, sottotitolo, nuovaCarriera, etichettaSalvataggi, listaSalvataggi, carica);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));
        return box;
    }
}
