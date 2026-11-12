# Organizzazione dei dati e persistenza

Il gioco legge e scrive due tipi di file, entrambi in formato JSON:

- i **salvataggi** delle carriere, creati dal giocatore nella cartella
  `saves/`;
- i **contenuti di gioco** (moto, campionati, obiettivi), che fanno parte
  dell'applicazione e stanno tra le sue risorse.

Il formato JSON è stato scelto perché leggibile e ispezionabile a occhio,
comodo in fase di sviluppo e debug, e facilmente estendibile con nuovi
campi.

## I salvataggi

L'unico aggregato persistito è `Carriera`, che contiene tutto ciò che
serve a ricostruire lo stato del giocatore:

- il `Pilota` (nome, stile di guida, statistiche, moto attuale, livello,
  esperienza, crediti, abilità sbloccate);
- il `Campionato` corrente (nome, elenco tappe con pista, avversari e
  difficoltà, indice della tappa raggiunta, punti);
- gli obiettivi sponsor con i progressi fatti;
- lo storico testuale dei risultati ottenuti.

Ogni salvataggio è un file `<nome>.json` dentro la cartella `saves/`
(creata automaticamente accanto all'eseguibile).

## Il problema delle interfacce e la soluzione scelta

Il modello di dominio usa diverse interfacce (`StileDiGuida`, `Abilita`,
`CondizionePista`, `CriterioObiettivo`) proprio per rispettare Open/Closed
e Dependency Inversion. Questo però crea un problema per la
serializzazione: una libreria come Gson, quando trova un campo di tipo
interfaccia, non ha modo di sapere *quale implementazione concreta*
ricreare in fase di lettura, e si ferma con un errore.

La soluzione adottata ha due parti.

**Un livello di DTO (Data Transfer Object)** dedicato alla persistenza,
separato dal modello di dominio: `PilotaDTO`, `GaraDTO`, `CampionatoDTO`,
`ObiettivoDTO`, `CarrieraDTO` contengono solo tipi semplici (stringhe,
numeri, altri DTO, e le classi concrete `Moto` e `Statistiche`, che non
hanno campi-interfaccia e quindi Gson può gestire direttamente).

**Un codice al posto dell'oggetto.** Dove il modello ha un oggetto il cui
tipo è un'interfaccia, il DTO ha una stringa: il suo codice (es.
`"AGGRESSIVO"`, `"FANGOSA"`, `"PODIO"`). Ogni implementazione dichiara il
proprio codice (interfaccia `Identificabile`) e `ConvertitoreCarriera` lo
ritraduce nell'oggetto interrogando un `Registro`.

Il convertitore quindi **non conosce nessuna classe concreta**: scrive
`stile.codice()` e legge `registroStili.perCodice(...)`. Aggiungere un
nuovo stile di guida, una condizione di pista o un criterio richiede solo
di registrarlo in `Registri`; salvataggio e caricamento funzionano senza
altre modifiche. In una prima versione del progetto il convertitore
riconosceva le implementazioni con una catena di `instanceof`, e andava
aggiornato a ogni nuova classe: il registro ha eliminato quel vincolo.

La conversione è inoltre collaudabile con semplici oggetti Java, senza
passare da Gson.

## I contenuti di gioco

Moto, campionati e obiettivi sponsor non sono scritti nel codice Java ma
in tre file tra le risorse dell'applicazione
(`src/main/resources/it/unicam/cs/mpgc/rpg129875/dati/`):

| File | Contenuto |
|---|---|
| `moto.json` | Le moto: nome, categoria, caratteristiche, prezzo. Quelle con prezzo 0 sono le moto di serie tra cui scegliere a inizio carriera. |
| `campionati.json` | I campionati, nell'ordine in cui si affrontano, ciascuno con le sue tappe: nome, codice della condizione di pista, numero di avversari, difficoltà. |
| `obiettivi.json` | Gli obiettivi sponsor: descrizione, codice del criterio, traguardo, ricompensa. |

Un esempio da `campionati.json`:

```json
{ "nomeTappa": "GP di Loket", "pistaChiave": "FANGOSA", "numeroAvversari": 6, "difficolta": 0 }
```

Campionati e obiettivi usano gli stessi DTO dei salvataggi: un solo
formato da conoscere e una sola conversione da mantenere.

`CatalogoJson` legge i file una sola volta, all'avvio, e li offre al resto
dell'applicazione attraverso le interfacce `CatalogoMoto`,
`CatalogoCampionati` e `CatalogoObiettivi`, definite nel modello. Chi usa i
contenuti (`Negozio`, `GestoreCarriera`) non sa che arrivano da un file.

### Controllo dei contenuti

Un file di contenuti sbagliato è un errore di chi sviluppa, non del
giocatore, e va scoperto subito. Per questo `ConvertitoreCatalogo`
controlla tutto all'avvio (nomi mancanti o duplicati, categorie o codici
sconosciuti, prezzi negativi, campionati senza tappe, assenza di una moto
di serie...) e in caso di problemi lancia una `CatalogoNonValidoException`
con un messaggio che indica cosa correggere. È un'eccezione non
controllata, perché l'applicazione non può proseguire in modo sensato
senza i suoi contenuti. Il test `CatalogoJsonTest` carica i file veri:
un errore introdotto modificandoli fa fallire `./gradlew build`.

## Errori e compatibilità dei salvataggi

- Un errore di lettura/scrittura, un file danneggiato o un codice
  sconosciuto diventano una `PersistenzaException` (controllata): la UI la
  mostra al giocatore e l'applicazione continua a funzionare.
- Un campo assente nel file vale come zero o come elenco vuoto. Così i
  salvataggi creati con la prima versione del gioco, che non avevano
  obiettivi, difficoltà e prezzo della moto, si caricano ancora (c'è un
  test dedicato).

## Perché dietro un'interfaccia

`RepositorySalvataggiJson` implementa l'interfaccia generica
`RepositorySalvataggi<T>`. Il resto dell'applicazione (motore di gioco,
interfaccia grafica) dipende solo da questa interfaccia, mai
dall'implementazione concreta. Questo significa che:

- sostituire i file JSON con un database (es. SQLite) o con una chiamata
  a un servizio remoto (per una futura versione mobile/web, come previsto
  dalla specifica di progetto) richiede solo una nuova implementazione di
  `RepositorySalvataggi<Carriera>`; lo stesso vale per i contenuti di
  gioco e le interfacce catalogo;
- i test sulla logica di gioco non hanno bisogno di toccare il file
  system;
- la UI (`HoleshotApplication`) esegue le operazioni di
  salvataggio/caricamento su un thread dedicato (un `ExecutorService`),
  così l'accesso al disco non blocca mai il thread grafico di JavaFX.
