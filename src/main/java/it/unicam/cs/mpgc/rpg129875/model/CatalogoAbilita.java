package it.unicam.cs.mpgc.rpg129875.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Catalogo di tutte le abilità sbloccabili in carriera, indicizzate per
 * livello. Aggiungere una nuova abilità significa aggiungere una riga in
 * {@link #CatalogoAbilita()}: il resto del sistema di progressione non
 * cambia.
 */
public final class CatalogoAbilita {

    private final Map<Integer, Abilita> abilitaPerLivello = new LinkedHashMap<>();

    public CatalogoAbilita() {
        registra(new SangueFreddo());
        registra(new SestoSenso());
        registra(new PartenzaFulminea());
        registra(new RiflessiDaCampione());
    }

    private void registra(Abilita abilita) {
        abilitaPerLivello.put(abilita.livelloRichiesto(), abilita);
    }

    /** Restituisce l'abilità sbloccata esattamente al livello indicato, se esiste. */
    public Optional<Abilita> abilitaSbloccataAlLivello(int livello) {
        return Optional.ofNullable(abilitaPerLivello.get(livello));
    }

    public Map<Integer, Abilita> tutte() {
        return Map.copyOf(abilitaPerLivello);
    }
}
