package it.unicam.cs.mpgc.rpg129875.model;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Il pilota controllato dal giocatore. Contiene solo dati e comportamenti
 * propri del pilota (nome, statistiche, livello, moto, abilità, denaro).
 *
 * <p>La logica di progressione (quanta esperienza serve, quando si sale di
 * livello) è responsabilità di {@code GestoreProgressione}; la logica di
 * gara è responsabilità del motore di gara. Pilota si limita a contenere e
 * proteggere il proprio stato (principio di Singola Responsabilità).</p>
 */
public final class Pilota {

    private String nome;
    private final StileDiGuida stile;
    private final Statistiche statistiche;
    private Moto motoAttuale;
    private int livello;
    private int esperienza;
    private int denaro;
    private final Set<Abilita> abilitaSbloccate = new LinkedHashSet<>();

    public Pilota(String nome, StileDiGuida stile, Moto motoIniziale) {
        this(nome, stile, stile.statisticheIniziali(), motoIniziale);
    }

    /** Costruttore usato per ricostruire un pilota con statistiche già note (es. da un salvataggio). */
    public Pilota(String nome, StileDiGuida stile, Statistiche statistiche, Moto motoIniziale) {
        this.nome = nome;
        this.stile = stile;
        this.statistiche = statistiche;
        this.motoAttuale = motoIniziale;
        this.livello = 1;
        this.esperienza = 0;
        this.denaro = 0;
    }

    public String getNome() {
        return nome;
    }

    public void rinomina(String nuovoNome) {
        this.nome = nuovoNome;
    }

    public StileDiGuida getStile() {
        return stile;
    }

    public Statistiche getStatistiche() {
        return statistiche;
    }

    public Moto getMotoAttuale() {
        return motoAttuale;
    }

    public void cambiaMoto(Moto nuovaMoto) {
        this.motoAttuale = nuovaMoto;
    }

    public int getLivello() {
        return livello;
    }

    /** Riservato al sistema di progressione: imposta il livello raggiunto. */
    public void impostaLivello(int livello) {
        this.livello = livello;
    }

    public int getEsperienza() {
        return esperienza;
    }

    /** Riservato al sistema di progressione: imposta l'esperienza residua nel livello corrente. */
    public void impostaEsperienza(int esperienza) {
        this.esperienza = esperienza;
    }

    /** Riservato al sistema di progressione: applica la crescita delle statistiche per un livello. */
    public void applicaCrescita(int deltaRiflessi, int deltaConcentrazione, int deltaTecnica, int deltaResistenza) {
        statistiche.aumenta(deltaRiflessi, deltaConcentrazione, deltaTecnica, deltaResistenza);
    }

    public int getDenaro() {
        return denaro;
    }

    public void aggiungiDenaro(int quantita) {
        this.denaro = Math.max(0, this.denaro + quantita);
    }

    public boolean puoSpendere(int quantita) {
        return quantita <= denaro;
    }

    /**
     * Toglie al pilota i crediti indicati.
     *
     * @throws IllegalStateException se il pilota non ha abbastanza crediti
     */
    public void spendiDenaro(int quantita) {
        if (quantita < 0) {
            throw new IllegalArgumentException("La spesa non può essere negativa: " + quantita);
        }
        if (!puoSpendere(quantita)) {
            throw new IllegalStateException("Crediti insufficienti: servono " + quantita
                    + ", disponibili " + denaro);
        }
        this.denaro -= quantita;
    }

    public Set<Abilita> getAbilitaSbloccate() {
        return Set.copyOf(abilitaSbloccate);
    }

    public void sbloccaAbilita(Abilita abilita) {
        abilitaSbloccate.add(abilita);
    }

    public boolean haAbilita(Abilita abilita) {
        return abilitaSbloccate.contains(abilita);
    }

    /** Tempo di reazione "percepito" dopo aver applicato tutte le abilità sbloccate dal pilota. */
    public long applicaAbilitaAlTempo(long tempoReazioneMs) {
        long tempoCorretto = tempoReazioneMs;
        for (Abilita abilita : abilitaSbloccate) {
            tempoCorretto = abilita.applica(tempoCorretto);
        }
        return tempoCorretto;
    }
}
