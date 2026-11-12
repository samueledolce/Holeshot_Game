# 🏍️ Holeshot

Un gioco di ruolo di carriera motocross incentrato sulla partenza al cancelletto:
crei un pilota, scegli il suo stile di guida e la sua moto, e affronti tre
campionati di difficoltà crescente in cui **la partenza è tutto**. Un
minigioco di reazione determina la qualità della partenza, che insieme alle
statistiche del pilota e alla moto decide il piazzamento finale di ogni
gara. Gara dopo gara il pilota guadagna esperienza, sale di livello, sblocca
abilità, raggiunge gli obiettivi dello sponsor e accumula crediti con cui
comprare moto migliori.

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
   Equilibrato) e moto di serie.
2. **Al cancelletto**: quando la luce diventa verde e la sbarra cade, premi
   **SCATTA!** il più velocemente possibile. Premerlo troppo presto è una
   falsa partenza.
3. **Risultato**: la qualità della partenza, le statistiche del pilota e la
   moto determinano il piazzamento. Guadagni esperienza e crediti sponsor,
   puoi salire di livello e sbloccare nuove abilità.
4. **Obiettivi sponsor**: podi, vittorie e partenze perfette fanno avanzare
   gli obiettivi dello sponsor, che pagano una ricompensa in crediti.
5. **Negozio**: con i crediti compri una moto migliore, cedendo la tua in
   permuta. Serve: gli avversari diventano più forti a ogni campionato.
6. **Campionati**: concluso il Campionato Rookie si passa al Nazionale e
   poi al Mondiale.
7. **Salva** la carriera in qualsiasi momento dalla schermata di carriera e
   ricaricala dal menu principale.

## 🏗️ Architettura (in breve)

Il progetto è organizzato in quattro package sotto
`it.unicam.cs.mpgc.rpg129875`:

- `model` — le entità di dominio (Pilota, Moto, Abilità, Condizioni di
  pista, Campionato, Obiettivi sponsor, Carriera...) e i contratti
  (interfacce) che permettono di estendere il gioco senza modificare il
  codice esistente.
- `engine` — la logica di gioco (progressione, generazione avversari,
  calcolo del piazzamento, negozio, obiettivi, percorso di carriera),
  separata dal modello e dalla interfaccia grafica.
- `persistence` — la lettura e scrittura dei file JSON: i salvataggi, dietro
  l'interfaccia generica `RepositorySalvataggi<T>`, e i contenuti di gioco.
- `gui` — l'interfaccia grafica JavaFX, che dipende solo dalle astrazioni
  di `engine`/`persistence`/`model`, mai il contrario.

I **contenuti di gioco** (moto, campionati, gare, obiettivi) non sono
scritti nel codice: stanno in file JSON in
`src/main/resources/.../dati/`, e si possono modificare o ampliare senza
toccare il codice Java. L'**aspetto** dell'applicazione sta nel foglio di
stile `src/main/resources/.../grafica/holeshot.css`.

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
- generare il codice sorgente (modello di dominio, motore di gioco,
  persistenza JSON, interfaccia grafica JavaFX) e i test JUnit;
- disegnare gli sfondi, tramite uno script che li genera da zero
  (`strumenti/genera_sfondi.py`): non derivano da immagini di terzi;
- scrivere la documentazione (README e Wiki).

La logica di gioco è coperta dai test JUnit del progetto, che Claude ha
eseguito durante lo sviluppo. L'ambiente cloud usato da Claude non può però
scaricare le librerie Java (JavaFX, Gson, JUnit): per compilare ed eseguire
i test ha usato delle sostitute scritte apposta, e non ha potuto avviare
l'interfaccia grafica. La verifica con le librerie vere (`./gradlew build`
e `./gradlew run`) avviene sul computer dello studente. Per
la dichiarazione completa, vedi la pagina *Dichiarazione uso AI* nella Wiki
del repository.

---

## 📄 Licenza

Progetto accademico, sviluppato a scopo didattico.
