package it.unicam.cs.mpgc.rpg129875.gui;

import it.unicam.cs.mpgc.rpg129875.engine.GestoreCarriera;
import it.unicam.cs.mpgc.rpg129875.model.Moto;
import it.unicam.cs.mpgc.rpg129875.model.StileDiGuida;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.List;
import java.util.function.Function;

/** Schermata di creazione di un nuovo pilota: nome, stile di guida, moto iniziale. */
final class SchermataCreazionePilota {

    private static final double LARGHEZZA_PANNELLO = 500;

    private SchermataCreazionePilota() {
    }

    static Parent crea(Navigatore navigatore, List<StileDiGuida> stili, List<Moto> motoDiSerie,
                       GestoreCarriera gestoreCarriera) {
        TextField campoNome = new TextField();
        campoNome.setPromptText("Nome del pilota");

        ComboBox<StileDiGuida> sceltaStile = new ComboBox<>();
        sceltaStile.getItems().addAll(stili);
        sceltaStile.setConverter(mostrando(StileDiGuida::nome));
        sceltaStile.setMaxWidth(Double.MAX_VALUE);
        Label descrizioneStile = Componenti.nota("");
        sceltaStile.valueProperty().addListener((osservabile, precedente, scelto) ->
                descrizioneStile.setText(scelto == null ? "" : descrizioneDi(scelto)));
        sceltaStile.getSelectionModel().selectFirst();

        ComboBox<Moto> sceltaMoto = new ComboBox<>();
        sceltaMoto.getItems().addAll(motoDiSerie);
        sceltaMoto.setConverter(mostrando(Moto::toString));
        sceltaMoto.setMaxWidth(Double.MAX_VALUE);
        Label descrizioneMoto = Componenti.nota("");
        sceltaMoto.valueProperty().addListener((osservabile, precedente, scelta) ->
                descrizioneMoto.setText(scelta == null ? "" : descrizioneDi(scelta)));
        sceltaMoto.getSelectionModel().selectFirst();

        Label errore = Componenti.testo("", "errore");

        Button conferma = Componenti.pulsantePrincipale("Inizia la carriera", () -> {
            String nome = campoNome.getText() == null ? "" : campoNome.getText().trim();
            if (nome.isEmpty()) {
                errore.setText("Inserisci un nome per il tuo pilota.");
                return;
            }
            navigatore.avviaCarriera(
                    gestoreCarriera.nuovaCarriera(nome, sceltaStile.getValue(), sceltaMoto.getValue()));
        });
        Button indietro = Componenti.pulsante("Indietro", navigatore::mostraMenu);
        HBox pulsanti = new HBox(10, conferma, indietro);
        pulsanti.setAlignment(Pos.CENTER_LEFT);

        VBox pannello = Componenti.pannello(LARGHEZZA_PANNELLO,
                Componenti.titolo("Crea il tuo pilota"),
                Componenti.intestazione("Nome"), campoNome,
                Componenti.intestazione("Stile di guida"), sceltaStile, descrizioneStile,
                Componenti.intestazione("Moto di serie"), sceltaMoto, descrizioneMoto,
                errore,
                pulsanti);
        return Componenti.schermata(Componenti.SFONDO_BOX, pannello);
    }

    private static String descrizioneDi(StileDiGuida stile) {
        return stile.descrizione() + "\n" + stile.statisticheIniziali();
    }

    private static String descrizioneDi(Moto moto) {
        return "Potenza " + moto.getPotenza() + "  ·  Maneggevolezza " + moto.getManeggevolezza()
                + "  ·  Scatto " + moto.getBonusPartenza();
    }

    /** Fa mostrare a una casella di scelta il testo ricavato da ogni elemento con la funzione indicata. */
    private static <T> StringConverter<T> mostrando(Function<T, String> comeTesto) {
        return new StringConverter<>() {
            @Override
            public String toString(T elemento) {
                return elemento == null ? "" : comeTesto.apply(elemento);
            }

            @Override
            public T fromString(String testo) {
                return null;
            }
        };
    }
}
