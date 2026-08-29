package it.unicam.cs.mpgc.rpg129875.model;

import java.util.List;

/** Catalogo delle moto disponibili all'inizio di una nuova carriera. */
public final class CatalogoMoto {

    private CatalogoMoto() {
    }

    public static List<Moto> disponibiliPerNuovaCarriera() {
        return List.of(
                new Moto("Falco 125", CategoriaMoto.MX_125, 40, 80, 65),
                new Moto("Tempesta 250", CategoriaMoto.MX_250, 65, 65, 55),
                new Moto("Titano 450", CategoriaMoto.MX_450, 85, 45, 40)
        );
    }
}
