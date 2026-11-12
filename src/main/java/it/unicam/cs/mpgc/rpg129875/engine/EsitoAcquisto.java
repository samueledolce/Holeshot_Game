package it.unicam.cs.mpgc.rpg129875.engine;

/** Come può concludersi un tentativo di acquisto al negozio. */
public enum EsitoAcquisto {

    COMPLETATO("Acquisto completato."),
    CREDITI_INSUFFICIENTI("Non hai abbastanza crediti sponsor."),
    MOTO_GIA_IN_USO("Stai già usando questa moto.");

    private final String messaggio;

    EsitoAcquisto(String messaggio) {
        this.messaggio = messaggio;
    }

    public String getMessaggio() {
        return messaggio;
    }
}
