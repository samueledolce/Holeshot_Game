package it.unicam.cs.mpgc.rpg129875.model;

import java.util.List;

/**
 * Elenco, in un unico punto, delle implementazioni disponibili per le
 * interfacce del modello che rappresentano un comportamento.
 *
 * <p>Aggiungere un nuovo stile di guida, una nuova condizione di pista o
 * un nuovo criterio per gli obiettivi sponsor richiede di scrivere la
 * nuova classe e di aggiungerla all'elenco
 * corrispondente qui sotto: schermate, salvataggi e contenuti di gioco la
 * troveranno tramite il suo codice, senza altre modifiche.</p>
 */
public final class Registri {

    private Registri() {
    }

    public static Registro<StileDiGuida> stiliDiGuida() {
        return new Registro<>(List.of(
                new StileAggressivo(),
                new StileTecnico(),
                new StileEquilibrato()
        ));
    }

    public static Registro<CondizionePista> condizioniPista() {
        return new Registro<>(List.of(
                new PistaAsciutta(),
                new PistaFangosa(),
                new PistaSabbiosa()
        ));
    }

    public static Registro<CriterioObiettivo> criteriObiettivo() {
        return new Registro<>(List.of(
                new CriterioQualitaPartenza("PARTENZA_PERFETTA", QualitaPartenza.PERFETTA),
                new CriterioQualitaPartenza("HOLESHOT", QualitaPartenza.OTTIMA),
                new CriterioPiazzamento("PODIO", 3),
                new CriterioPiazzamento("VITTORIA", 1)
        ));
    }
}
