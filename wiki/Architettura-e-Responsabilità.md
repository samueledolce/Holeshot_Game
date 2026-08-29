# Architettura e responsabilità delle classi

Il codice è organizzato in quattro package sotto
`it.unicam.cs.mpgc.rpg129875`, ciascuno con una responsabilità precisa:

| Package | Responsabilità |
|---|---|
| `model` | Le entità del dominio (dati + comportamenti minimi legati ai dati) e i contratti (interfacce) che descrivono concetti che possono avere più implementazioni. |
| `engine` | La logica di gioco: progressione, generazione avversari, calcolo del piazzamento, risoluzione della partenza. Non sa nulla di grafica. |
| `persistence` | Salvataggio/caricamento, isolato dietro un'interfaccia generica. |
| `gui` | L'interfaccia grafica JavaFX. Dipende da `engine`/`model`/`persistence`, mai il contrario. |

Questa separazione è la prima applicazione del **Dependency Inversion
Principle**: `engine` e `model` non conoscono JavaFX né Gson; è la `gui` a
dipendere da loro, e `persistence` è usata attraverso un'interfaccia.

## Package `model`

| Classe / Interfaccia | Responsabilità |
|---|---|
| `Pilota` | Contenere e proteggere lo stato di un pilota (nome, statistiche, livello, moto, abilità, denaro). Non decide *come* si progredisce, solo *cosa* viene memorizzato. |
| `Statistiche` | I quattro valori numerici (riflessi, concentrazione, tecnica di partenza, resistenza) e la loro crescita, con i limiti (1–100). |
| `StileDiGuida` (interfaccia) + `StileAggressivo` / `StileTecnico` / `StileEquilibrato` | Definiscono le statistiche iniziali di un pilota. Aggiungere un nuovo stile = una nuova classe, senza toccare `Pilota` (Open/Closed). |
| `Moto`, `CategoriaMoto`, `CatalogoMoto` | La moto usata dal pilota e il catalogo di quelle disponibili a inizio carriera. |
| `Abilita` (interfaccia) + `SangueFreddo` / `SestoSenso` / `PartenzaFulminea` / `RiflessiDaCampione` | Un'abilità sbloccabile che corregge il tempo di reazione misurato. Ogni abilità è una classe indipendente (Open/Closed, Dependency Inversion: chi usa un'abilità dipende solo dall'interfaccia). |
| `CatalogoAbilita` | Sa quale abilità si sblocca a quale livello (una `Map<Integer, Abilita>`). |
| `CondizionePista` (interfaccia) + `PistaAsciutta` / `PistaFangosa` / `PistaSabbiosa` | Le soglie (in millisecondi) che classificano una partenza in base al tipo di terreno. Una nuova condizione di pista = una nuova classe. |
| `QualitaPartenza`, `RisultatoPartenza` | La qualità di una partenza e l'esito completo (tempo + qualità). |
| `Avversario` | Un avversario "leggero": solo nome e prestazione complessiva, sufficiente per calcolare un piazzamento. |
| `Gara`, `Campionato`, `CatalogoCampionati` | Una singola gara (pista + numero avversari) e la sequenza di gare che compone un campionato, con punteggio e avanzamento. Non sanno *come* si disputa una gara. |
| `RisultatoGara` | La classifica finale e la posizione del giocatore. |
| `Carriera` | L'aggregato radice: pilota + campionato corrente + storico risultati. È l'unico oggetto salvato/caricato. |

## Package `engine`

| Classe / Interfaccia | Responsabilità |
|---|---|
| `GestoreProgressione` | Regole di progressione: quanta esperienza serve per salire di livello, quanto crescono le statistiche, quali abilità si sbloccano. Separata da `Pilota` per rispettare il Single Responsibility Principle: `Pilota` cambia per motivi legati al pilota, `GestoreProgressione` cambia se cambiano le regole di gioco. |
| `GeneratoreAvversari` (interfaccia) + `GeneratoreAvversariCasuale` | Genera gli avversari di una gara. Interfaccia pensata per poter cambiare strategia di generazione in futuro. |
| `CalcolatorePiazzamento` (interfaccia) + `CalcolatorePiazzamentoStandard` | Calcola il piazzamento finale combinando partenza, statistiche e avversari. Isolare questa logica dietro un'interfaccia permette di sostituirla con una simulazione più realistica senza toccare il motore di gara. |
| `RisolutorePartenza` | Trasforma il tempo di reazione misurato dalla UI in un `RisultatoPartenza`, applicando abilità e soglie di pista. |
| `MotoreDiGara` | Orchestra una gara: genera avversari, calcola il piazzamento, assegna ricompense, aggiorna campionato e storico. Dipende solo dalle interfacce sopra elencate, mai dalle implementazioni concrete. |
| `EsitoGara` | Valore che riassume risultato di gara + ricompense, restituito da `MotoreDiGara` alla UI. |

