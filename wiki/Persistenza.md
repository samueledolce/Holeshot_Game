# Organizzazione dei dati e persistenza

## Cosa viene salvato

L'unico aggregato persistito è `Carriera`, che contiene tutto ciò che
serve a ricostruire lo stato del giocatore:

- il `Pilota` (nome, stile di guida, statistiche, moto attuale, livello,
  esperienza, denaro, abilità sbloccate);
- il `Campionato` corrente (nome, elenco tappe, indice della tappa
  raggiunta, punti);
- lo storico testuale dei risultati ottenuti.

Ogni salvataggio è un file `<nome>.json` dentro la cartella `saves/`
(creata automaticamente accanto all'eseguibile). Il formato JSON è stato
scelto perché leggibile e ispezionabile a occhio, comodo in fase di
sviluppo e debug, e facilmente estendibile con nuovi campi in futuro.

## Il problema delle interfacce e la soluzione scelta

Il modello di dominio usa diverse interfacce (`StileDiGuida`, `Abilita`,
`CondizionePista`) proprio per rispettare Open/Closed e Dependency
Inversion. Questo però crea un problema per la serializzazione: una
libreria come Gson, quando trova un campo di tipo interfaccia, non ha modo
di sapere *quale implementazione concreta* ricreare in fase di
deserializzazione (a meno di scrivere `TypeAdapter` personalizzati per
ognuna, soluzione più fragile e più difficile da testare in isolamento).

La soluzione adottata è un **livello di DTO (Data Transfer Object)**
dedicato alla persistenza, separato dal modello di dominio:

- `PilotaDTO`, `GaraDTO`, `CampionatoDTO`, `CarrieraDTO` contengono solo
  tipi semplici (stringhe, numeri, altri DTO, e le classi concrete `Moto`
  e `Statistiche`, che non hanno campi-interfaccia e quindi Gson può
  gestire direttamente).
- Al posto delle interfacce, i DTO usano una **chiave testuale** (es.
  `"AGGRESSIVO"`, `"FANGOSA"`) per identificare quale implementazione
  concreta ricostruire.
- La classe `ConvertitoreCarriera` (package-private, usata solo da
  `RepositorySalvataggiJson`) converte in entrambe le direzioni tra
  modello di dominio e DTO, ed è l'unico punto del progetto che deve
  conoscere tutte le implementazioni concrete di quelle interfacce.

Questa scelta ha un costo esplicito, dichiarato qui per trasparenza:
aggiungere un nuovo `StileDiGuida`, `Abilita` o `CondizionePista` richiede
anche di aggiornare le mappature "chiave ↔ classe" in
`ConvertitoreCarriera`. È l'unico punto che deve essere toccato: il resto
del motore di gioco e la UI restano invariati. In compenso, la conversione
è testabile con semplici oggetti Java, senza bisogno di eseguire
davvero la libreria di serializzazione durante i test (vedi
`ConvertitoreCarriera` e il relativo test manuale eseguito durante lo
sviluppo).

## Perché dietro un'interfaccia

`RepositorySalvataggiJson` implementa l'interfaccia generica
`RepositorySalvataggi<T>`. Il resto dell'applicazione (motore di gioco,
interfaccia grafica) dipende solo da questa interfaccia, mai
dall'implementazione concreta. Questo significa che:

- sostituire i file JSON con un database (es. SQLite) o con una chiamata
  a un servizio remoto (per una futura versione mobile/web, come previsto
  dalla specifica di progetto) richiede solo una nuova implementazione di
  `RepositorySalvataggi<Carriera>`;
- i test sulla logica di gioco non hanno bisogno di toccare il file
  system;
- la UI (`HoleshotApplication`) esegue le operazioni di
  salvataggio/caricamento su un thread dedicato (un `ExecutorService`),
  così l'accesso al disco non blocca mai il thread grafico di JavaFX.
