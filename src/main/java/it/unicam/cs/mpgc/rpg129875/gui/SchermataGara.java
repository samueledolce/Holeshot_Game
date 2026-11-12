package it.unicam.cs.mpgc.rpg129875.gui;

import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.Gara;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Schermata di presentazione della gara, prima del minigioco di partenza. */
final class SchermataGara {

    private static final double LARGHEZZA_PANNELLO = 520;

    private SchermataGara() {
    }

    static Parent crea(Navigatore navigatore, Carriera carriera) {
        Gara gara = carriera.getCampionatoCorrente().tappaCorrente();

        HBox pulsanti = new HBox(10,
                Componenti.pulsantePrincipale("Al cancelletto!", () -> navigatore.mostraPartenza(gara)),
                Componenti.pulsante("Torna alla carriera", navigatore::mostraCarriera));
        pulsanti.setAlignment(Pos.CENTER_LEFT);

        VBox pannello = Componenti.pannello(LARGHEZZA_PANNELLO,
                Componenti.sottotitolo(carriera.getCampionatoCorrente().getNome()),
                Componenti.titolo(gara.getNomeTappa()),
                Componenti.scheda(
                        Componenti.testo(gara.getPista().nome() + "  ·  " + gara.getNumeroAvversari()
                                + " avversari al via", "valore"),
                        Componenti.nota("Partenza perfetta entro " + gara.getPista().sogliaPerfettaMs()
                                + " ms, ottima entro " + gara.getPista().sogliaOttimaMs()
                                + " ms, buona entro " + gara.getPista().sogliaBuonaMs() + " ms.")),
                Componenti.testo("Quando la luce diventa verde e il cancelletto cade, premi SCATTA! "
                        + "il più velocemente possibile. Premere prima è una falsa partenza."),
                pulsanti);
        return Componenti.schermata(Componenti.sfondoPer(gara.getPista()), pannello);
    }
}
