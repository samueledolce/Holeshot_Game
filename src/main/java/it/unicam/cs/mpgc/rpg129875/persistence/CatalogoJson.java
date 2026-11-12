package it.unicam.cs.mpgc.rpg129875.persistence;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.unicam.cs.mpgc.rpg129875.model.Campionato;
import it.unicam.cs.mpgc.rpg129875.model.CatalogoCampionati;
import it.unicam.cs.mpgc.rpg129875.model.CatalogoMoto;
import it.unicam.cs.mpgc.rpg129875.model.Moto;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * I contenuti di gioco (moto e campionati) letti da file JSON inclusi
 * nell'applicazione.
 *
 * <p>Aggiungere una moto, una gara o un intero campionato significa
 * modificare un file di testo, senza toccare né ricompilare il codice
 * Java. I file vengono letti e controllati una sola volta, alla
 * creazione: se qualcosa non va l'applicazione si ferma subito con un
 * messaggio che indica il problema.</p>
 */
public final class CatalogoJson implements CatalogoMoto, CatalogoCampionati {

    /** Cartella, tra le risorse dell'applicazione, in cui si trovano i file dei contenuti. */
    public static final String CARTELLA_PREDEFINITA = "/it/unicam/cs/mpgc/rpg129875/dati/";

    private static final String FILE_MOTO = "moto.json";
    private static final String FILE_CAMPIONATI = "campionati.json";

    private final String cartellaRisorse;
    private final Gson gson = new Gson();
    private final ConvertitoreCatalogo convertitore = new ConvertitoreCatalogo(new ConvertitoreCarriera());

    private final List<Moto> moto;
    private final List<CampionatoDTO> modelliCampionato;

    public CatalogoJson() {
        this(CARTELLA_PREDEFINITA);
    }

    /**
     * @param cartellaRisorse percorso (che termina con "/") della cartella
     *                        delle risorse da cui leggere i file
     * @throws CatalogoNonValidoException se un file manca o non è valido
     */
    public CatalogoJson(String cartellaRisorse) {
        this.cartellaRisorse = cartellaRisorse;
        this.moto = convertitore.motoVersoDominio(Arrays.asList(leggi(FILE_MOTO, MotoDTO[].class)));
        this.modelliCampionato = Arrays.asList(leggi(FILE_CAMPIONATI, CampionatoDTO[].class));
        convertitore.controllaCampionati(modelliCampionato);
    }

    private <T> T leggi(String nomeFile, Class<T> tipo) {
        String percorso = cartellaRisorse + nomeFile;
        try (InputStream flusso = CatalogoJson.class.getResourceAsStream(percorso)) {
            if (flusso == null) {
                throw new CatalogoNonValidoException("File dei contenuti non trovato: " + percorso);
            }
            Reader lettore = new InputStreamReader(flusso, StandardCharsets.UTF_8);
            T contenuto = gson.fromJson(lettore, tipo);
            if (contenuto == null) {
                throw new CatalogoNonValidoException("Il file dei contenuti è vuoto: " + percorso);
            }
            return contenuto;
        } catch (IOException | JsonParseException e) {
            throw new CatalogoNonValidoException("Impossibile leggere " + percorso + ": " + e.getMessage(), e);
        }
    }

    // ---- CatalogoMoto ----

    @Override
    public List<Moto> tutte() {
        return moto;
    }

    // ---- CatalogoCampionati ----

    @Override
    public Campionato primo() {
        return convertitore.campionatoVersoDominio(modelliCampionato.get(0));
    }

    @Override
    public Optional<Campionato> successivoA(String nomeCampionato) {
        for (int i = 0; i < modelliCampionato.size() - 1; i++) {
            if (modelliCampionato.get(i).nome.equals(nomeCampionato)) {
                return Optional.of(convertitore.campionatoVersoDominio(modelliCampionato.get(i + 1)));
            }
        }
        return Optional.empty();
    }
}
