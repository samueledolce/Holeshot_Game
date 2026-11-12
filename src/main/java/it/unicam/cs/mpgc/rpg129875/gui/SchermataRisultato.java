package it.unicam.cs.mpgc.rpg129875.gui;

import it.unicam.cs.mpgc.rpg129875.engine.EsitoGara;
import it.unicam.cs.mpgc.rpg129875.model.Abilita;
import it.unicam.cs.mpgc.rpg129875.model.Gara;
import it.unicam.cs.mpgc.rpg129875.model.ObiettivoSponsor;
import it.unicam.cs.mpgc.rpg129875.model.RisultatoGara;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/** Mostra la classifica finale della gara e tutto ciò che il pilota ha ottenuto. */
final class SchermataRisultato {

    private static final double LARGHEZZA_PANNELLO = 520;
    private static final double ALTEZZA_CLASSIFICA = 170;

    private SchermataRisultato() {
    }

    static Parent crea(Navigatore navigatore, Gara gara, EsitoGara esito) {
        RisultatoGara risultato = esito.getRisultatoGara();

        ListView<String> classifica = new ListView<>();
        classifica.getItems().addAll(righeClassifica(risultato));
        classifica.setPrefHeight(ALTEZZA_CLASSIFICA);
        classifica.getSelectionModel().select(risultato.getPosizioneGiocatore() - 1);

        VBox pannello = Componenti.pannello(LARGHEZZA_PANNELLO,
                Componenti.sottotitolo(gara.getNomeTappa()),
                Componenti.titolo(titoloPer(risultato)),
                Componenti.testo("+" + esito.getEsperienzaGuadagnata() + " esperienza  ·  +"
                        + esito.getDenaroGuadagnato() + " crediti sponsor", "valore"),
                Componenti.intestazione("Classifica"),
                classifica);

        for (Abilita abilita : esito.getNuoveAbilitaSbloccate()) {
            pannello.getChildren().add(Componenti.testo("Nuova abilità: " + abilita.nome() + ". "
                    + abilita.descrizione(), "successo"));
        }
        for (ObiettivoSponsor obiettivo : esito.getObiettiviCompletati()) {
            pannello.getChildren().add(Componenti.testo("Obiettivo sponsor completato: "
                    + obiettivo.getDescrizione() + " (+" + obiettivo.getRicompensa() + " crediti)", "successo"));
        }
        pannello.getChildren().add(Componenti.pulsantePrincipale("Torna alla carriera", navigatore::mostraCarriera));

        return Componenti.schermata(Componenti.sfondoPer(gara.getPista()), pannello);
    }

    private static String titoloPer(RisultatoGara risultato) {
        if (risultato.isVittoria()) {
            return "Vittoria!";
        }
        if (risultato.isPodio()) {
            return "Podio! " + risultato.getPosizioneGiocatore() + "° posto";
        }
        return risultato.getPosizioneGiocatore() + "° posto su " + risultato.getNumeroPartecipanti();
    }

    /** Le righe della classifica, numerate, con il giocatore messo in evidenza. */
    private static List<String> righeClassifica(RisultatoGara risultato) {
        List<String> nomi = risultato.getClassificaOrdinata();
        List<String> righe = new ArrayList<>();
        for (int i = 0; i < nomi.size(); i++) {
            int posizione = i + 1;
            String segnaposto = posizione == risultato.getPosizioneGiocatore() ? "   (tu)" : "";
            righe.add(posizione + ".  " + nomi.get(i) + segnaposto);
        }
        return righe;
    }
}