## Package `persistence`

| Classe / Interfaccia | Responsabilità |
|---|---|
| `RepositorySalvataggi<T>` (interfaccia) | Contratto generico di persistenza: salva, carica, elenca, elimina. Il resto dell'applicazione dipende solo da questa interfaccia. |
| `RepositorySalvataggiJson` | Implementazione su file JSON (tramite Gson) dell'interfaccia sopra, specializzata per `Carriera`. |
| `PilotaDTO`, `GaraDTO`, `CampionatoDTO`, `CarrieraDTO` | Rappresentazioni "piatte" (solo tipi semplici) del modello, usate solo per la serializzazione. Vedi [Persistenza](Persistenza) per il perché di questa scelta. |
| `ConvertitoreCarriera` | Converte tra modello di dominio e DTO. È l'unico punto del progetto che deve conoscere tutte le implementazioni concrete di `StileDiGuida`, `Abilita` e `CondizionePista`. |
| `PersistenzaException` | Eccezione controllata per segnalare un errore di persistenza, senza esporre dettagli tecnici (es. `IOException`) al resto dell'applicazione. |

## Package `gui`

| Classe | Responsabilità |
|---|---|
| `HoleshotApplication` | Punto di ingresso JavaFX. Tiene l'unica finestra dell'applicazione e implementa `Navigatore`: cambia il contenuto della finestra a seconda della schermata richiesta, ed espone i servizi (motore di gara, persistenza) alle schermate. |
| `Navigatore` (interfaccia) | Contratto di navigazione tra schermate. Le singole schermate dipendono solo da questa interfaccia, non da `HoleshotApplication`: potrebbero essere riusate con un altro coordinatore. |
| `SchermataMenu`, `SchermataCreazionePilota`, `SchermataCarriera`, `SchermataGara`, `SchermataPartenza`, `SchermataRisultato` | Ognuna costruisce l'interfaccia di una schermata e collega gli eventi dell'utente al `Navigatore` e al modello. Nessuna contiene logica di gioco: quella vive in `engine`. |
| `Main` | Punto di ingresso dell'applicazione, separato da `HoleshotApplication` per lasciare aperta la porta a un front-end diverso in futuro. |

## Come i principi SOLID si vedono nel codice

- **Single Responsibility**: `Pilota` contiene stato, `GestoreProgressione`
  contiene le regole di progressione, `MotoreDiGara` orchestra la gara,
  `CalcolatorePiazzamento` calcola solo il piazzamento. Ogni classe ha un
  solo motivo per cambiare.
- **Open/Closed**: nuovi stili di guida, abilità o condizioni di pista si
  aggiungono con nuove classi, senza modificare il codice che le usa
  (vedi [Estendibilità](Estendibilità)).
- **Liskov Substitution**: ogni implementazione di `CondizionePista`,
  `Abilita`, `StileDiGuida` rispetta il contratto della propria interfaccia
  (es. `classifica(...)` restituisce sempre una `QualitaPartenza` valida
  per qualunque tempo di reazione, `Abilita.applica(...)` restituisce
  sempre un tempo `>= 0`), quindi sono intercambiabili senza sorprese per
  chi le usa.
- **Interface Segregation**: le interfacce sono piccole e mirate a un solo
  compito (`GeneratoreAvversari` genera avversari, `CalcolatorePiazzamento`
  calcola un piazzamento, `RepositorySalvataggi` gestisce solo
  salva/carica/elenca/elimina), invece di un'unica interfaccia che faccia
  tutto.
- **Dependency Inversion**: `MotoreDiGara` dipende da
  `GeneratoreAvversari`, `CalcolatorePiazzamento` e `GestoreProgressione`
  (astrazioni); `HoleshotApplication` dipende da `RepositorySalvataggi<Carriera>`
  (astrazione), non da `RepositorySalvataggiJson` direttamente nel resto
  del codice.
