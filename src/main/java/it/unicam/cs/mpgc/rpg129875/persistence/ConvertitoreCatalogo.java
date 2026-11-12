package it.unicam.cs.mpgc.rpg129875.persistence;

import it.unicam.cs.mpgc.rpg129875.model.Campionato;
import it.unicam.cs.mpgc.rpg129875.model.CategoriaMoto;
import it.unicam.cs.mpgc.rpg129875.model.Moto;
import it.unicam.cs.mpgc.rpg129875.model.ObiettivoSponsor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Trasforma i contenuti di gioco letti dai file JSON (DTO) negli oggetti
 * del modello, controllando che i dati abbiano senso.
 *
 * <p>Non usa Gson: riceve DTO già letti. Così tutti i controlli sui
 * contenuti si possono collaudare con semplici oggetti Java.</p>
 */
final class ConvertitoreCatalogo {

    private final ConvertitoreCarriera convertitoreCarriera;

    ConvertitoreCatalogo(ConvertitoreCarriera convertitoreCarriera) {
        this.convertitoreCarriera = convertitoreCarriera;
    }

    // ---- Moto ----

    List<Moto> motoVersoDominio(List<MotoDTO> elenco) {
        List<Moto> moto = new ArrayList<>();
        Set<String> nomiGiaVisti = new HashSet<>();
        for (MotoDTO dto : elenco) {
            Moto nuova = motoVersoDominio(dto);
            if (!nomiGiaVisti.add(nuova.getNome())) {
                throw new CatalogoNonValidoException("Due moto hanno lo stesso nome: " + nuova.getNome());
            }
            moto.add(nuova);
        }
        if (moto.stream().noneMatch(Moto::isDiSerie)) {
            throw new CatalogoNonValidoException(
                    "Serve almeno una moto di serie (prezzo 0) con cui iniziare la carriera.");
        }
        return List.copyOf(moto);
    }

    private Moto motoVersoDominio(MotoDTO dto) {
        if (dto == null || dto.nome == null || dto.nome.isBlank()) {
            throw new CatalogoNonValidoException("Una moto non ha il nome.");
        }
        if (dto.prezzo < 0) {
            throw new CatalogoNonValidoException("La moto \"" + dto.nome + "\" ha un prezzo negativo.");
        }
        return new Moto(dto.nome, categoriaDi(dto), dto.potenza, dto.maneggevolezza, dto.bonusPartenza,
                dto.prezzo);
    }

    private static CategoriaMoto categoriaDi(MotoDTO dto) {
        try {
            return CategoriaMoto.valueOf(dto.categoria);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new CatalogoNonValidoException("La moto \"" + dto.nome
                    + "\" ha una categoria sconosciuta: " + dto.categoria, e);
        }
    }

    // ---- Campionati ----

    /** Controlla che ogni campionato dell'elenco sia utilizzabile, senza modificarlo. */
    void controllaCampionati(List<CampionatoDTO> elenco) {
        if (elenco.isEmpty()) {
            throw new CatalogoNonValidoException("Serve almeno un campionato.");
        }
        Set<String> nomiGiaVisti = new HashSet<>();
        for (CampionatoDTO dto : elenco) {
            if (dto == null || dto.nome == null || dto.nome.isBlank()) {
                throw new CatalogoNonValidoException("Un campionato non ha il nome.");
            }
            if (!nomiGiaVisti.add(dto.nome)) {
                throw new CatalogoNonValidoException("Due campionati hanno lo stesso nome: " + dto.nome);
            }
            controllaTappe(dto);
            campionatoVersoDominio(dto);
        }
    }

    private static void controllaTappe(CampionatoDTO dto) {
        if (dto.tappe == null || dto.tappe.isEmpty()) {
            throw new CatalogoNonValidoException("Il campionato \"" + dto.nome + "\" non ha tappe.");
        }
        for (GaraDTO tappa : dto.tappe) {
            if (tappa == null || tappa.nomeTappa == null || tappa.nomeTappa.isBlank()) {
                throw new CatalogoNonValidoException("Una tappa del campionato \"" + dto.nome
                        + "\" non ha il nome.");
            }
            if (tappa.numeroAvversari < 1) {
                throw new CatalogoNonValidoException("La tappa \"" + tappa.nomeTappa + "\" del campionato \""
                        + dto.nome + "\" deve avere almeno un avversario.");
            }
        }
    }

    /** Crea un campionato nuovo, ancora da disputare, a partire dal suo modello. */
    Campionato campionatoVersoDominio(CampionatoDTO dto) {
        try {
            return convertitoreCarriera.campionatoVersoDominio(dto);
        } catch (IllegalArgumentException e) {
            throw new CatalogoNonValidoException("Il campionato \"" + dto.nome + "\" non è valido: "
                    + e.getMessage(), e);
        }
    }

    // ---- Obiettivi sponsor ----

    /** Crea obiettivi nuovi, con i progressi a zero, a partire dai loro modelli. */
    List<ObiettivoSponsor> obiettiviVersoDominio(List<ObiettivoDTO> elenco) {
        return elenco.stream().map(this::obiettivoVersoDominio).collect(Collectors.toList());
    }

    private ObiettivoSponsor obiettivoVersoDominio(ObiettivoDTO dto) {
        if (dto == null || dto.descrizione == null || dto.descrizione.isBlank()) {
            throw new CatalogoNonValidoException("Un obiettivo sponsor non ha la descrizione.");
        }
        try {
            ObiettivoSponsor obiettivo = convertitoreCarriera.obiettivoVersoDominio(dto);
            obiettivo.impostaProgresso(0);
            return obiettivo;
        } catch (IllegalArgumentException e) {
            throw new CatalogoNonValidoException("L'obiettivo \"" + dto.descrizione + "\" non è valido: "
                    + e.getMessage(), e);
        }
    }
}
