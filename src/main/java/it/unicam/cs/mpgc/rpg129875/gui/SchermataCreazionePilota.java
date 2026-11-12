package it.unicam.cs.mpgc.rpg129875.gui;

import it.unicam.cs.mpgc.rpg129875.model.Campionato;
import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.CatalogoCampionati;
import it.unicam.cs.mpgc.rpg129875.model.CatalogoMoto;
import it.unicam.cs.mpgc.rpg129875.model.Moto;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import it.unicam.cs.mpgc.rpg129875.model.Registri;
import it.unicam.cs.mpgc.rpg129875.model.StileDiGuida;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.List;
import java.util.function.Function;

/** Schermata di creazione di un nuovo pilota: nome, stile di guida, moto iniziale. */
final class SchermataCreazionePilota {

    private SchermataCreazionePilota() {
    }

    static Parent crea(Navigatore navigatore) {
        Label titolo = new Label("Crea il tuo pilota");
        titolo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField campoNome = new TextField();
        campoNome.setPromptText("Nome del pilota");

        List<StileDiGuida> stili = Registri.stiliDiGuida().tutti();
        ComboBox<StileDiGuida> comboStile = new ComboBox<>();
        comboStile.getItems().addAll(stili);
        comboStile.setConverter(convertitoreVisualizzazione(StileDiGuida::nome));

        Label descrizioneStile = new Label();
        descrizioneStile.setWrapText(true);
        descrizioneStile.setMaxWidth(420);
        comboStile.valueProperty().addListener((osservabile, vecchio, nuovo) ->
                descrizioneStile.setText(nuovo == null ? "" : nuovo.descrizione()));
        comboStile.getSelectionModel().selectFirst();

        List<Moto> moto = CatalogoMoto.disponibiliPerNuovaCarriera();
        ComboBox<Moto> comboMoto = new ComboBox<>();
        comboMoto.getItems().addAll(moto);
        comboMoto.setConverter(convertitoreVisualizzazione(Moto::getNome));
        comboMoto.getSelectionModel().selectFirst();

        Label errore = new Label();
        errore.setStyle("-fx-text-fill: #c0392b;");
        errore.setWrapText(true);

        Button conferma = new Button("Inizia la carriera");
        conferma.setOnAction(evento -> {
            String nome = campoNome.getText() == null ? "" : campoNome.getText().trim();
            if (nome.isEmpty()) {
                errore.setText("Inserisci un nome per il tuo pilota.");
                return;
            }
            Pilota pilota = new Pilota(nome, comboStile.getValue(), comboMoto.getValue());
            Campionato campionato = CatalogoCampionati.nuovoCampionatoRookie();
            navigatore.avviaCarriera(new Carriera(pilota, campionato));
        });

        Button indietro = new Button("Indietro");
        indietro.setOnAction(evento -> navigatore.mostraMenu());

        VBox box = new VBox(12,
                titolo,
                new Label("Nome"), campoNome,
                new Label("Stile di guida"), comboStile, descrizioneStile,
                new Label("Moto"), comboMoto,
                errore,
                conferma, indietro);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        box.setMaxWidth(480);
        return box;
    }

    private static <T> StringConverter<T> convertitoreVisualizzazione(Function<T, String> aStringa) {
        return new StringConverter<>() {
            @Override
            public String toString(T oggetto) {
                return oggetto == null ? "" : aStringa.apply(oggetto);
            }

            @Override
            public T fromString(String testo) {
                return null;
            }
        };
    }
}
