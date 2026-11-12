package it.unicam.cs.mpgc.rpg129875.persistence;

import it.unicam.cs.mpgc.rpg129875.DatiDiProva;
import it.unicam.cs.mpgc.rpg129875.model.Campionato;
import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.CategoriaMoto;
import it.unicam.cs.mpgc.rpg129875.model.CriterioPiazzamento;
import it.unicam.cs.mpgc.rpg129875.model.Gara;
import it.unicam.cs.mpgc.rpg129875.model.Moto;
import it.unicam.cs.mpgc.rpg129875.model.ObiettivoSponsor;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
import it.unicam.cs.mpgc.rpg129875.model.PistaSabbiosa;
import it.unicam.cs.mpgc.rpg129875.model.StileAggressivo;
import it.unicam.cs.mpgc.rpg129875.model.StileTecnico;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositorySalvataggiJsonTest {

    @Test
    void unaCarrieraSalvataVieneRicaricataIdentica(@TempDir Path cartellaTemporanea) throws PersistenzaException {
        RepositorySalvataggi<Carriera> repository = new RepositorySalvataggiJson(cartellaTemporanea);

        Moto moto = DatiDiProva.motoMedia();
        Pilota pilota = new Pilota("Marco", new StileTecnico(), moto);
        pilota.aggiungiDenaro(150);
        Campionato campionato = DatiDiProva.campionatoDiCinqueTappe();
        Carriera carriera = new Carriera(pilota, campionato);
        carriera.aggiungiRisultato("Prima gara: 3° su 6");

        repository.salva("prova", carriera);
        Optional<Carriera> ricaricata = repository.carica("prova");

        assertTrue(ricaricata.isPresent());
        Carriera risultato = ricaricata.get();
        assertEquals("Marco", risultato.getPilota().getNome());
        assertEquals(150, risultato.getPilota().getDenaro());
        assertEquals("Tecnico", risultato.getPilota().getStile().nome());
        assertEquals(moto.getNome(), risultato.getPilota().getMotoAttuale().getNome());
        assertEquals(1, risultato.getStoricoRisultati().size());
    }

    @Test
    void elencaSalvataggiRestituisceINomiSalvati(@TempDir Path cartellaTemporanea) throws PersistenzaException {
        RepositorySalvataggi<Carriera> repository = new RepositorySalvataggiJson(cartellaTemporanea);
        Moto moto = DatiDiProva.motoLeggera();
        Carriera carriera = new Carriera(
                new Pilota("Ada", new StileAggressivo(), moto),
                DatiDiProva.campionatoDiCinqueTappe());

        repository.salva("carriera1", carriera);
        repository.salva("carriera2", carriera);

        List<String> salvataggi = repository.elencaSalvataggi();

        assertEquals(2, salvataggi.size());
        assertTrue(salvataggi.contains("carriera1"));
        assertTrue(salvataggi.contains("carriera2"));
    }

    @Test
    void caricareUnSalvataggioInesistenteRestituisceOptionalVuoto(@TempDir Path cartellaTemporanea) throws PersistenzaException {
        RepositorySalvataggi<Carriera> repository = new RepositorySalvataggiJson(cartellaTemporanea);

        assertTrue(repository.carica("non-esiste").isEmpty());
    }

    @Test
    void unFileDanneggiatoVieneSegnalatoConUnaPersistenzaException(@TempDir Path cartellaTemporanea) throws IOException {
        RepositorySalvataggi<Carriera> repository = new RepositorySalvataggiJson(cartellaTemporanea);
        Files.writeString(cartellaTemporanea.resolve("rotto.json"), "{ questo non è JSON valido");

        assertThrows(PersistenzaException.class, () -> repository.carica("rotto"));
    }

    @Test
    void unoStileSconosciutoNelSalvataggioVieneSegnalato(@TempDir Path cartellaTemporanea) throws Exception {
        RepositorySalvataggi<Carriera> repository = new RepositorySalvataggiJson(cartellaTemporanea);
        Moto moto = DatiDiProva.motoLeggera();
        Carriera carriera = new Carriera(
                new Pilota("Ada", new StileAggressivo(), moto),
                DatiDiProva.campionatoDiCinqueTappe());
        repository.salva("manomesso", carriera);
        Path file = cartellaTemporanea.resolve("manomesso.json");
        Files.writeString(file, Files.readString(file).replace("AGGRESSIVO", "INESISTENTE"));

        assertThrows(PersistenzaException.class, () -> repository.carica("manomesso"));
    }

    @Test
    void leTappeDelCampionatoVengonoRicaricateConPistaEDifficolta(@TempDir Path cartellaTemporanea) throws PersistenzaException {
        RepositorySalvataggi<Carriera> repository = new RepositorySalvataggiJson(cartellaTemporanea);
        Campionato campionato = new Campionato("Coppa di prova", List.of(
                new Gara("Tappa sulla sabbia", new PistaSabbiosa(), 9, 14)));
        Carriera carriera = new Carriera(new Pilota("Ada", new StileAggressivo(), DatiDiProva.motoLeggera()), campionato);

        repository.salva("tappe", carriera);
        Gara ricaricata = repository.carica("tappe").orElseThrow().getCampionatoCorrente().tappaCorrente();

        assertEquals("Tappa sulla sabbia", ricaricata.getNomeTappa());
        assertEquals("SABBIOSA", ricaricata.getPista().codice());
        assertEquals(9, ricaricata.getNumeroAvversari());
        assertEquals(14, ricaricata.getDifficolta());
    }

    @Test
    void gliObiettiviSponsorVengonoRicaricatiConIProgressiFatti(@TempDir Path cartellaTemporanea) throws PersistenzaException {
        RepositorySalvataggi<Carriera> repository = new RepositorySalvataggiJson(cartellaTemporanea);
        ObiettivoSponsor cinquePodi = new ObiettivoSponsor("Sali sul podio 5 volte", new CriterioPiazzamento("PODIO", 3), 5, 400);
        cinquePodi.impostaProgresso(2);
        Carriera carriera = new Carriera(new Pilota("Ada", new StileAggressivo(), DatiDiProva.motoLeggera()),
                DatiDiProva.campionatoDiCinqueTappe(), List.of(cinquePodi));

        repository.salva("obiettivi", carriera);
        ObiettivoSponsor ricaricato = repository.carica("obiettivi").orElseThrow().getObiettivi().get(0);

        assertEquals("Sali sul podio 5 volte", ricaricato.getDescrizione());
        assertEquals("PODIO", ricaricato.getCriterio().codice());
        assertEquals(5, ricaricato.getTraguardo());
        assertEquals(400, ricaricato.getRicompensa());
        assertEquals(2, ricaricato.getProgresso());
    }

    @Test
    void laMotoAcquistataVieneRicaricataConIlSuoPrezzo(@TempDir Path cartellaTemporanea) throws PersistenzaException {
        RepositorySalvataggi<Carriera> repository = new RepositorySalvataggiJson(cartellaTemporanea);
        Moto acquistata = new Moto("Tempesta 250 Racing", CategoriaMoto.MX_250, 76, 76, 68, 400);
        Carriera carriera = new Carriera(new Pilota("Ada", new StileAggressivo(), acquistata),
                DatiDiProva.campionatoDiCinqueTappe());

        repository.salva("moto", carriera);
        Moto ricaricata = repository.carica("moto").orElseThrow().getPilota().getMotoAttuale();

        assertEquals(acquistata, ricaricata);
        assertEquals(400, ricaricata.getPrezzo());
        assertEquals(CategoriaMoto.MX_250, ricaricata.getCategoria());
    }

    /** Un file scritto dalla prima versione del gioco: senza obiettivi, senza difficoltà, senza prezzo della moto. */
    private static final String SALVATAGGIO_PRIMA_VERSIONE = """
            {
              "pilota": {
                "nome": "Veterano",
                "stileChiave": "TECNICO",
                "statistiche": { "riflessi": 57, "concentrazione": 77, "tecnicaPartenza": 72, "resistenza": 57 },
                "moto": { "nome": "Tempesta 250", "categoria": "MX_250", "potenza": 65, "maneggevolezza": 65, "bonusPartenza": 55 },
                "livello": 2,
                "esperienza": 40,
                "denaro": 350,
                "abilitaChiavi": ["Sangue Freddo"]
              },
              "campionato": {
                "nome": "Campionato Rookie",
                "tappe": [
                  { "nomeTappa": "GP di Maggiora", "pistaChiave": "ASCIUTTA", "numeroAvversari": 5 },
                  { "nomeTappa": "GP di Loket", "pistaChiave": "FANGOSA", "numeroAvversari": 6 }
                ],
                "tappaCorrente": 1,
                "puntiGiocatore": 25
              },
              "storicoRisultati": ["GP di Maggiora: 1° su 6"]
            }
            """;

    @Test
    void unSalvataggioDellaPrimaVersioneSiCaricaAncora(@TempDir Path cartellaTemporanea) throws Exception {
        RepositorySalvataggi<Carriera> repository = new RepositorySalvataggiJson(cartellaTemporanea);
        Files.writeString(cartellaTemporanea.resolve("vecchio.json"), SALVATAGGIO_PRIMA_VERSIONE);

        Carriera carriera = repository.carica("vecchio").orElseThrow();

        assertEquals("Veterano", carriera.getPilota().getNome());
        assertEquals(2, carriera.getPilota().getLivello());
        assertEquals(1, carriera.getPilota().getAbilitaSbloccate().size());
        assertTrue(carriera.getPilota().getMotoAttuale().isDiSerie());
        assertEquals("GP di Loket", carriera.getCampionatoCorrente().tappaCorrente().getNomeTappa());
        assertEquals(0, carriera.getCampionatoCorrente().tappaCorrente().getDifficolta());
        assertTrue(carriera.getObiettivi().isEmpty());
    }
}
