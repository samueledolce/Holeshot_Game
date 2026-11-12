package it.unicam.cs.mpgc.rpg129875.gui;

import it.unicam.cs.mpgc.rpg129875.engine.EsitoAcquisto;
import it.unicam.cs.mpgc.rpg129875.engine.Negozio;
import it.unicam.cs.mpgc.rpg129875.engine.OffertaMoto;
import it.unicam.cs.mpgc.rpg129875.model.Moto;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

/** Il negozio: le moto in vendita, confrontate con quella del pilota, e il pulsante per comprarle. */
final class SchermataNegozio {

    private static final double LARGHEZZA_PANNELLO = 760;
    private static final double ALTEZZA_ELENCO = 250;

    private SchermataNegozio() {
    }

    static Parent crea(Navigatore navigatore, Pilota pilota, Negozio negozio) {
        Moto motoAttuale = pilota.getMotoAttuale();
        Label esito = Componenti.testo("", "errore");

        VBox schedaAttuale = Componenti.scheda(
                Componenti.intestazione("La tua moto"),
                Componenti.testo(motoAttuale.toString(), "valore"),
                Componenti.nota(caratteristiche(motoAttuale) + "  ·  vale "
                        + negozio.valorePermuta(motoAttuale) + " crediti in permuta"));
        schedaAttuale.getStyleClass().setAll("scheda-evidenziata");

        VBox elenco = new VBox(10);
        List<OffertaMoto> offerte = negozio.offertePer(pilota);
        if (offerte.isEmpty()) {
            elenco.getChildren().add(Componenti.nota("Non ci sono altre moto in vendita."));
        }
        for (OffertaMoto offerta : offerte) {
            elenco.getChildren().add(schedaOfferta(navigatore, pilota, negozio, offerta, esito));
        }
        ScrollPane areaScorrevole = new ScrollPane(elenco);
        areaScorrevole.setFitToWidth(true);
        areaScorrevole.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        areaScorrevole.setPrefHeight(ALTEZZA_ELENCO);

        Region spazio = new Region();
        HBox.setHgrow(spazio, Priority.ALWAYS);
        HBox intestazione = new HBox(10,
                Componenti.titolo("Negozio moto"),
                spazio,
                Componenti.testo(pilota.getDenaro() + " crediti sponsor", "valore"));
        intestazione.setAlignment(Pos.CENTER_LEFT);

        VBox pannello = Componenti.pannello(LARGHEZZA_PANNELLO,
                intestazione,
                schedaAttuale,
                Componenti.intestazione("In vendita"),
                areaScorrevole,
                esito,
                Componenti.pulsante("Torna alla carriera", navigatore::mostraCarriera));
        return Componenti.schermata(Componenti.SFONDO_BOX, pannello);
    }

    private static HBox schedaOfferta(Navigatore navigatore, Pilota pilota, Negozio negozio,
                                      OffertaMoto offerta, Label esito) {
        Moto moto = offerta.moto();
        VBox descrizione = new VBox(4,
                Componenti.testo(moto.toString(), "valore"),
                Componenti.nota(caratteristiche(moto)),
                Componenti.nota(confronto(moto, pilota.getMotoAttuale())));
        HBox.setHgrow(descrizione, Priority.ALWAYS);

        Button compra = Componenti.pulsantePrincipale("Compra per " + offerta.costo() + " crediti", () -> {
            EsitoAcquisto esitoAcquisto = negozio.acquista(pilota, moto);
            if (esitoAcquisto == EsitoAcquisto.COMPLETATO) {
                navigatore.mostraNegozio();
            } else {
                esito.setText(esitoAcquisto.getMessaggio());
            }
        });
        compra.setDisable(!offerta.allaPortata());
        compra.setMinWidth(Region.USE_PREF_SIZE);

        VBox acquisto = new VBox(4, compra, Componenti.nota("Prezzo di listino: " + moto.getPrezzo()));
        acquisto.setAlignment(Pos.CENTER_RIGHT);

        HBox scheda = new HBox(14, descrizione, acquisto);
        scheda.setAlignment(Pos.CENTER_LEFT);
        scheda.getStyleClass().add("scheda");
        return scheda;
    }

    private static String caratteristiche(Moto moto) {
        return "Potenza " + moto.getPotenza() + "  ·  Maneggevolezza " + moto.getManeggevolezza()
                + "  ·  Scatto " + moto.getBonusPartenza();
    }

    private static String confronto(Moto inVendita, Moto attuale) {
        int differenza = inVendita.punteggioComplessivo() - attuale.punteggioComplessivo();
        if (differenza > 0) {
            return "Rispetto alla tua: +" + differenza + " punti di qualità complessiva";
        }
        if (differenza < 0) {
            return "Rispetto alla tua: " + differenza + " punti di qualità complessiva";
        }
        return "Rispetto alla tua: stessa qualità complessiva";
    }
}
