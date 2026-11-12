package it.unicam.cs.mpgc.rpg129875.model;

/**
 * Un obiettivo proposto dallo sponsor: un certo numero di gare che
 * soddisfano un {@link CriterioObiettivo}, in cambio di una ricompensa in
 * crediti.
 *
 * <p>Responsabilità unica: tenere il conto dei progressi verso il
 * traguardo. Non decide quali gare contano (lo fa il criterio) e non paga
 * la ricompensa (lo fa chi gestisce gli obiettivi).</p>
 */
public final class ObiettivoSponsor {

    private final String descrizione;
    private final CriterioObiettivo criterio;
    private final int traguardo;
    private final int ricompensa;
    private int progresso;

    /**
     * @param traguardo  quante gare devono soddisfare il criterio (almeno 1)
     * @param ricompensa crediti sponsor ottenuti al completamento
     */
    public ObiettivoSponsor(String descrizione, CriterioObiettivo criterio, int traguardo, int ricompensa) {
        if (traguardo < 1) {
            throw new IllegalArgumentException("Il traguardo di un obiettivo deve essere almeno 1.");
        }
        if (ricompensa < 0) {
            throw new IllegalArgumentException("La ricompensa di un obiettivo non può essere negativa.");
        }
        this.descrizione = descrizione;
        this.criterio = criterio;
        this.traguardo = traguardo;
        this.ricompensa = ricompensa;
        this.progresso = 0;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public CriterioObiettivo getCriterio() {
        return criterio;
    }

    public int getTraguardo() {
        return traguardo;
    }

    public int getRicompensa() {
        return ricompensa;
    }

    public int getProgresso() {
        return progresso;
    }

    public boolean isCompletato() {
        return progresso >= traguardo;
    }

    /**
     * Tiene conto di una gara appena disputata.
     *
     * @return {@code true} solo se l'obiettivo è stato completato proprio
     *         con questa gara: è il momento in cui spetta la ricompensa
     */
    public boolean registraGara(RisultatoPartenza partenza, RisultatoGara gara) {
        if (isCompletato() || !criterio.soddisfattoDa(partenza, gara)) {
            return false;
        }
        progresso++;
        return isCompletato();
    }

    /** Riservato alla ricostruzione da un salvataggio: impone i progressi già fatti. */
    public void impostaProgresso(int progresso) {
        this.progresso = Math.max(0, Math.min(traguardo, progresso));
    }
}
