# Funzionalità implementate

## Gestione della carriera

- Creazione di un nuovo pilota: nome, stile di guida (**Aggressivo**,
  **Tecnico**, **Equilibrato** — ognuno con statistiche iniziali diverse) e
  moto di serie scelta dal catalogo.
- Tre campionati da affrontare uno dopo l'altro (**Rookie**, **Nazionale**,
  **Mondiale**), ciascuno composto da più tappe su piste con condizioni
  diverse (asciutta, fangosa, sabbiosa). A ogni campionato gli avversari
  sono più numerosi e più forti.
- Progressione del pilota: guadagno di esperienza e di crediti sponsor al
  termine di ogni gara, salita di livello con crescita delle statistiche,
  sblocco di abilità legate al livello raggiunto.
- Storico dei risultati ottenuti durante la carriera.

## Il minigioco della partenza

- Un cancelletto che si apre dopo un'attesa casuale (diversa per ogni
  condizione di pista): la luce passa da rossa a verde e la sbarra cade.
- Il giocatore deve reagire il più velocemente possibile premendo il
  pulsante "SCATTA!".
- Premere il pulsante **prima** dell'apertura è una **falsa partenza**,
  penalizzata pesantemente nel calcolo del piazzamento.
- Il tempo di reazione misurato viene corretto dalle abilità sbloccate dal
  pilota (es. "Sesto Senso" riduce il tempo percepito), poi classificato
  in una qualità di partenza (Falsa partenza, Lenta, Buona, Ottima,
  Perfetta) in base a soglie specifiche della pista corrente.

## Risoluzione della gara

- Gli avversari vengono generati con nomi e prestazioni casuali. La loro
  forza dipende dalla difficoltà della gara e, in misura minore, dal
  livello del giocatore.
- Il piazzamento finale combina: qualità della partenza, statistiche del
  pilota, qualità della moto e una piccola componente casuale.
- Il gioco è tarato perché sia la partenza a decidere: un pilota alle prime
  armi arriva a metà gruppo con una buona partenza e vince quasi sempre
  con una partenza perfetta. Un test automatico
  (`BilanciamentoGaraTest`) lo verifica simulando centinaia di gare.
- Il giocatore vede la classifica completa e la propria posizione, oltre
  a ciò che ha ottenuto (esperienza, crediti, nuove abilità, obiettivi
  completati).

## Obiettivi sponsor

- A inizio carriera lo sponsor propone alcuni obiettivi: salire sul podio,
  vincere, fare partenze perfette, conquistare holeshot (partenze ottime o
  perfette).
- Ogni gara fa avanzare gli obiettivi che la riguardano. Al completamento
  il pilota riceve una ricompensa in crediti, una sola volta.
- I progressi sono visibili nella schermata di carriera e vengono salvati
  insieme alla carriera.

## Negozio moto

- I crediti sponsor si spendono nel negozio per moto migliori (versioni
  Racing, Factory e Ufficiale).
- Il pilota ha una sola moto alla volta: comprandone una nuova cede la
  vecchia in permuta, e metà del suo prezzo viene scalata dal costo.
- Per ogni moto il negozio mostra le caratteristiche, il confronto con la
  moto attuale e il costo effettivo; l'acquisto è possibile solo se i
  crediti bastano.

## Persistenza

- Salvataggio e caricamento della carriera su file JSON, con nome scelto
  dal giocatore.
- Elenco dei salvataggi disponibili, consultabile dal menu principale.
- Le operazioni di salvataggio/caricamento avvengono su un thread separato
  per non bloccare l'interfaccia grafica durante l'accesso al disco.
- Un file danneggiato o non compatibile viene segnalato al giocatore con un
  messaggio, senza far cadere l'applicazione.
- I salvataggi creati con la prima versione del gioco si caricano ancora.

## Contenuti di gioco modificabili

Moto, campionati, gare e obiettivi sponsor sono descritti in file JSON
inclusi nell'applicazione (vedi [Persistenza](Persistenza)): per
aggiungere una moto o un campionato basta modificare un file di testo.

## Interfaccia grafica

Realizzata con JavaFX: menu principale, creazione del pilota, schermata di
carriera, negozio, presentazione della gara, minigioco della partenza,
schermata dei risultati. La navigazione passa attraverso un'unica finestra,
il cui contenuto viene sostituito con una dissolvenza (vedi
[Architettura e Responsabilità](Architettura-e-Responsabilità)).

Ogni schermata ha uno sfondo: quelle di gara mostrano il rettilineo di
partenza nella condizione di pista della tappa (asciutto, fango, sabbia).
Colori, caratteri e sfondi sono definiti in un foglio di stile separato dal
codice. Gli sfondi sono immagini originali, disegnate da uno script incluso
nel repository.

## Cosa non è (ancora) implementato, e perché

Come chiarito a lezione, l'obiettivo del progetto non è realizzare un GdR
motocross completo, ma un sistema software ben progettato che usi il GdR
come scenario applicativo. Alcune funzionalità sono state volutamente
lasciate fuori (es. una simulazione di gara più dettagliata dopo la
partenza, componenti da montare sulla moto, effetti sonori), ma
l'architettura è pensata apposta per poterle aggiungere in futuro senza
stravolgere il codice esistente: vedi la pagina
[Estendibilità](Estendibilità).
