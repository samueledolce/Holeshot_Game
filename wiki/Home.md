# Holeshot — Wiki del progetto

Benvenuto nella Wiki di **Holeshot**, un gioco di ruolo di carriera
motocross incentrato sulla partenza al cancelletto, sviluppato per l'esame
di Metodologie di Programmazione e Modellazione della Conoscenza (AA
2025/26).

Questa Wiki documenta il progetto secondo quanto richiesto dalla specifica
d'esame. Indice delle pagine:

- **[Funzionalità](Funzionalità)** — cosa fa l'applicazione, dal punto di vista del giocatore.
- **[Architettura e Responsabilità](Architettura-e-Responsabilità)** — organizzazione del codice, classi, interfacce e responsabilità di ciascuna.
- **[Persistenza](Persistenza)** — come sono organizzati i dati (salvataggi e contenuti di gioco) e come viene garantito il salvataggio/caricamento.
- **[Estendibilità](Estendibilità)** — i meccanismi pensati per integrare nuove funzionalità senza modificare il codice esistente.
- **[Dichiarazione uso AI](Dichiarazione-uso-AI)** — dettaglio degli strumenti di intelligenza artificiale usati nello sviluppo e a quale scopo.

## Il concept in breve

Nel motocross la partenza è spesso decisiva: chi scatta meglio al
cancelletto parte davanti e ha un vantaggio enorme sul resto della gara.
Holeshot mette il giocatore nei panni di un pilota che cresce nel tempo
(livelli, statistiche, abilità, moto) e affronta tre campionati di
difficoltà crescente in cui ogni gara si apre con un minigioco di reazione
al cancelletto: il tempo di reazione, corretto dalle abilità sbloccate,
determina la qualità della partenza, che insieme alle statistiche del
pilota e alla moto decide il piazzamento finale. I crediti guadagnati in
gara e con gli obiettivi dello sponsor servono a comprare moto migliori,
indispensabili per restare competitivi quando gli avversari crescono.

Il gioco è volutamente **essenziale nel gameplay** (non simula un'intera
gara di motocross) per lasciare spazio a un'architettura software pulita,
estendibile e ben suddivisa per responsabilità — che è l'obiettivo reale
del progetto d'esame.
