package it.unicam.cs.mpgc.rpg129875.gui;

import it.unicam.cs.mpgc.rpg129875.engine.EsitoAcquisto;
import it.unicam.cs.mpgc.rpg129875.engine.Negozio;
import it.unicam.cs.mpgc.rpg129875.engine.OffertaMoto;
import it.unicam.cs.mpgc.rpg129875.model.Moto;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Il negozio: elenco delle moto in vendita, con il costo per il pilota e il pulsante per comprarle. */
final class SchermataNegozio {

    private SchermataNegozio() {
    }

    static Parent crea(Navigatore navigatore, Pilota pilota, Negozio negozio) {
        Label titolo = new Label("Negozio moto");
        titolo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label crediti = new Label("Crediti sponsor: " + pilota.getDenaro());
        Label motoAttuale = new Label("La tua moto: " + descrizione(pilota.getMotoAttuale())
                + " - vale " + negozio.valorePermuta(pilota.getMotoAttuale()) + " crediti in permuta");
        Label esito = new Label();

        VBox box = new VBox(10, titolo, crediti, motoAttuale);
        for (OffertaMoto offerta : negozio.offertePer(pilota)) {
            box.getChildren().add(rigaOfferta(navigatore, pilota, negozio, offerta, esito));
        }

        Button indietro = new Button("Torna alla carriera");
        indietro.setOnAction(evento -> navigatore.mostraCarriera());
        box.getChildren().addAll(esito, indietro);

        box.setPadding(new Insets(30));
        return box;
    }

    private static HBox rigaOfferta(Navigatore navigatore, Pilota pilota, Negozio negozio,
                                    OffertaMoto offerta, Label esito) {
        Label descrizione = new Label(descrizione(offerta.moto()));
        Button compra = new Button("Compra per " + offerta.costo() + " crediti");
        compra.setDisable(!offerta.allaPortata());
        compra.setOnAction(evento -> {
            EsitoAcquisto esitoAcquisto = negozio.acquista(pilota, offerta.moto());
            if (esitoAcquisto == EsitoAcquisto.COMPLETATO) {
                navigatore.mostraNegozio();
            } else {
                esito.setText(esitoAcquisto.getMessaggio());
            }
        });
        HBox riga = new HBox(12, compra, descrizione);
        riga.setAlignment(Pos.CENTER_LEFT);
        return riga;
    }

    private static String descrizione(Moto moto) {
        return moto + ": potenza " + moto.getPotenza() + ", maneggevolezza " + moto.getManeggevolezza()
                + ", scatto " + moto.getBonusPartenza();
    }
}
