# Dichiarazione dettagliata di uso di strumenti di AI

Come richiesto dalla specifica di progetto, questa pagina descrive nel
dettaglio quali strumenti di intelligenza artificiale sono stati usati
nella realizzazione di questo progetto, per quali attività e con quale
livello di intervento personale.

## Strumento usato

**Claude** (Anthropic), nell'ambiente Cowork, usato in modalità
sviluppo integrato. 

## Scopo e ambito di utilizzo

1. **Progettazione del concept del gioco**:lo studente ha proposto l'idea
   di un GdR ambientato nel motocross, incentrato sulla partenza al
   cancelletto (elemento tecnico reale e importante nella disciplina).
2. **Progettazione dell'architettura software**: individuazione dei
   package, delle classi, delle interfacce e delle rispettive
   responsabilità, con l'obiettivo esplicito di rispettare i principi
   SOLID e la pulizia del codice richiesti dalla specifica d'esame.
3. **Generazione del codice sorgente**: codice sorgente Java
   (modello di dominio, motore di gioco, persistenza JSON, interfaccia
   grafica JavaFX) è stato impostato da Clade e modificato dallo studente, seguendo l'architettura
   concordata.
4. **Generazione dei test** JUnit inclusi nel progetto.
5. **Scrittura della documentazione**: README e Wiki.

## Controllo e Validazione da parte dello Studente
Tutto il codice proposto o suggerito dallo strumento di Intelligenza Artificiale è stato:
1. **Analizzato nel dettaglio:** Ogni metodo, ciclo ed eccezione è stato compreso prima dell'integrazione nel codice sorgente.
2. **Validato e testato:** Sono stati effettuati test manuali e build automatiche (tramite Gradle) per assicurare che il comportamento rispondesse esattamente alle richieste.
3. **Modificato per aderenza didattica:** Alcune strutture dati o pattern sono stati modificati per allinearsi esattamente alle metodologie di programmazione illustrate a lezione.

La titolarità logica e concettuale dell'applicativo e della sua architettura fa interamente capo allo studente.