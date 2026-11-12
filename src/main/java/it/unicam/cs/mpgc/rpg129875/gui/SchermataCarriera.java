package it.unicam.cs.mpgc.rpg129875.gui;

import it.unicam.cs.mpgc.rpg129875.engine.GestoreCarriera;
import it.unicam.cs.mpgc.rpg129875.engine.GestoreProgressione;
import it.unicam.cs.mpgc.rpg129875.model.Abilita;
import it.unicam.cs.mpgc.rpg129875.model.Campionato;
import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.Gara;
import it.unicam.cs.mpgc.rpg129875.model.ObiettivoSponsor;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import it.unicam.cs.mpgc.rpg129875.model.Statistiche;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.stream.Collectors;

/** Schermata centrale della carriera: il pilota, il campionato, gli obiettivi sponsor, il salvataggio. */
final class SchermataCarriera {

    private static final double LARGHEZZA_PANNELLO = 940;
    private static final double LARGHEZZA_COLONNA_PILOTA = 360;
    private static final double LARGHEZZA_BARRA_OBIETTIVO = 90;
    private static final double LARGHEZZA_STATO_OBIETTIVO = 118;
    private static final int STATISTICA_MASSIMA = 100;

    private SchermataCarriera() {
    }

    static Parent crea(Navigatore navigatore, Carriera carriera, GestoreCarriera gestoreCarriera,
                       GestoreProgressione gestoreProgressione) {
        Pilota pilota = carriera.getPilota();

        VBox colonnaPilota = new VBox(12,
                schedaPilota(pilota, gestoreProgressione),
                schedaMoto(navigatore, pilota));
        colonnaPilota.setPrefWidth(LARGHEZZA_COLONNA_PILOTA);
        colonnaPilota.setMinWidth(LARGHEZZA_COLONNA_PILOTA);

        VBox colonnaGare = new VBox(12,
                schedaCampionato(navigatore, carriera, gestoreCarriera),
                schedaObiettivi(carriera));
        HBox.setHgrow(colonnaGare, Priority.ALWAYS);

        HBox colonne = new HBox(14, colonnaPilota, colonnaGare);

        VBox pannello = Componenti.pannello(LARGHEZZA_PANNELLO,
                Componenti.titolo(pilota.getNome()),
                colonne,
                rigaSalvataggio(navigatore, pilota));
        return Componenti.schermata(Componenti.SFONDO_BOX, pannello);
    }

    // ---- Pilota ----

    private static VBox schedaPilota(Pilota pilota, GestoreProgressione gestoreProgressione) {
        int esperienzaRichiesta = gestoreProgressione.esperienzaRichiestaPer(pilota.getLivello());
        Statistiche statistiche = pilota.getStatistiche();

        GridPane griglia = new GridPane();
        griglia.setHgap(10);
        griglia.setVgap(7);
        aggiungiStatistica(griglia, 0, "Riflessi", statistiche.getRiflessi());
        aggiungiStatistica(griglia, 1, "Concentrazione", statistiche.getConcentrazione());
        aggiungiStatistica(griglia, 2, "Tecnica di partenza", statistiche.getTecnicaPartenza());
        aggiungiStatistica(griglia, 3, "Resistenza", statistiche.getResistenza());

        String abilita = pilota.getAbilitaSbloccate().isEmpty()
                ? "Nessuna abilità sbloccata: si ottengono salendo di livello."
                : "Abilità: " + pilota.getAbilitaSbloccate().stream()
                        .map(Abilita::nome)
                        .collect(Collectors.joining(", "));

        return Componenti.scheda(
                Componenti.sottotitolo("Livello " + pilota.getLivello() + "  ·  Stile "
                        + pilota.getStile().nome().toLowerCase()),
                Componenti.barra(pilota.getEsperienza(), esperienzaRichiesta),
                Componenti.nota("Esperienza: " + pilota.getEsperienza() + " su " + esperienzaRichiesta),
                griglia,
                Componenti.nota(abilita));
    }

    private static void aggiungiStatistica(GridPane griglia, int riga, String nome, int valore) {
        ProgressBar barra = Componenti.barra(valore, STATISTICA_MASSIMA);
        GridPane.setHgrow(barra, Priority.ALWAYS);
        griglia.add(Componenti.testo(nome), 0, riga);
        griglia.add(barra, 1, riga);
        griglia.add(Componenti.testo(String.valueOf(valore), "valore"), 2, riga);
    }

    private static VBox schedaMoto(Navigatore navigatore, Pilota pilota) {
        Label crediti = Componenti.testo(pilota.getDenaro() + " crediti sponsor", "valore");
        Button negozio = Componenti.pulsante("Negozio moto", navigatore::mostraNegozio);
        Region spazio = new Region();
        HBox.setHgrow(spazio, Priority.ALWAYS);
        HBox rigaCrediti = new HBox(10, crediti, spazio, negozio);
        rigaCrediti.setAlignment(Pos.CENTER_LEFT);

        return Componenti.scheda(
                Componenti.intestazione("Moto"),
                Componenti.testo(pilota.getMotoAttuale().toString(), "valore"),
                Componenti.nota("Potenza " + pilota.getMotoAttuale().getPotenza()
                        + "  ·  Maneggevolezza " + pilota.getMotoAttuale().getManeggevolezza()
                        + "  ·  Scatto " + pilota.getMotoAttuale().getBonusPartenza()),
                rigaCrediti);
    }

