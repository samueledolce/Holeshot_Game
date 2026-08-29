package it.unicam.cs.mpgc.rpg129875.persistence;

import it.unicam.cs.mpgc.rpg129875.model.Abilita;
import it.unicam.cs.mpgc.rpg129875.model.Campionato;
import it.unicam.cs.mpgc.rpg129875.model.CatalogoAbilita;
import it.unicam.cs.mpgc.rpg129875.model.CondizionePista;
import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.Gara;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import it.unicam.cs.mpgc.rpg129875.model.PistaAsciutta;
import it.unicam.cs.mpgc.rpg129875.model.PistaFangosa;
import it.unicam.cs.mpgc.rpg129875.model.PistaSabbiosa;
import it.unicam.cs.mpgc.rpg129875.model.StileAggressivo;
import it.unicam.cs.mpgc.rpg129875.model.StileDiGuida;
import it.unicam.cs.mpgc.rpg129875.model.StileEquilibrato;
import it.unicam.cs.mpgc.rpg129875.model.StileTecnico;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Converte tra il modello di dominio (che usa interfacce come
 * {@code StileDiGuida}, {@code Abilita}, {@code CondizionePista}) e i DTO
 * usati per la persistenza (che contengono solo tipi semplici,
 * direttamente serializzabili da Gson).
 *
 * <p>È l'unico punto del progetto che deve conoscere tutte le
 * implementazioni concrete di queste interfacce: aggiungerne una nuova
 * richiede di aggiornare le mappature qui, il resto dell'applicazione non
 * viene toccato.</p>
 */
final class ConvertitoreCarriera {

    private final CatalogoAbilita catalogoAbilita = new CatalogoAbilita();

    CarrieraDTO versoDto(Carriera carriera) {
        CarrieraDTO dto = new CarrieraDTO();
        dto.pilota = versoDto(carriera.getPilota());
        dto.campionato = versoDto(carriera.getCampionatoCorrente());
        dto.storicoRisultati = List.copyOf(carriera.getStoricoRisultati());
        return dto;
    }

    Carriera versoDominio(CarrieraDTO dto) {
        Pilota pilota = pilotaVersoDominio(dto.pilota);
        Campionato campionato = campionatoVersoDominio(dto.campionato);
        Carriera carriera = new Carriera(pilota, campionato);
        for (String risultato : dto.storicoRisultati) {
            carriera.aggiungiRisultato(risultato);
        }
        return carriera;
    }

    // ---- Pilota ----

    private PilotaDTO versoDto(Pilota pilota) {
        PilotaDTO dto = new PilotaDTO();
        dto.nome = pilota.getNome();
        dto.stileChiave = chiaveStile(pilota.getStile());
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
        StileDiGuida stile = stileDaChiave(dto.stileChiave);
        Pilota pilota = new Pilota(dto.nome, stile, dto.statistiche, dto.moto);
        pilota.impostaLivello(dto.livello);
        pilota.impostaEsperienza(dto.esperienza);
        pilota.aggiungiDenaro(dto.denaro);

        Set<Abilita> tutteLeAbilita = new LinkedHashSet<>(catalogoAbilita.tutte().values());
        for (String chiave : dto.abilitaChiavi) {
            tutteLeAbilita.stream()
                    .filter(abilita -> abilita.nome().equals(chiave))
                    .findFirst()
                    .ifPresent(pilota::sbloccaAbilita);
        }
        return pilota;
    }

    // ---- Campionato / Gara ----

    private CampionatoDTO versoDto(Campionato campionato) {
        CampionatoDTO dto = new CampionatoDTO();
        dto.nome = campionato.getNome();
        dto.tappe = campionato.getTappe().stream().map(this::versoDto).collect(Collectors.toList());
        dto.tappaCorrente = campionato.getIndiceTappaCorrente();
        dto.puntiGiocatore = campionato.getPuntiGiocatore();
        return dto;
    }

    private Campionato campionatoVersoDominio(CampionatoDTO dto) {
        List<Gara> tappe = dto.tappe.stream().map(this::garaVersoDominio).collect(Collectors.toList());
        Campionato campionato = new Campionato(dto.nome, tappe);
        campionato.impostaProgressione(dto.tappaCorrente, dto.puntiGiocatore);
        return campionato;
    }

    private GaraDTO versoDto(Gara gara) {
        GaraDTO dto = new GaraDTO();
        dto.nomeTappa = gara.getNomeTappa();
        dto.pistaChiave = chiavePista(gara.getPista());
        dto.numeroAvversari = gara.getNumeroAvversari();
        return dto;
    }

    private Gara garaVersoDominio(GaraDTO dto) {
        return new Gara(dto.nomeTappa, pistaDaChiave(dto.pistaChiave), dto.numeroAvversari);
    }

    // ---- Chiavi per le implementazioni concrete ----

    private static String chiaveStile(StileDiGuida stile) {
        if (stile instanceof StileAggressivo) {
            return "AGGRESSIVO";
        }
        if (stile instanceof StileTecnico) {
            return "TECNICO";
        }
        if (stile instanceof StileEquilibrato) {
            return "EQUILIBRATO";
        }
        throw new IllegalArgumentException("Stile di guida non gestito dal convertitore: " + stile.nome());
    }

    private static StileDiGuida stileDaChiave(String chiave) {
        return switch (chiave) {
            case "AGGRESSIVO" -> new StileAggressivo();
            case "TECNICO" -> new StileTecnico();
            case "EQUILIBRATO" -> new StileEquilibrato();
            default -> throw new IllegalArgumentException("Stile di guida sconosciuto nel salvataggio: " + chiave);
        };
    }

    private static String chiavePista(CondizionePista pista) {
        if (pista instanceof PistaAsciutta) {
            return "ASCIUTTA";
        }
        if (pista instanceof PistaFangosa) {
            return "FANGOSA";
        }
        if (pista instanceof PistaSabbiosa) {
            return "SABBIOSA";
        }
        throw new IllegalArgumentException("Condizione di pista non gestita dal convertitore: " + pista.nome());
    }

    private static CondizionePista pistaDaChiave(String chiave) {
        return switch (chiave) {
            case "ASCIUTTA" -> new PistaAsciutta();
            case "FANGOSA" -> new PistaFangosa();
            case "SABBIOSA" -> new PistaSabbiosa();
            default -> throw new IllegalArgumentException("Condizione di pista sconosciuta nel salvataggio: " + chiave);
        };
    }
}
