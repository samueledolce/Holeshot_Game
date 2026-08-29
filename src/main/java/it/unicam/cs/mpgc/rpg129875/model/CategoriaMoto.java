package it.unicam.cs.mpgc.rpg129875.model;

/** Le categorie di moto da cross disponibili in carriera. */
public enum CategoriaMoto {

    MX_125("125cc"),
    MX_250("250cc"),
    MX_450("450cc");

    private final String etichetta;

    CategoriaMoto(String etichetta) {
        this.etichetta = etichetta;
    }

    public String getEtichetta() {
        return etichetta;
    }
}
