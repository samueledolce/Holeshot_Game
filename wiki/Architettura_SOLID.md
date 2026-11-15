# Architettura e responsabilità delle classi

Il codice è organizzato in quattro package sotto
`it.unicam.cs.mpgc.rpg129875`, ciascuno con una responsabilità precisa:

| Package | Responsabilità |
|---|---|
| `model` | Le entità del dominio (dati + comportamenti minimi legati ai dati) e i contratti (interfacce) che descrivono concetti che possono avere più implementazioni. |
| `engine` | La logica di gioco: progressione, generazione avversari, calcolo del piazzamento, risoluzione della partenza, negozio, obiettivi, percorso di carriera. Non sa nulla di grafica. |
| `persistence` | Lettura e scrittura dei file JSON: salvataggi e contenuti di gioco. È l'unico package che conosce Gson. |
| `gui` | L'interfaccia grafica JavaFX. Dipende da `engine`/`model`/`persistence`, mai il contrario. |

Questa separazione è la prima applicazione del **Dependency Inversion
Principle**: `engine` e `model` non conoscono JavaFX né Gson; è la `gui` a
dipendere da loro, e `persistence` è usata attraverso interfacce definite
nel modello.

Accanto al codice ci sono le **risorse** dell'applicazione
(`src/main/resources`): la cartella `dati/` con i contenuti di gioco in
JSON e la cartella `grafica/` con il foglio di stile e gli sfondi.

## Una regola che attraversa il progetto: comportamenti nelle classi, dati nei file

Gli elementi del gioco sono di due tipi, trattati in modo diverso.

