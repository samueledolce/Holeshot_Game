package it.unicam.cs.mpgc.rpg129875.persistence;

import it.unicam.cs.mpgc.rpg129875.DatiDiProva;
import it.unicam.cs.mpgc.rpg129875.model.Campionato;
import it.unicam.cs.mpgc.rpg129875.model.Carriera;
import it.unicam.cs.mpgc.rpg129875.model.Moto;
import it.unicam.cs.mpgc.rpg129875.model.Pilota;
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
}
