package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.model.Abilita;
import it.unicam.cs.mpgc.rpg129875.model.CatalogoAbilita;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsabile della progressione del pilota: quanta esperienza serve per
 * salire di livello, quanto crescono le statistiche e quali abilità si
 * sbloccano. {@code Pilota} si limita a contenere lo stato; qui vive la
 * regola (principio di Singola Responsabilità: Pilota cambia per motivi
 * legati al pilota, questa classe cambia se cambiano le regole di gioco).
 */
public final class GestoreProgressione {

    private static final int ESPERIENZA_BASE_LIVELLO = 100;
    private static final int CRESCITA_PER_LIVELLO = 2;

    private final CatalogoAbilita catalogoAbilita;

    public GestoreProgressione(CatalogoAbilita catalogoAbilita) {
        this.catalogoAbilita = catalogoAbilita;
    }

    public GestoreProgressione() {
        this(new CatalogoAbilita());
    }

    private int esperienzaRichiestaPer(int livello) {
        return livello * ESPERIENZA_BASE_LIVELLO;
    }

    /**
     * Assegna esperienza al pilota, lo fa salire di livello quante volte
     * serve e restituisce l'elenco delle abilità eventualmente sbloccate in
     * questo passaggio (può essere vuoto).
     */
    public List<Abilita> assegnaEsperienza(Pilota pilota, int esperienzaGuadagnata) {
        List<Abilita> nuoveAbilita = new ArrayList<>();
        int esperienzaTotale = pilota.getEsperienza() + esperienzaGuadagnata;
        int livello = pilota.getLivello();

        while (esperienzaTotale >= esperienzaRichiestaPer(livello)) {
            esperienzaTotale -= esperienzaRichiestaPer(livello);
            livello++;
            pilota.applicaCrescita(CRESCITA_PER_LIVELLO, CRESCITA_PER_LIVELLO, CRESCITA_PER_LIVELLO, CRESCITA_PER_LIVELLO);
            catalogoAbilita.abilitaSbloccataAlLivello(livello).ifPresent(abilita -> {
                pilota.sbloccaAbilita(abilita);
                nuoveAbilita.add(abilita);
            });
        }

        pilota.impostaLivello(livello);
        pilota.impostaEsperienza(esperienzaTotale);
        return nuoveAbilita;
    }
}
