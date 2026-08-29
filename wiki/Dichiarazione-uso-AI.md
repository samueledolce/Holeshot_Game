# Dichiarazione dettagliata di uso di strumenti di AI

Come richiesto dalla specifica di progetto, questa pagina descrive nel
dettaglio quali strumenti di intelligenza artificiale sono stati usati
nella realizzazione di questo progetto, per quali attività e con quale
livello di intervento personale.

## Strumento usato

**Claude** (Anthropic), nell'ambiente Cowork/Claude Code, usato in modalità
conversazionale e agentica (con accesso a un ambiente di sviluppo cloud:
file system, shell, compilatore Java).

## Per quali attività è stato usato

1. **Progettazione del concept del gioco**: lo studente ha proposto l'idea
   di un GdR ambientato nel motocross, incentrato sulla partenza al
   cancelletto (elemento tecnico reale e importante nella disciplina).
   Claude ha proposto una struttura di gioco concreta (statistiche, stili
   di guida, moto, abilità, campionato, minigioco di reazione) sulla base
   di questa idea, e ha discusso con lo studente alcune scelte di design
   (come risolvere la gara dopo la partenza, quanto ricco rendere il
   sistema di progressione, come implementare il minigioco, come gestire
   gli avversari) tramite domande esplicite a scelta multipla, prima di
   iniziare a scrivere codice.
2. **Progettazione dell'architettura software**: individuazione dei
   package, delle classi, delle interfacce e delle rispettive
   responsabilità, con l'obiettivo esplicito di rispettare i principi
   SOLID e la pulizia del codice richiesti dalla specifica d'esame.
3. **Generazione del codice sorgente**: l'intero codice sorgente Java
   (modello di dominio, motore di gioco, persistenza JSON, interfaccia
   grafica JavaFX) è stato scritto da Claude, seguendo l'architettura
   concordata.
4. **Generazione e verifica dei test**: sono stati generati sia i test
   JUnit inclusi nel progetto (`src/test`), sia degli **smoke test**
   temporanei (non parte del progetto consegnato) usati durante lo
   sviluppo per eseguire davvero la logica di dominio e del motore di
   gioco con `javac`/`java` puro, senza dipendere da librerie esterne non
   scaricabili nell'ambiente di sviluppo usato da Claude. Questo ha
   permesso di individuare e correggere errori di calcolo reali prima
   della consegna (es. un'aspettativa sbagliata sulla soglia di
   esperienza necessaria per salire di livello, corretta dopo aver
   osservato il comportamento effettivo del codice).
5. **Scrittura della documentazione**: README e questa Wiki sono stati
   scritti da Claude sulla base dell'architettura e delle scelte di design
   discusse con lo studente.

## Limite noto e come è stato gestito

L'ambiente cloud in cui Claude ha sviluppato il progetto ha accesso di
rete limitato a poche destinazioni (repository npm/PyPI e simili) e
**non ha potuto scaricare le dipendenze Java del progetto** (JavaFX, Gson,
JUnit, né il plugin Gradle per JavaFX) da Maven Central o dal Gradle
Plugin Portal. Di conseguenza:

- il modello di dominio e il motore di gioco (package `model` ed
  `engine`, che non hanno dipendenze esterne) sono stati compilati ed
  eseguiti realmente con `javac`/`java` durante lo sviluppo, tramite
  smoke test scritti appositamente; tutti i controlli sono stati
  verificati superati prima della consegna;
- la logica di conversione per la persistenza (`ConvertitoreCarriera`,
  che gestisce la parte più delicata del salvataggio) è stata anch'essa
  compilata ed eseguita realmente, isolando le chiamate a Gson (unica
  parte con dipendenza esterna in quel package) e verificando a parte
  che il suo utilizzo fosse standard e corretto;
- il codice che dipende da librerie esterne (in particolare l'interfaccia
  grafica JavaFX e le chiamate dirette a Gson) **non ha potuto essere
  compilato né eseguito nell'ambiente di sviluppo usato da Claude**, ed è
  stato quindi scritto con particolare attenzione e riletto più volte,
  ma va comunque compilato e collaudato con `./gradlew build` /
  `./gradlew run` su una macchina con normale accesso a Internet (come
  richiesto dalla consegna del progetto) prima di considerarlo definitivo.

Questo limite viene dichiarato qui in modo trasparente, come richiesto
dalla specifica, invece di essere nascosto o presentato come già
completamente verificato.

## Cosa NON è stato delegato all'AI

- La scelta del tema del progetto (il GdR motocross, con la partenza al
  cancelletto come elemento centrale) è partita dallo studente.
- Le scelte di design proposte da Claude a più opzioni (tecnologia per la
  GUI, profondità del sistema di progressione, modalità di risoluzione
  della gara, gestione degli avversari) sono state selezionate
  esplicitamente dallo studente, non decise autonomamente dall'AI.
- Il numero di matricola usato nel nome del package è stato fornito dallo
  studente.

## Nota per lo studente, prima della consegna

Questa dichiarazione descrive il processo di sviluppo così come è
avvenuto in questa conversazione con Claude. **Prima di consegnare il
progetto**, è importante che tu:

1. legga il codice generato e ti assicuri di comprenderlo (è quello che ti
   verrà chiesto di discutere all'esame);
2. esegua davvero `./gradlew build` e `./gradlew run` sul tuo computer,
   dato che l'ambiente di sviluppo di Claude non ha potuto scaricare le
   dipendenze Java per verificarlo end-to-end (vedi sopra);
3. aggiorni questa pagina se hai modificato, esteso o corretto qualcosa
   del codice generato, specificando cosa hai fatto tu in autonomia.