- Quelli che hanno un **comportamento** proprio (uno stile di guida, una
  condizione di pista, un'abilità, un criterio per gli obiettivi) sono
  **classi** che implementano un'interfaccia. Hanno un codice testuale
  (`Identificabile`) e sono elencati in un unico punto (`Registri`).
- Quelli che sono solo **dati** (una moto, una gara, un campionato, un
  obiettivo sponsor) stanno in **file JSON** e arrivano al gioco attraverso
  interfacce "catalogo".

I file JSON indicano i comportamenti tramite il loro codice (es.
`"pistaChiave": "FANGOSA"`): è un `Registro` a tradurre il codice
nell'oggetto corrispondente.

## Package `model`

| Classe / Interfaccia | Responsabilità |
|---|---|
| `Pilota` | Contenere e proteggere lo stato di un pilota (nome, statistiche, livello, moto, abilità, crediti). Non decide *come* si progredisce, solo *cosa* viene memorizzato. |
| `Statistiche` | I quattro valori numerici (riflessi, concentrazione, tecnica di partenza, resistenza) e la loro crescita, con i limiti (1–100). |
| `Identificabile` (interfaccia) | Un elemento riconoscibile da un codice testuale stabile, quello che viene scritto nei file JSON. |
| `Registro<T>` | Raccolta generica di elementi `Identificabile`: dato un codice restituisce l'elemento. La stessa classe serve per stili, condizioni di pista e criteri. |
| `Registri` | L'elenco, in un unico punto, delle implementazioni disponibili di `StileDiGuida`, `CondizionePista` e `CriterioObiettivo`. |
| `StileDiGuida` (interfaccia) + `StileAggressivo` / `StileTecnico` / `StileEquilibrato` | Definiscono le statistiche iniziali di un pilota. Aggiungere un nuovo stile = una nuova classe, senza toccare `Pilota` (Open/Closed). |
| `Moto`, `CategoriaMoto` | La moto usata dal pilota, con caratteristiche e prezzo. Due moto con lo stesso nome sono la stessa moto (`equals`/`hashCode`). |
| `CatalogoMoto` (interfaccia) | L'elenco delle moto esistenti, da qualunque fonte provengano. |
| `Abilita` (interfaccia) + `SangueFreddo` / `SestoSenso` / `PartenzaFulminea` / `RiflessiDaCampione` | Un'abilità sbloccabile che corregge il tempo di reazione misurato. Ogni abilità è una classe indipendente. |
| `CatalogoAbilita` | Sa quale abilità si sblocca a quale livello (una `Map<Integer, Abilita>`). |
| `CondizionePista` (interfaccia) + `PistaAsciutta` / `PistaFangosa` / `PistaSabbiosa` | Le soglie (in millisecondi) che classificano una partenza in base al tipo di terreno. Una nuova condizione di pista = una nuova classe. |
| `QualitaPartenza`, `RisultatoPartenza` | La qualità di una partenza e l'esito completo (tempo + qualità). |
| `Avversario` | Un avversario "leggero": solo nome e prestazione complessiva, sufficiente per calcolare un piazzamento. |
| `Gara`, `Campionato` | Una singola gara (pista, numero di avversari, difficoltà) e la sequenza di gare che compone un campionato, con punteggio e avanzamento. Non sanno *come* si disputa una gara. |
| `CatalogoCampionati` (interfaccia) | La sequenza dei campionati di una carriera: il primo e, dato un campionato, il successivo. |
| `RisultatoGara` | La classifica finale e la posizione del giocatore. |
| `CriterioObiettivo` (interfaccia) + `CriterioPiazzamento` / `CriterioQualitaPartenza` | Decide se una gara conta per un obiettivo (es. "entro la terza posizione", "partenza almeno ottima"). |
| `ObiettivoSponsor` | Tiene il conto dei progressi verso un traguardo. Non decide quali gare contano (lo fa il criterio) e non paga la ricompensa. |
| `CatalogoObiettivi` (interfaccia) | Gli obiettivi che lo sponsor propone a ogni nuova carriera. |
| `Carriera` | L'aggregato radice: pilota + campionato corrente + obiettivi sponsor + storico risultati. È l'unico oggetto salvato/caricato. |

## Package `engine`

| Classe / Interfaccia | Responsabilità |
|---|---|
| `GestoreProgressione` | Regole di progressione: quanta esperienza serve per salire di livello, quanto crescono le statistiche, quali abilità si sbloccano. Separata da `Pilota` per rispettare il Single Responsibility Principle: `Pilota` cambia per motivi legati al pilota, `GestoreProgressione` cambia se cambiano le regole di gioco. |
| `GeneratoreAvversari` (interfaccia) + `GeneratoreAvversariCasuale` | Genera gli avversari di una gara, più o meno forti secondo la difficoltà. |
| `CalcolatorePiazzamento` (interfaccia) + `CalcolatorePiazzamentoStandard` | Calcola il piazzamento finale combinando partenza, statistiche e avversari. Isolare questa logica dietro un'interfaccia permette di sostituirla con una simulazione più realistica senza toccare il motore di gara. |
| `RisolutorePartenza` | Trasforma il tempo di reazione misurato dalla UI in un `RisultatoPartenza`, applicando abilità e soglie di pista. |
| `GestoreObiettivi` | Dopo ogni gara aggiorna gli obiettivi sponsor e paga le ricompense di quelli appena completati. |
| `MotoreDiGara` | Orchestra una gara: genera avversari, calcola il piazzamento, assegna ricompense, aggiorna campionato, obiettivi e storico. Non contiene nessuna di queste regole: le delega ai collaboratori ricevuti nel costruttore. |
| `EsitoGara` | Valore che riassume risultato di gara, ricompense e obiettivi completati, restituito da `MotoreDiGara` alla UI. |
| `GestoreCarriera` | Regole del percorso di carriera: come nasce una carriera e quando si può passare al campionato successivo. |
| `Negozio` | Regole di compravendita delle moto: cosa è in vendita, quanto vale la permuta, quando un acquisto riesce. |
| `OffertaMoto`, `EsitoAcquisto` | Una moto in vendita vista da un pilota (costo e se può permettersela) e i possibili esiti di un acquisto. |

## Package `persistence`

| Classe / Interfaccia | Responsabilità |
|---|---|
| `RepositorySalvataggi<T>` (interfaccia) | Contratto generico di persistenza: salva, carica, elenca, elimina. Il resto dell'applicazione dipende solo da questa interfaccia. |
| `RepositorySalvataggiJson` | Implementazione su file JSON (tramite Gson) dell'interfaccia sopra, specializzata per `Carriera`. |
| `CatalogoJson` | Legge dai file JSON i contenuti di gioco e li offre attraverso le interfacce `CatalogoMoto`, `CatalogoCampionati` e `CatalogoObiettivi`. |
| `PilotaDTO`, `GaraDTO`, `CampionatoDTO`, `ObiettivoDTO`, `CarrieraDTO`, `MotoDTO` | Rappresentazioni "piatte" (solo tipi semplici) del modello, usate solo per leggere e scrivere JSON. Vedi [Persistenza](Persistenza) per il perché di questa scelta. |
| `ConvertitoreCarriera` | Converte tra modello di dominio e DTO dei salvataggi, traducendo ogni codice nell'oggetto corrispondente tramite i registri. |
| `ConvertitoreCatalogo` | Converte i DTO dei contenuti di gioco in oggetti del modello, controllando che i dati abbiano senso. |
| `PersistenzaException` | Eccezione controllata per un salvataggio/caricamento non riuscito, senza esporre dettagli tecnici (es. `IOException`) al resto dell'applicazione. |
| `CatalogoNonValidoException` | Eccezione non controllata per contenuti di gioco mancanti o sbagliati: ferma l'applicazione all'avvio con un messaggio chiaro. |

## Package `gui`

| Classe | Responsabilità |
|---|---|
| `HoleshotApplication` | Punto di ingresso JavaFX. Crea i servizi e li collega tra loro (è l'unico punto in cui si scelgono le implementazioni concrete), tiene l'unica finestra e implementa `Navigatore`. |
| `Navigatore` (interfaccia) | Contratto di navigazione tra schermate. Le singole schermate dipendono solo da questa interfaccia, non da `HoleshotApplication`: potrebbero essere riusate con un altro coordinatore. |
| `Componenti` | Costruisce i componenti grafici ricorrenti (sfondo, pannello, titoli, pulsanti, barre) assegnando le classi di stile. Evita di ripetere lo stesso codice in ogni schermata. |
| `SchermataMenu`, `SchermataCreazionePilota`, `SchermataCarriera`, `SchermataNegozio`, `SchermataGara`, `SchermataPartenza`, `SchermataRisultato` | Ognuna costruisce l'interfaccia di una schermata e collega gli eventi dell'utente al `Navigatore` e ai servizi. Nessuna contiene regole di gioco: quelle vivono in `engine`. |
| `Main` | Punto di ingresso dell'applicazione, separato da `HoleshotApplication` per lasciare aperta la porta a un front-end diverso in futuro. |

L'aspetto (colori, caratteri, sfondi) non è nel codice Java ma nel foglio
di stile `grafica/holeshot.css`: le schermate decidono *cosa* mostrare, il
foglio di stile *come* appare.

## Come i principi SOLID si vedono nel codice

- **Single Responsibility**: `Pilota` contiene stato, `GestoreProgressione`
  contiene le regole di progressione, `MotoreDiGara` orchestra la gara,
  `CalcolatorePiazzamento` calcola solo il piazzamento, `Negozio` contiene
  solo le regole di compravendita, `GestoreObiettivi` solo quelle degli
  obiettivi. Nella persistenza chi parla con Gson (`RepositorySalvataggiJson`,
  `CatalogoJson`) è separato da chi converte e controlla i dati
  (`ConvertitoreCarriera`, `ConvertitoreCatalogo`). Ogni classe ha un solo
  motivo per cambiare.
- **Open/Closed**: nuovi stili di guida, abilità, condizioni di pista o
  criteri per gli obiettivi si aggiungono con nuove classi; nuove moto,
  gare, campionati e obiettivi aggiungendo righe ai file JSON. In entrambi
  i casi il codice che li usa non viene modificato (vedi
  [Estendibilità](Estendibilità)).
- **Liskov Substitution**: ogni implementazione di `CondizionePista`,
  `Abilita`, `StileDiGuida`, `CriterioObiettivo` rispetta il contratto
  della propria interfaccia (es. `classifica(...)` restituisce sempre una
  `QualitaPartenza` valida per qualunque tempo di reazione,
  `Abilita.applica(...)` restituisce sempre un tempo `>= 0`), quindi sono
  intercambiabili senza sorprese per chi le usa. Per questo nel codice non
  serve mai chiedersi di che classe concreta sia un oggetto: non c'è nessun
  `instanceof` sulle implementazioni.
- **Interface Segregation**: le interfacce sono piccole e mirate a un solo
  compito. I contenuti di gioco, ad esempio, sono offerti da tre interfacce
  distinte (`CatalogoMoto`, `CatalogoCampionati`, `CatalogoObiettivi`):
  `Negozio` dipende solo dalla prima, `GestoreCarriera` dalle altre due,
  anche se oggi è una sola classe (`CatalogoJson`) a implementarle tutte.
- **Dependency Inversion**: `MotoreDiGara` dipende da `GeneratoreAvversari`
  e `CalcolatorePiazzamento`; `Negozio` e `GestoreCarriera` dipendono dalle
  interfacce catalogo, definite nel modello e implementate nella
  persistenza; `HoleshotApplication` usa `RepositorySalvataggi<Carriera>`.
  Le implementazioni concrete vengono scelte in un solo punto
  (`HoleshotApplication.init()`) e passate agli altri oggetti tramite il
  costruttore.

## Test

La logica di gioco è coperta da una novantina di test JUnit (`src/test`) su modello,
motore di gioco e persistenza. I test della logica non dipendono dai file
dei contenuti (usano gli oggetti fissi di `DatiDiProva`), mentre
`CatalogoJsonTest` collauda proprio i file veri, così un errore introdotto
modificando un JSON viene scoperto da `./gradlew build`. I generatori di
numeri casuali sono passati dall'esterno, quindi i test possono fissarne il
seme e ottenere sempre lo stesso risultato.
