package it.unicam.cs.mpgc.rpg129875.persistence;

import it.unicam.cs.mpgc.rpg129875.model.Abilita;
import it.unicam.cs.mpgc.rpg129875.model.Campionato;
import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.CatalogoAbilita;
import it.unicam.cs.mpgc.rpg129875.model.CondizionePista;
import it.unicam.cs.mpgc.rpg129875.model.Gara;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import it.unicam.cs.mpgc.rpg129875.model.Registri;
import it.unicam.cs.mpgc.rpg129875.model.Registro;
import it.unicam.cs.mpgc.rpg129875.model.StileDiGuida;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Converte tra il modello di dominio (che usa interfacce come
 * {@code StileDiGuida}, {@code Abilita}, {@code CondizionePista}) e i DTO
 * usati per la persistenza (che contengono solo tipi semplici,
 * direttamente serializzabili da Gson).
 *
 * <p>Al posto di un oggetto il cui tipo è un'interfaccia, nel DTO viene
 * scritto il suo codice; in lettura il codice viene ritradotto
 * nell'oggetto tramite un {@link Registro}. Il convertitore non conosce
 * quindi nessuna implementazione concreta: uno stile di guida o una
 * condizione di pista aggiunti in futuro vengono salvati e ricaricati
 * senza modificare questa classe.</p>
 */
final class ConvertitoreCarriera {

    private final Registro<StileDiGuida> stili;
    private final Registro<CondizionePista> condizioniPista;
    private final CatalogoAbilita catalogoAbilita;

    ConvertitoreCarriera() {
        this(Registri.stiliDiGuida(), Registri.condizioniPista(), new CatalogoAbilita());
    }

    ConvertitoreCarriera(Registro<StileDiGuida> stili, Registro<CondizionePista> condizioniPista,
                         CatalogoAbilita catalogoAbilita) {
        this.stili = stili;
        this.condizioniPista = condizioniPista;
        this.catalogoAbilita = catalogoAbilita;
    }

    CarrieraDTO versoDto(Carriera carriera) {
        CarrieraDTO dto = new CarrieraDTO();
        dto.pilota = versoDto(carriera.getPilota());
        dto.campionato = versoDto(carriera.getCampionatoCorrente());
        dto.storicoRisultati = List.copyOf(carriera.getStoricoRisultati());
        return dto;
    }

    /**
     * @throws IllegalArgumentException se il DTO è incompleto o contiene
     *                                  codici che nessun registro conosce
     */
    Carriera versoDominio(CarrieraDTO dto) {
        if (dto.pilota == null || dto.campionato == null) {
            throw new IllegalArgumentException("Salvataggio incompleto: mancano il pilota o il campionato.");
        }
        Carriera carriera = new Carriera(pilotaVersoDominio(dto.pilota), campionatoVersoDominio(dto.campionato));
        for (String risultato : elencoOVuoto(dto.storicoRisultati)) {
            carriera.aggiungiRisultato(risultato);
        }
        return carriera;
    }

    // ---- Pilota ----

    private PilotaDTO versoDto(Pilota pilota) {
        PilotaDTO dto = new PilotaDTO();
        dto.nome = pilota.getNome();
        dto.stileChiave = pilota.getStile().codice();
        dto.statistiche = pilota.getStatistiche().copia();
        dto.moto = pilota.getMotoAttuale();
        dto.livello = pilota.getLivello();
        dto.esperienza = pilota.getEsperienza();
        dto.denaro = pilota.getDenaro();
        dto.abilitaChiavi = pilota.getAbilitaSbloccate().stream()
                .map(Abilita::nome)
                .collect(Collectors.toList());
        return dto;
    }

    private Pilota pilotaVersoDominio(PilotaDTO dto) {
        Pilota pilota = new Pilota(dto.nome, stili.perCodice(dto.stileChiave), dto.statistiche, dto.moto);
        pilota.impostaLivello(dto.livello);
        pilota.impostaEsperienza(dto.esperienza);
        pilota.aggiungiDenaro(dto.denaro);
        for (String nomeAbilita : elencoOVuoto(dto.abilitaChiavi)) {
            abilitaConNome(nomeAbilita).ifPresent(pilota::sbloccaAbilita);
        }
        return pilota;
    }

    private Optional<Abilita> abilitaConNome(String nome) {
        return catalogoAbilita.tutte().values().stream()
                .filter(abilita -> abilita.nome().equals(nome))
                .findFirst();
    }

    // ---- Campionato / Gara ----

    CampionatoDTO versoDto(Campionato campionato) {
        CampionatoDTO dto = new CampionatoDTO();
        dto.nome = campionato.getNome();
        dto.tappe = campionato.getTappe().stream().map(this::versoDto).collect(Collectors.toList());
        dto.tappaCorrente = campionato.getIndiceTappaCorrente();
        dto.puntiGiocatore = campionato.getPuntiGiocatore();
        return dto;
    }

    Campionato campionatoVersoDominio(CampionatoDTO dto) {
        List<Gara> tappe = elencoOVuoto(dto.tappe).stream()
                .map(this::garaVersoDominio)
                .collect(Collectors.toList());
        Campionato campionato = new Campionato(dto.nome, tappe);
        campionato.impostaProgressione(dto.tappaCorrente, dto.puntiGiocatore);
        return campionato;
    }

    private GaraDTO versoDto(Gara gara) {
        GaraDTO dto = new GaraDTO();
        dto.nomeTappa = gara.getNomeTappa();
        dto.pistaChiave = gara.getPista().codice();
        dto.numeroAvversari = gara.getNumeroAvversari();
        dto.difficolta = gara.getDifficolta();
        return dto;
    }

    private Gara garaVersoDominio(GaraDTO dto) {
        return new Gara(dto.nomeTappa, condizioniPista.perCodice(dto.pistaChiave), dto.numeroAvversari,
                dto.difficolta);
    }

    /** Un elenco assente nel file (es. salvataggio di una versione precedente) equivale a un elenco vuoto. */
    private static <T> List<T> elencoOVuoto(List<T> lista) {
        return lista == null ? List.of() : lista;
    }
}
