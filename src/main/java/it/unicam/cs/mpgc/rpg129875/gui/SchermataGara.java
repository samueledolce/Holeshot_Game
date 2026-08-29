package it.unicam.cs.mpgc.rpg129875.gui;

import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.Gara;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/** Schermata di presentazione della gara, prima del minigioco di partenza. */
final class SchermataGara {

    private SchermataGara() {
    }

    static Parent crea(Navigatore navigatore, Carriera carriera) {
        Gara gara = carriera.getCampionatoCorrente().tappaCorrente();

        Label titolo = new Label(gara.getNomeTappa());
        titolo.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label pista = new Label("Condizioni: " + gara.getPista().nome());
        Label avversari = new Label(gara.getNumeroAvversari() + " avversari al via.");

        Label istruzioni = new Label(
                "Quando il cancelletto si apre, premi SCATTA! il più velocemente possibile.\n"
                        + "Attenzione: premere prima dell'apertura è una falsa partenza.");
        istruzioni.setWrapText(true);
        istruzioni.setMaxWidth(420);

        Button vaiAllaPartenza = new Button("Al cancelletto!");
        vaiAllaPartenza.setOnAction(evento -> navigatore.mostraPartenza(gara));

        Button indietro = new Button("Torna alla carriera");
        indietro.setOnAction(evento -> navigatore.mostraCarriera());

        VBox box = new VBox(14, titolo, pista, avversari, istruzioni, vaiAllaPartenza, indietro);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        return box;
    }
}
