package it.unicam.cs.mpgc.rpg129875.engine;

import it.unicam.cs.mpgc.rpg129875.model.CatalogoMoto;
import it.unicam.cs.mpgc.rpg129875.model.Moto;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Il negozio in cui il pilota spende i crediti sponsor per una moto
 * migliore.
 *
 * <p>Responsabilità unica: le regole di compravendita. Il pilota possiede
 * una sola moto alla volta: comprandone una nuova cede la vecchia in
 * permuta, e il suo valore viene scalato dal prezzo.</p>
 */
public final class Negozio {

    private static final int PERCENTUALE_VALORE_PERMUTA = 50;

    private final CatalogoMoto catalogo;

    public Negozio(CatalogoMoto catalogo) {
        this.catalogo = catalogo;
    }

    /** Le moto che il pilota può comprare: tutte quelle a pagamento, tranne quella che sta già usando. */
    public List<OffertaMoto> offertePer(Pilota pilota) {
        return catalogo.tutte().stream()
                .filter(moto -> !moto.isDiSerie())
                .filter(moto -> !moto.equals(pilota.getMotoAttuale()))
                .map(moto -> offerta(pilota, moto))
                .collect(Collectors.toList());
    }

    private OffertaMoto offerta(Pilota pilota, Moto moto) {
        int costo = costoPer(pilota, moto);
        return new OffertaMoto(moto, costo, pilota.puoSpendere(costo));
    }

    /** Quanto vale una moto ceduta in permuta. */
    public int valorePermuta(Moto moto) {
        return moto.getPrezzo() * PERCENTUALE_VALORE_PERMUTA / 100;
    }

    /** Quanto deve pagare il pilota per la moto indicata, cedendo in permuta quella attuale. */
    public int costoPer(Pilota pilota, Moto moto) {
        return Math.max(0, moto.getPrezzo() - valorePermuta(pilota.getMotoAttuale()));
    }

    /**
     * Tenta l'acquisto: se va a buon fine il pilota paga il costo e passa
     * alla nuova moto, altrimenti non cambia nulla.
     *
     * @throws IllegalArgumentException se la moto non è tra quelle del catalogo
     */
    public EsitoAcquisto acquista(Pilota pilota, Moto moto) {
        if (!catalogo.tutte().contains(moto)) {
            throw new IllegalArgumentException("Moto non in vendita: " + moto.getNome());
        }
        if (moto.equals(pilota.getMotoAttuale())) {
            return EsitoAcquisto.MOTO_GIA_IN_USO;
        }
        int costo = costoPer(pilota, moto);
        if (!pilota.puoSpendere(costo)) {
            return EsitoAcquisto.CREDITI_INSUFFICIENTI;
        }
        pilota.spendiDenaro(costo);
        pilota.cambiaMoto(moto);
        return EsitoAcquisto.COMPLETATO;
    }
}
