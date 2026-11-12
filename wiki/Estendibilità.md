# Estendibilità

La specifica di progetto chiede esplicitamente che l'applicazione sia
progettata per poter essere estesa in futuro (nuove funzionalità, nuove
piattaforme), anche se non tutte le funzionalità devono essere presenti
nella prima release. Questa pagina elenca i punti di estensione pensati
esplicitamente nel progetto, dal più semplice al più impegnativo.

## Aggiungere contenuti: solo file JSON, niente codice

Moto, gare, campionati e obiettivi sponsor sono dati, e stanno nei file
della cartella `src/main/resources/it/unicam/cs/mpgc/rpg129875/dati/`.

**Una nuova moto**: una riga in `moto.json`. Compare da sola nel negozio
(o tra le moto di serie, se il prezzo è 0).

```json
{ "nome": "Fulmine 250 Factory", "categoria": "MX_250", "potenza": 88, "maneggevolezza": 84, "bonusPartenza": 82, "prezzo": 1350 }
```

**Una nuova gara o un nuovo campionato**: un blocco in `campionati.json`.
L'ordine nel file è l'ordine in cui i campionati si affrontano.

```json
{
  "nome": "Supercross delle Nazioni",
  "tappe": [
    { "nomeTappa": "Finale di Parigi", "pistaChiave": "ASCIUTTA", "numeroAvversari": 15, "difficolta": 36 }
  ]
}
```

**Un nuovo obiettivo sponsor**: una riga in `obiettivi.json`, scegliendo
uno dei criteri esistenti (`PODIO`, `VITTORIA`, `HOLESHOT`,
`PARTENZA_PERFETTA`).

```json
{ "descrizione": "Vinci 10 gare", "criterioChiave": "VITTORIA", "traguardo": 10, "ricompensa": 1000 }
```

I file vengono controllati all'avvio e dal test `CatalogoJsonTest`: un
errore di battitura (una categoria o un codice che non esiste, un prezzo
negativo...) viene segnalato con un messaggio che indica cosa correggere.

Anche il bilanciamento del gioco si regola così: prezzi delle moto e
difficoltà delle gare sono stati ritoccati modificando solo questi file.

## Aggiungere comportamenti: una classe e una riga nel registro

Gli elementi che hanno una logica propria sono classi che implementano
un'interfaccia. Per aggiungerne uno servono due passi: scrivere la classe
e aggiungerla all'elenco corrispondente in `Registri`. Da quel momento è
disponibile ovunque tramite il suo codice: nelle schermate, nei file dei
contenuti e nei salvataggi, senza modificare altre classi.

**Un nuovo stile di guida**: implementare `StileDiGuida`.

```java
public final class StileTemerario implements StileDiGuida {
    public String codice() { return "TEMERARIO"; }
    public String nome() { return "Temerario"; }
    public String descrizione() { return "..."; }
    public Statistiche statisticheIniziali() { return new Statistiche(90, 40, 60, 30); }
}
```

Una volta aggiunto a `Registri.stiliDiGuida()` compare nella schermata di
creazione del pilota e viene salvato e ricaricato correttamente.

**Una nuova condizione di pista**: implementare `CondizionePista` con le
proprie soglie (in millisecondi) e registrarla in
`Registri.condizioniPista()`. Il suo codice si può usare subito in
`campionati.json`. Il minigioco della partenza e il motore di gara non
cambiano: dipendono solo dall'interfaccia. Per darle uno sfondo dedicato
basta una regola nel foglio di stile, con il codice in minuscolo:

```css
.sfondo-pista-ghiacciata { -fx-background-image: url("sfondo-pista-ghiacciata.jpg"); }
```

**Un nuovo criterio per gli obiettivi**: spesso non serve nemmeno una
classe nuova, perché i due criteri esistenti sono parametrici. Un obiettivo
"arriva tra i primi cinque" è una riga in `Registri.criteriObiettivo()`:

```java
new CriterioPiazzamento("PRIMI_CINQUE", 5)
```

Per una condizione diversa (es. "vinci su pista fangosa") si implementa
`CriterioObiettivo`.

**Una nuova abilità**: implementare `Abilita` e registrarla in
`CatalogoAbilita` con il livello a cui si sblocca. `GestoreProgressione` la
sbloccherà automaticamente al livello giusto.

```java
public final class OcchioDiFalco implements Abilita {
    public String nome() { return "Occhio di Falco"; }
    public String descrizione() { return "..."; }
    public int livelloRichiesto() { return 6; }
    public long applica(long tempoReazioneMs) { return Math.max(0, tempoReazioneMs - 50); }
}
```

## Sostituire una regola di gioco: una nuova implementazione

**Il calcolo del piazzamento.** `CalcolatorePiazzamentoStandard` è una
delle possibili implementazioni di `CalcolatorePiazzamento`. In futuro si
potrebbe aggiungere, ad esempio, `CalcolatorePiazzamentoConFasiDiGara` che
simuli più fasi dopo la partenza (sorpassi, cadute...), passandolo al
costruttore di `MotoreDiGara` al posto dell'implementazione standard: il
resto del motore di gara non cambia.

**La generazione degli avversari.** Allo stesso modo un
`GeneratoreAvversari` diverso potrebbe proporre rivali fissi, con un nome
e una storia, letti da un file.

Entrambe le sostituzioni si fanno in un solo punto:
`HoleshotApplication.init()`, dove vengono create le implementazioni
concrete.

## Cambiare la fonte dei dati

**I salvataggi.** Come descritto in [Persistenza](Persistenza), basta
scrivere una nuova implementazione di `RepositorySalvataggi<Carriera>` (es.
su database o via rete) e usarla al posto di `RepositorySalvataggiJson`.

**I contenuti di gioco.** `Negozio` e `GestoreCarriera` dipendono dalle
interfacce `CatalogoMoto`, `CatalogoCampionati` e `CatalogoObiettivi`, non
da `CatalogoJson`. Una versione online del gioco potrebbe scaricare moto e
campionati da un server implementando quelle stesse interfacce.

## Cambiare l'aspetto

Colori, caratteri, sfondi e dimensioni sono nel foglio di stile
`grafica/holeshot.css`, non nel codice Java: un tema diverso si ottiene
modificando quel file. Gli sfondi sono generati dallo script
`strumenti/genera_sfondi.py`, ma possono essere sostituiti con qualunque
immagine che abbia lo stesso nome.

## Verso altre piattaforme

La logica di gioco (`model` + `engine`) non dipende in alcun modo da
JavaFX: non importa nessuna classe `javafx.*`. Questo significa che, in
futuro, la stessa logica potrebbe essere riutilizzata da un front-end
diverso (es. un'interfaccia a riga di comando, o un'interfaccia web),
scrivendo nuove classi nel package `gui` (o un package sorella) che
implementino `Navigatore` in modo diverso, senza toccare `model`,
`engine` né `persistence`. Anche le regole che una schermata potrebbe
essere tentata di contenere (creare una carriera, passare al campionato
successivo, comprare una moto) stanno in `engine`, proprio per poter
essere riusate. La classe `Main`, separata da `HoleshotApplication`, esiste
per rendere esplicito questo punto di estensione: oggi delega a JavaFX, in
futuro potrebbe scegliere quale front-end avviare.
