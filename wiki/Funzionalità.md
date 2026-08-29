# Funzionalità implementate

## Gestione della carriera

- Creazione di un nuovo pilota: nome, stile di guida (**Aggressivo**,
  **Tecnico**, **Equilibrato** — ognuno con statistiche iniziali diverse) e
  moto di partenza scelta da un catalogo.
- Un campionato composto da più tappe (piste con condizioni diverse:
  asciutta, fangosa, sabbiosa), da affrontare in sequenza.
- Progressione del pilota: guadagno di esperienza e denaro (crediti
  sponsor) al termine di ogni gara, salita di livello con crescita delle
  statistiche, sblocco di abilità legate al livello raggiunto.
- Storico dei risultati ottenuti durante la carriera.

## Il minigioco della partenza

- Un cancelletto virtuale che si apre dopo un'attesa casuale (diversa per
  ogni condizione di pista).
- Il giocatore deve reagire il più velocemente possibile premendo il
  pulsante "SCATTA!".
- Premere il pulsante **prima** dell'apertura è una **falsa partenza**,
  penalizzata pesantemente nel calcolo del piazzamento.
- Il tempo di reazione misurato viene corretto dalle abilità sbloccate dal
  pilota (es. "Sesto Senso" riduce il tempo percepito), poi classificato
  in una qualità di partenza (Falsa partenza, Lenta, Buona, Ottima,
  Perfetta) in base a soglie specifiche della pista corrente.

## Risoluzione della gara

- Gli avversari vengono generati con nomi e prestazioni casuali, scalate
  sul livello del giocatore.
- Il piazzamento finale combina: qualità della partenza, statistiche del
  pilota, qualità della moto e una piccola componente casuale.
- Il giocatore vede la classifica completa e la propria posizione, oltre
  alle ricompense ottenute (esperienza, denaro, eventuali nuove abilità).

## Persistenza

- Salvataggio e caricamento della carriera su file JSON, con nome scelto
  dal giocatore.
- Elenco dei salvataggi disponibili, consultabile dal menu principale.
- Le operazioni di salvataggio/caricamento avvengono su un thread separato
  per non bloccare l'interfaccia grafica durante l'accesso al disco.

## Interfaccia grafica

Realizzata con JavaFX: menu principale, creazione del pilota, schermata
"hub" di carriera, presentazione della gara, minigioco della partenza,
schermata dei risultati. La navigazione tra le schermate passa attraverso
un'unica finestra, il cui contenuto viene sostituito a seconda della
schermata richiesta (vedi [Architettura e Responsabilità](Architettura-e-Responsabilità)).

## Cosa non è (ancora) implementato, e perché

Come chiarito a lezione, l'obiettivo del progetto non è realizzare un GdR
motocross completo, ma un sistema software ben progettato che usi il GdR
come scenario applicativo. Alcune funzionalità sono state volutamente
lasciate fuori dalla prima release (es. un vero negozio per acquistare
nuove moto, più campionati, una simulazione di gara più dettagliata dopo
la partenza), ma l'architettura è pensata apposta per poterle aggiungere
in futuro senza stravolgere il codice esistente: vedi la pagina
[Estendibilità](Estendibilità).
