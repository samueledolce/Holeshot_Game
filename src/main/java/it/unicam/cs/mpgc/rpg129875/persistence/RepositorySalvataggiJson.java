package it.unicam.cs.mpgc.rpg129875.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unicam.cs.mpgc.rpg129875.model.Carriera;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Implementazione su file JSON di {@link RepositorySalvataggi} per
 * l'aggregato {@link Carriera}.
 *
 * <p>Ogni salvataggio è un file "&lt;nome&gt;.json" in una cartella
 * dedicata; la conversione da/verso il modello di dominio è delegata a
 * {@link ConvertitoreCarriera}, cosicché Gson lavori solo su DTO "piatti"
 * (nessuna interfaccia da deserializzare in modo ambiguo).</p>
 */
public final class RepositorySalvataggiJson implements RepositorySalvataggi<Carriera> {

    private final Path cartellaSalvataggi;
    private final Gson gson;
    private final ConvertitoreCarriera convertitore = new ConvertitoreCarriera();

    public RepositorySalvataggiJson(Path cartellaSalvataggi) {
        this.cartellaSalvataggi = cartellaSalvataggi;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        creaCartellaSeNecessario();
    }

    public RepositorySalvataggiJson() {
        this(Path.of("saves"));
    }

    private void creaCartellaSeNecessario() {
        try {
            Files.createDirectories(cartellaSalvataggi);
        } catch (IOException e) {
            throw new IllegalStateException("Impossibile creare la cartella dei salvataggi: " + cartellaSalvataggi, e);
        }
    }

    private Path percorsoPer(String nomeSalvataggio) {
        String nomeFile = nomeSalvataggio.trim().replaceAll("[^a-zA-Z0-9_\\-]", "_") + ".json";
        return cartellaSalvataggi.resolve(nomeFile);
    }

    @Override
    public void salva(String nomeSalvataggio, Carriera carriera) throws PersistenzaException {
        CarrieraDTO dto = convertitore.versoDto(carriera);
        Path percorso = percorsoPer(nomeSalvataggio);
        try (Writer writer = Files.newBufferedWriter(percorso, StandardCharsets.UTF_8)) {
            gson.toJson(dto, writer);
        } catch (IOException e) {
            throw new PersistenzaException("Impossibile salvare la carriera \"" + nomeSalvataggio + "\"", e);
        }
    }

    @Override
    public Optional<Carriera> carica(String nomeSalvataggio) throws PersistenzaException {
        Path percorso = percorsoPer(nomeSalvataggio);
        if (!Files.exists(percorso)) {
            return Optional.empty();
        }
        try (Reader reader = Files.newBufferedReader(percorso, StandardCharsets.UTF_8)) {
            CarrieraDTO dto = gson.fromJson(reader, CarrieraDTO.class);
            if (dto == null) {
                return Optional.empty();
            }
            return Optional.of(convertitore.versoDominio(dto));
        } catch (IOException e) {
            throw new PersistenzaException("Impossibile caricare la carriera \"" + nomeSalvataggio + "\"", e);
        }
    }

    @Override
    public List<String> elencaSalvataggi() {
        List<String> nomi = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(cartellaSalvataggi, "*.json")) {
            for (Path file : stream) {
                String nomeFile = file.getFileName().toString();
                nomi.add(nomeFile.substring(0, nomeFile.length() - ".json".length()));
            }
        } catch (IOException e) {
            return List.of();
        }
        nomi.sort(Comparator.naturalOrder());
        return nomi;
    }

    @Override
    public void elimina(String nomeSalvataggio) throws PersistenzaException {
        Path percorso = percorsoPer(nomeSalvataggio);
        try {
            Files.deleteIfExists(percorso);
        } catch (IOException e) {
            throw new PersistenzaException("Impossibile eliminare la carriera \"" + nomeSalvataggio + "\"", e);
        }
    }
}
