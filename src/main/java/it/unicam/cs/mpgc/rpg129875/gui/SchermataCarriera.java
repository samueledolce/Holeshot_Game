package it.unicam.cs.mpgc.rpg129875.gui;

import it.unicam.cs.mpgc.rpg129875.engine.GestoreCarriera;
import it.unicam.cs.mpgc.rpg129875.model.Abilita;
import it.unicam.cs.mpgc.rpg129875.model.Campionato;
import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.Gara;
import it.unicam.cs.mpgc.rpg129875.model.ObiettivoSponsor;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.stream.Collectors;

/** Schermata "hub" di carriera: statistiche del pilota, prossima gara, salvataggio. */
final class SchermataCarriera {

    private SchermataCarriera() {
    }

    static Parent crea(Navigatore navigatore, Carriera carriera, GestoreCarriera gestoreCarriera) {
        Pilota pilota = carriera.getPilota();
        Campionato campionato = carriera.getCampionatoCorrente();

        Label titolo = new Label(pilota.getNome() + " - Livello " + pilota.getLivello());
        titolo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label stile = new Label("Stile: " + pilota.getStile().nome());
        Label statistiche = new Label(pilota.getStatistiche().toString());
        Label moto = new Label("Moto: " + pilota.getMotoAttuale());
        Label denaro = new Label("Crediti sponsor: " + pilota.getDenaro());

        String testoAbilita = pilota.getAbilitaSbloccate().isEmpty()
                ? "nessuna ancora"
                : pilota.getAbilitaSbloccate().stream().map(Abilita::nome).collect(Collectors.joining(", "));
        Label abilita = new Label("Abilità: " + testoAbilita);
        abilita.setWrapText(true);

        Label campionatoLabel = new Label("Campionato: " + campionato.getNome()
                + " (" + campionato.getPuntiGiocatore() + " punti)");

        VBox obiettivi = new VBox(4, new Label("Obiettivi sponsor:"));
        for (ObiettivoSponsor obiettivo : carriera.getObiettivi()) {
            obiettivi.getChildren().add(new Label(descrizione(obiettivo)));
        }

        Button prossimaGara = pulsanteProssimaGara(navigatore, carriera, gestoreCarriera);

        TextField campoNomeSalvataggio = new TextField(pilota.getNome());
        Button salva = new Button("Salva carriera");
        Label esitoSalvataggio = new Label();
        salva.setOnAction(evento -> {
            salva.setDisable(true);
            esitoSalvataggio.setText("Salvataggio in corso...");
            navigatore.salvaCarriera(campoNomeSalvataggio.getText(), () -> {
                salva.setDisable(false);
                esitoSalvataggio.setText("Carriera salvata.");
            });
        });

        Button vaiAlNegozio = new Button("Negozio moto");
        vaiAlNegozio.setOnAction(evento -> navigatore.mostraNegozio());

        Button menuPrincipale = new Button("Torna al menu");
        menuPrincipale.setOnAction(evento -> navigatore.mostraMenu());

        HBox rigaSalvataggio = new HBox(8, campoNomeSalvataggio, salva, esitoSalvataggio);
        rigaSalvataggio.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(10, titolo, stile, statistiche, moto, denaro, abilita, campionatoLabel, obiettivi,
                prossimaGara, vaiAlNegozio, rigaSalvataggio, menuPrincipale);
        box.setPadding(new Insets(30));
        box.setMaxWidth(560);
        return box;
    }

    private static String descrizione(ObiettivoSponsor obiettivo) {
        String stato = obiettivo.isCompletato()
                ? "completato"
                : obiettivo.getProgresso() + " su " + obiettivo.getTraguardo();
        return obiettivo.getDescrizione() + " (" + stato + ") - " + obiettivo.getRicompensa() + " crediti";
    }

    private static Button pulsanteProssimaGara(Navigatore navigatore, Carriera carriera,
                                               GestoreCarriera gestoreCarriera) {
        Campionato campionato = carriera.getCampionatoCorrente();
        if (!campionato.isConcluso()) {
            Gara prossima = campionato.tappaCorrente();
            Button bottone = new Button("Vai alla partenza: " + prossima.getNomeTappa()
                    + " (" + prossima.getPista().nome() + ")");
            bottone.setOnAction(evento -> navigatore.mostraGara());
            return bottone;
        }
        Optional<Campionato> prossimoCampionato = gestoreCarriera.prossimoCampionato(carriera);
        if (prossimoCampionato.isPresent()) {
            Button bottone = new Button("Inizia il " + prossimoCampionato.get().getNome());
            bottone.setOnAction(evento -> navigatore.passaAlProssimoCampionato());
            return bottone;
        }
        Button bottone = new Button("Hai completato tutti i campionati");
        bottone.setDisable(true);
        return bottone;
    }
}
