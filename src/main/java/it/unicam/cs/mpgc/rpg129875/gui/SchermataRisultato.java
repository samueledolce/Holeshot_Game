package it.unicam.cs.mpgc.rpg129875.gui;

import it.unicam.cs.mpgc.rpg129875.engine.EsitoGara;
import it.unicam.cs.mpgc.rpg129875.model.Abilita;
import it.unicam.cs.mpgc.rpg129875.model.ObiettivoSponsor;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoGara;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.stream.Collectors;

/** Mostra la classifica finale della gara e le ricompense ottenute. */
final class SchermataRisultato {

    private SchermataRisultato() {
    }

    static Parent crea(Navigatore navigatore, EsitoGara esito) {
        RisultatoGara risultato = esito.getRisultatoGara();

        String esitoTesto = risultato.isVittoria() ? "VITTORIA!"
                : risultato.isPodio() ? "Podio!"
                : "Arrivato " + risultato.getPosizioneGiocatore() + "°";

        Label titolo = new Label(esitoTesto);
        titolo.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label posizione = new Label("Posizione: " + risultato.getPosizioneGiocatore()
                + " su " + risultato.getNumeroPartecipanti());
        Label ricompense = new Label("+" + esito.getEsperienzaGuadagnata() + " esperienza, +"
                + esito.getDenaroGuadagnato() + " crediti sponsor");

        ListView<String> classifica = new ListView<>();
        classifica.getItems().addAll(risultato.getClassificaOrdinata());
        classifica.setPrefHeight(180);

        VBox box = new VBox(12, titolo, posizione, ricompense, new Label("Classifica:"), classifica);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        box.setMaxWidth(420);

        if (!esito.getNuoveAbilitaSbloccate().isEmpty()) {
            String nomiAbilita = esito.getNuoveAbilitaSbloccate().stream()
                    .map(Abilita::nome)
                    .collect(Collectors.joining(", "));
            Label nuoveAbilita = new Label("Nuove abilità sbloccate: " + nomiAbilita);
            nuoveAbilita.setWrapText(true);
            box.getChildren().add(nuoveAbilita);
        }

        for (ObiettivoSponsor obiettivo : esito.getObiettiviCompletati()) {
            Label completato = new Label("Obiettivo sponsor completato: " + obiettivo.getDescrizione()
                    + " (+" + obiettivo.getRicompensa() + " crediti)");
            completato.setWrapText(true);
            box.getChildren().add(completato);
        }

        Button continua = new Button("Torna alla carriera");
        continua.setOnAction(evento -> navigatore.mostraCarriera());
        box.getChildren().add(continua);

        return box;
    }
}
