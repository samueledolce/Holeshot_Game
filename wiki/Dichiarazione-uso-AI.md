# Dichiarazione dettagliata di uso di strumenti di AI

Come richiesto dalla specifica di progetto, questa pagina descrive nel
dettaglio quali strumenti di intelligenza artificiale sono stati usati
nella realizzazione di questo progetto, per quali attività e con quale
livello di intervento personale.

## Strumento usato

**Claude** (Anthropic), nell'ambiente Cowork, usato in modalità
conversazionale e agentica (con accesso a un ambiente di sviluppo cloud:
file system, shell, compilatore Java). Il lavoro si è svolto in due fasi:
una prima versione del gioco e, in seguito, una seconda versione che l'ha
ampliata e migliorata.

## Prima fase: la prima versione del gioco

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
4. **Generazione dei test** JUnit inclusi nel progetto.
5. **Scrittura della documentazione**: README e Wiki.

## Seconda fase: ampliamento e miglioramento

6. **Confronto con un altro progetto.** Su richiesta dello studente, Claude
   ha analizzato alcuni file del progetto di un altro studente dello stesso
   corso, per capire quali aspetti fossero realizzati meglio e quali peggio
   rispetto a Holeshot. Ne sono emerse tre idee da riprendere: tenere i
   contenuti di gioco in file JSON anziché nel codice, dare al giocatore
   più decisioni da prendere, curare di più la grafica. Lo studente ha
   scelto di svilupparle tutte e tre. Da quel progetto sono state riprese
   solo le idee: nessuna riga di codice e nessuna risorsa (immagini,
   musiche, testi) è stata copiata, e la realizzazione in Holeshot è
   indipendente.
7. **Nuove funzionalità**, progettate e scritte da Claude dopo aver chiesto
   allo studente come le voleva (cosa vendere nel negozio, se aggiungere
   suoni, chi dovesse fornire le immagini):
   - contenuti di gioco letti da file JSON (moto, campionati, obiettivi) e
     due nuovi campionati;
   - negozio delle moto con permuta;
   - obiettivi sponsor;
   - nuova grafica: foglio di stile, sfondi, dissolvenze, cancelletto
     animato.
8. **Miglioramenti al codice esistente**, proposti da Claude: un registro
   generico delle implementazioni al posto delle catene di `instanceof`
   nel convertitore dei salvataggi; segnalazione al giocatore dei
   salvataggi danneggiati, che prima fallivano in silenzio.
9. **Correzione del bilanciamento.** Simulando migliaia di gare, Claude ha
   scoperto che nella prima versione il giocatore vinceva sempre, anche
   con una partenza lenta, perché gli avversari erano troppo deboli: il
   contrario dell'idea del gioco. Ha quindi ritarato gli avversari,
   introdotto una difficoltà per ogni gara e aggiunto un test
   (`BilanciamentoGaraTest`) che verifica che sia la partenza a decidere.
10. **Sfondi.** Le cinque immagini di sfondo non provengono da fotografie
    o da raccolte di terzi: sono disegnate da uno script Python scritto da
    Claude (`strumenti/genera_sfondi.py`, incluso nel repository), che le
    compone con forme geometriche e sfumature.
11. **Test e documentazione**: i test JUnit sono passati da 16 a una
    novantina; README e Wiki sono stati aggiornati da Claude.

## Come è stato verificato il codice, e con quali limiti

L'ambiente cloud in cui Claude lavora ha accesso di rete limitato a poche
destinazioni e **non può scaricare le librerie Java del progetto**
(JavaFX, Gson, JUnit, né il plugin Gradle per JavaFX) da Maven Central o
dal Gradle Plugin Portal. Non può quindi eseguire `./gradlew build`.
Questo limite è stato gestito così:

- **Logica di gioco e persistenza** (package `model`, `engine`,
  `persistence`): compilate ed eseguite realmente. Al posto di Gson e
  JUnit, Claude ha usato delle versioni ridotte scritte apposta (non
  incluse nel progetto), sufficienti a eseguire tutti i test del progetto:
  risultavano tutti superati. Con le stesse classi ha simulato oltre mille
  carriere complete, con salvataggio e ricaricamento a ogni gara, senza
  errori.
- **Interfaccia grafica** (package `gui`): compilata contro delle classi
  segnaposto che riproducono solo le firme dei metodi di JavaFX usati.
  Questo esclude errori di sintassi e di tipo nel codice del progetto, ma
  non garantisce che ogni chiamata a JavaFX sia corretta né che le
  schermate abbiano l'aspetto previsto: **Claude non ha mai potuto avviare
  l'interfaccia grafica**. Lo stesso vale per il foglio di stile.
- **Verifica con le librerie vere**: è stata fatta dallo studente sul
  proprio computer. La prima versione è stata compilata ed eseguita con
  `./gradlew build` e `./gradlew run`, e funzionava correttamente.

Questo limite viene dichiarato qui in modo trasparente, come richiesto
dalla specifica, invece di essere nascosto o presentato come una verifica
completa.

## Cosa NON è stato delegato all'AI

- La scelta del tema del progetto (il GdR motocross, con la partenza al
  cancelletto come elemento centrale) è partita dallo studente.
- Le scelte di design proposte da Claude a più opzioni (tecnologia per la
  GUI, profondità del sistema di progressione, modalità di risoluzione
  della gara, gestione degli avversari; nella seconda fase: cosa vendere
  nel negozio, presenza dei suoni, provenienza delle immagini) sono state
  selezionate esplicitamente dallo studente, non decise autonomamente
  dall'AI.
- La decisione di ampliare il progetto e la scelta di quali miglioramenti
  realizzare.
- L'esecuzione e il collaudo del gioco con le librerie vere, sul computer
  dello studente.
- Il numero di matricola usato nel nome del package è stato fornito dallo
  studente.

## Nota per lo studente (da cancellare prima della consegna)

Questa dichiarazione descrive il processo di sviluppo così come è
avvenuto nelle conversazioni con Claude. **Prima di consegnare il
progetto**, è importante che tu:

1. legga il codice generato e ti assicuri di comprenderlo (è quello che ti
   verrà chiesto di discutere all'esame);
2. esegua `./gradlew build` e `./gradlew run` su **questa** versione: nel
   paragrafo sulla verifica c'è scritto che hai collaudato la prima, e va
   aggiornato dicendo com'è andata con la seconda;
3. rilegga il punto 6 sul confronto con un altro progetto e decida se la
   formulazione ti rappresenta: la dichiarazione è tua;
4. aggiorni questa pagina se hai modificato, esteso o corretto qualcosa
   del codice generato, specificando cosa hai fatto tu in autonomia;
5. cancelli questa nota.
