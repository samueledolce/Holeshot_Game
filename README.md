# 🏍️ Holeshot

Un gioco di ruolo di carriera motocross incentrato sulla partenza al cancelletto:
crei un pilota, scegli il suo stile di guida e la sua moto, e affronti un
campionato in cui **la partenza è tutto**. Un minigioco di reazione
determina la qualità della partenza, che insieme alle statistiche del
pilota decide il piazzamento finale di ogni gara. Vincendo gare il pilota
guadagna esperienza, sale di livello, sblocca abilità e accumula crediti
sponsor.

Progetto sviluppato per l'esame di Metodologie di Programmazione e
Modellazione della Conoscenza (AA 2025/26), Università di Camerino.

---

## 🚀 Come eseguire il progetto

### Prerequisiti
- JDK 21 (LTS) o superiore
- Connessione a Internet al primo avvio (Gradle scarica JavaFX, Gson e JUnit da Maven Central)

### Istruzioni

```bash
git clone <url-del-repository>
cd holeshot
```

### Build del progetto
```bash
./gradlew build
```

### Esecuzione
```bash
./gradlew run
```

Si aprirà la finestra dell'applicazione JavaFX. I salvataggi vengono creati
nella cartella `saves/` (in formato JSON leggibile).

---

## 🎮 Come si gioca

1. **Nuova carriera**: scegli nome, stile di guida (Aggressivo / Tecnico /
   Equilibrato) e moto iniziale.
2. **Al cancelletto**: quando il rettangolo diventa verde e appare "VIA!!!",
   premi **SCATTA!** il più velocemente possibile. Premerlo troppo presto è
   una falsa partenza.
3. **Risultato**: la qualità della partenza, le statistiche del pilota e la
   moto determinano il piazzamento in gara. Guadagni esperienza e crediti
   sponsor, puoi salire di livello e sbloccare nuove abilità.
4. **Salva** la carriera in qualsiasi momento dalla schermata di carriera e
   ricaricala dal menu principale.

## 🏗️ Architettura (in breve)

Il progetto è organizzato in quattro package sotto
`it.unicam.cs.mpgc.rpg129875`:

- `model` — le entità di dominio (Pilota, Moto, Abilità, Condizioni di
  pista, Campionato, Carriera...) e i contratti (interfacce) che permettono
  di estendere il gioco senza modificare il codice esistente.
- `engine` — la logica di gioco (progressione, generazione avversari,
  calcolo del piazzamento, risoluzione della partenza), separata dal
  modello e dalla interfaccia grafica.
- `persistence` — il salvataggio/caricamento su file JSON, nascosto dietro
  un'interfaccia generica (`RepositorySalvataggi<T>`) per poter cambiare
  meccanismo di persistenza in futuro senza toccare il resto del codice.
- `gui` — l'interfaccia grafica JavaFX, che dipende solo dalle astrazioni
  di `engine`/`persistence`/`model`, mai il contrario.

Per la descrizione dettagliata delle responsabilità di ogni classe, delle
scelte di design e dei meccanismi di estendibilità, vedi la **Wiki** del
repository.

---

## 🤖 Uso di strumenti di AI

Questo progetto è stato sviluppato in collaborazione con **Claude**
(Anthropic), usato come assistente di sviluppo per:

- discutere e progettare insieme il concept del gioco (un GdR di carriera
  motocross incentrato sulla partenza al cancelletto) e l'architettura
  del software (package, interfacce, rispetto dei principi SOLID);
- generare una prima versione del codice sorgente (modello di dominio,
  motore di gioco, persistenza JSON, interfaccia grafica JavaFX, test);
- generare e verificare i test JUnit e degli smoke test manuali eseguiti
  durante lo sviluppo per validare la logica di progressione, di
  piazzamento gara e di conversione per la persistenza;
- scrivere la documentazione (README e Wiki).

La logica di dominio e del motore di gioco (che non dipende da librerie
esterne) è stata compilata ed eseguita realmente durante lo sviluppo con
`javac`/`java`, oltre ai test JUnit inclusi nel progetto. L'ambiente di
sviluppo cloud usato da Claude non aveva invece accesso a Internet per
scaricare le dipendenze Java (JavaFX, Gson, JUnit), quindi la parte di
interfaccia grafica e le chiamate dirette a Gson non hanno potuto essere
compilate in quell'ambiente: vanno collaudate con `./gradlew build` /
`./gradlew run` su una macchina con normale accesso a Internet. Per la
dichiarazione completa sull'uso dell'AI (compresa questa limitazione), vedi
la pagina *Dichiarazione uso AI* nella Wiki del repository.

---

## 📄 Licenza

Progetto accademico, sviluppato a scopo didattico.
