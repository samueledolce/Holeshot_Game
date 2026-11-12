package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.model.Campionato;
import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.CatalogoCampionati;
import it.unicam.cs.mpgc.rpg129875.model.CatalogoObiettivi;
import it.unicam.cs.mpgc.rpg129875.model.Moto;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import it.unicam.cs.mpgc.rpg129875.model.StileDiGuida;

import java.util.Optional;

/**
 * Regole che governano il percorso di una carriera: come nasce e come si
 * passa da un campionato al successivo.
 *
 * <p>Tenere queste regole qui, e non nelle schermate, permette di
 * riutilizzarle con un'interfaccia grafica diversa e di collaudarle senza
 * avviare JavaFX.</p>
 */
public final class GestoreCarriera {

    private final CatalogoCampionati catalogoCampionati;
    private final CatalogoObiettivi catalogoObiettivi;

    public GestoreCarriera(CatalogoCampionati catalogoCampionati, CatalogoObiettivi catalogoObiettivi) {
        this.catalogoCampionati = catalogoCampionati;
        this.catalogoObiettivi = catalogoObiettivi;
    }

    /**
     * Crea una carriera che parte dal primo campionato del catalogo, con
     * gli obiettivi sponsor ancora tutti da raggiungere.
     *
     * @throws IllegalArgumentException se il nome del pilota è vuoto
     */
    public Carriera nuovaCarriera(String nomePilota, StileDiGuida stile, Moto motoIniziale) {
        if (nomePilota == null || nomePilota.isBlank()) {
            throw new IllegalArgumentException("Il pilota deve avere un nome.");
        }
        Pilota pilota = new Pilota(nomePilota.trim(), stile, motoIniziale);
        return new Carriera(pilota, catalogoCampionati.primo(), catalogoObiettivi.nuoviObiettivi());
    }

    /** Il campionato a cui il giocatore può passare: esiste solo quando quello in corso è concluso. */
    public Optional<Campionato> prossimoCampionato(Carriera carriera) {
        Campionato corrente = carriera.getCampionatoCorrente();
        if (!corrente.isConcluso()) {
            return Optional.empty();
        }
        return catalogoCampionati.successivoA(corrente.getNome());
    }

    /**
     * Fa iniziare alla carriera il campionato successivo, annotando nello
     * storico il risultato di quello appena concluso.
     *
     * @return {@code false} se non c'è un campionato a cui passare
     */
    public boolean passaAlProssimoCampionato(Carriera carriera) {
        Optional<Campionato> prossimo = prossimoCampionato(carriera);
        if (prossimo.isEmpty()) {
            return false;
        }
        Campionato concluso = carriera.getCampionatoCorrente();
        carriera.aggiungiRisultato(concluso.getNome() + " concluso con "
                + concluso.getPuntiGiocatore() + " punti");
        carriera.iniziaNuovoCampionato(prossimo.get());
        return true;
    }
}