    // ---- Campionato ----

    private static VBox schedaCampionato(Navigatore navigatore, Carriera carriera,
                                         GestoreCarriera gestoreCarriera) {
        Campionato campionato = carriera.getCampionatoCorrente();
        int tappeTotali = campionato.getTappe().size();
        int tappeDisputate = Math.min(campionato.getIndiceTappaCorrente(), tappeTotali);

        Button prossimoPasso = pulsanteProssimoPasso(navigatore, carriera, gestoreCarriera);
        prossimoPasso.setMaxWidth(Double.MAX_VALUE);

        return Componenti.scheda(
                Componenti.intestazione("Campionato"),
                Componenti.testo(campionato.getNome() + "  ·  " + campionato.getPuntiGiocatore() + " punti",
                        "valore"),
                Componenti.barra(tappeDisputate, tappeTotali),
                Componenti.nota("Tappe disputate: " + tappeDisputate + " su " + tappeTotali),
                prossimoPasso);
    }

    private static Button pulsanteProssimoPasso(Navigatore navigatore, Carriera carriera,
                                                GestoreCarriera gestoreCarriera) {
        Campionato campionato = carriera.getCampionatoCorrente();
        if (!campionato.isConcluso()) {
            Gara prossima = campionato.tappaCorrente();
            return Componenti.pulsantePrincipale("Prossima gara: " + prossima.getNomeTappa()
                    + " (" + prossima.getPista().nome().toLowerCase() + ")", navigatore::mostraGara);
        }
        Optional<Campionato> prossimoCampionato = gestoreCarriera.prossimoCampionato(carriera);
        if (prossimoCampionato.isPresent()) {
            return Componenti.pulsantePrincipale("Inizia il " + prossimoCampionato.get().getNome(),
                    navigatore::passaAlProssimoCampionato);
        }
        Button nessunAltro = Componenti.pulsante("Hai completato tutti i campionati", () -> { });
        nessunAltro.setDisable(true);
        return nessunAltro;
    }

    // ---- Obiettivi sponsor ----

    private static VBox schedaObiettivi(Carriera carriera) {
        VBox scheda = Componenti.scheda(Componenti.intestazione("Obiettivi sponsor"));
        if (carriera.getObiettivi().isEmpty()) {
            scheda.getChildren().add(Componenti.nota("Questa carriera non ha obiettivi sponsor."));
        }
        for (ObiettivoSponsor obiettivo : carriera.getObiettivi()) {
            scheda.getChildren().add(rigaObiettivo(obiettivo));
        }
        return scheda;
    }

    private static HBox rigaObiettivo(ObiettivoSponsor obiettivo) {
        Label descrizione = Componenti.testo(obiettivo.getDescrizione());
        descrizione.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(descrizione, Priority.ALWAYS);

        ProgressBar barra = Componenti.barra(obiettivo.getProgresso(), obiettivo.getTraguardo());
        barra.setMinWidth(LARGHEZZA_BARRA_OBIETTIVO);
        barra.setMaxWidth(LARGHEZZA_BARRA_OBIETTIVO);

        Label stato;
        if (obiettivo.isCompletato()) {
            barra.getStyleClass().add("barra-completata");
            stato = Componenti.testo("Completato", "successo");
        } else {
            stato = Componenti.nota(obiettivo.getProgresso() + " su " + obiettivo.getTraguardo()
                    + "  ·  " + obiettivo.getRicompensa() + " crediti");
        }
        stato.setMinWidth(LARGHEZZA_STATO_OBIETTIVO);

        HBox riga = new HBox(10, descrizione, barra, stato);
        riga.setAlignment(Pos.CENTER_LEFT);
        return riga;
    }

    // ---- Salvataggio ----

    private static HBox rigaSalvataggio(Navigatore navigatore, Pilota pilota) {
        TextField nomeSalvataggio = new TextField(pilota.getNome());
        nomeSalvataggio.setPromptText("Nome del salvataggio");
        Label esito = Componenti.nota("");

        Button salva = new Button("Salva carriera");
        salva.setOnAction(evento -> {
            String nome = nomeSalvataggio.getText() == null ? "" : nomeSalvataggio.getText().trim();
            if (nome.isEmpty()) {
                esito.setText("Dai un nome al salvataggio.");
                return;
            }
            salva.setDisable(true);
            esito.setText("Salvataggio in corso...");
            navigatore.salvaCarriera(nome,
                    () -> {
                        salva.setDisable(false);
                        esito.setText("Carriera salvata.");
                    },
                    () -> {
                        salva.setDisable(false);
                        esito.setText("Salvataggio non riuscito.");
                    });
        });

        Region spazio = new Region();
        HBox.setHgrow(spazio, Priority.ALWAYS);
        Button menu = Componenti.pulsante("Torna al menu", navigatore::mostraMenu);

        HBox riga = new HBox(10, nomeSalvataggio, salva, esito, spazio, menu);
        riga.setAlignment(Pos.CENTER_LEFT);
        return riga;
    }
}
