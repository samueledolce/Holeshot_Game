package it.unicam.cs.mpgc.rpg129875;

import it.unicam.cs.mpgc.rpg129875.model.Campionato;
import it.unicam.cs.mpgc.rpg129875.model.CategoriaMoto;
import it.unicam.cs.mpgc.rpg129875.model.Gara;
import it.unicam.cs.mpgc.rpg129875.model.Moto;
import it.unicam.cs.mpgc.rpg129875.model.PistaAsciutta;
import it.unicam.cs.mpgc.rpg129875.model.PistaFangosa;
import it.unicam.cs.mpgc.rpg129875.model.PistaSabbiosa;

import java.util.List;

/**
 * Oggetti pronti all'uso per i test. Hanno valori fissi e non dipendono dai
 * file dei contenuti: così i test della logica di gioco non cambiano esito
 * quando si ritoccano moto e campionati nei file JSON.
 */
public final class DatiDiProva {

    private DatiDiProva() {
    }

    public static Moto motoLeggera() {
        return new Moto("Leggera 125", CategoriaMoto.MX_125, 40, 80, 65);
    }

    public static Moto motoMedia() {
        return new Moto("Media 250", CategoriaMoto.MX_250, 65, 65, 55);
    }

    public static Moto motoPotente() {
        return new Moto("Potente 450", CategoriaMoto.MX_450, 85, 45, 40);
    }

    /** Un campionato di cinque tappe su terreni diversi. */
    public static Campionato campionatoDiCinqueTappe() {
        return new Campionato("Campionato di prova", List.of(
                new Gara("Prima tappa", new PistaAsciutta(), 5),
                new Gara("Seconda tappa", new PistaFangosa(), 6),
                new Gara("Terza tappa", new PistaSabbiosa(), 6),
                new Gara("Quarta tappa", new PistaAsciutta(), 7),
                new Gara("Quinta tappa", new PistaFangosa(), 7)
        ));
    }

    /** Un campionato di una sola tappa, comodo da portare a conclusione. */
    public static Campionato campionatoDiUnaTappa(String nome) {
        return new Campionato(nome, List.of(new Gara("Tappa unica", new PistaAsciutta(), 3)));
    }
}
