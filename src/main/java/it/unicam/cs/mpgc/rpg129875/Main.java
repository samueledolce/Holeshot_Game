package it.unicam.cs.mpgc.rpg129875;

import it.unicam.cs.mpgc.rpg129875.gui.HoleshotApplication;

/**
 * Punto di ingresso dell'applicazione.
 *
 * <p>Delega subito all'interfaccia grafica JavaFX. Separare questa classe
 * da {@code HoleshotApplication} lascia aperta la porta a un futuro
 * front-end diverso (es. a riga di comando, o per un'altra piattaforma)
 * senza dover toccare il resto del codice: basterebbe cambiare cosa viene
 * avviato qui.</p>
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        HoleshotApplication.main(args);
    }
}
