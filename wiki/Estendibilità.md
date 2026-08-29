# Estendibilità

La specifica di progetto chiede esplicitamente che l'applicazione sia
progettata per poter essere estesa in futuro (nuove funzionalità, nuove
piattaforme), anche se non tutte le funzionalità devono essere presenti
nella prima release. Questa pagina elenca i punti di estensione pensati
esplicitamente nel progetto.

## Aggiungere un nuovo stile di guida

Basta implementare l'interfaccia `StileDiGuida`:

```java
public final class StileTemerario implements StileDiGuida {
    public String nome() { return "Temerario"; }
    public String descrizione() { return "..."; }
    public Statistiche statisticheIniziali() { return new Statistiche(90, 40, 60, 30); }
}
```

Per renderlo scelto dal giocatore basta aggiungerlo alla lista in
`SchermataCreazionePilota`; per renderlo persistibile, aggiungere una riga
nella mappatura di `ConvertitoreCarriera` (vedi [Persistenza](Persistenza)).
Nessun'altra classe esistente deve essere modificata.

## Aggiungere una nuova abilità

Basta implementare l'interfaccia `Abilita` e registrarla in
`CatalogoAbilita` con il livello a cui si sblocca:

```java
public final class OcchioDiFalco implements Abilita {
    public String nome() { return "Occhio di Falco"; }
    public String descrizione() { return "..."; }
    public int livelloRichiesto() { return 6; }
    public long applica(long tempoReazioneMs) { return Math.max(0, tempoReazioneMs - 50); }
}
```

`GestoreProgressione` la sbloccherà automaticamente al livello giusto,
senza bisogno di altre modifiche.

## Aggiungere una nuova condizione di pista

Basta implementare `CondizionePista` con le proprie soglie (in
millisecondi) e usarla in una nuova `Gara` del catalogo campionati. Il
minigioco della partenza e il motore di gara non cambiano: dipendono solo
dall'interfaccia.

## Aggiungere un nuovo modo di calcolare il piazzamento

`CalcolatorePiazzamentoStandard` è una delle possibili implementazioni di
`CalcolatorePiazzamento`. In futuro si potrebbe aggiungere, ad esempio,
`CalcolatorePiazzamentoConFasiDiGara` che simuli più fasi dopo la partenza
(sorpassi, cadute...), passandolo semplicemente al costruttore di
`MotoreDiGara` al posto dell'implementazione standard: il resto del motore
di gara non cambia.

## Cambiare il meccanismo di persistenza

Come descritto in [Persistenza](Persistenza), basta scrivere una nuova
implementazione di `RepositorySalvataggi<Carriera>` (es. su database o via
rete) e passarla a `HoleshotApplication` al posto di
`RepositorySalvataggiJson`.

## Verso altre piattaforme

La logica di gioco (`model` + `engine`) non dipende in alcun modo da
JavaFX: non importa nessuna classe `javafx.*`. Questo significa che, in
futuro, la stessa logica potrebbe essere riutilizzata da un front-end
diverso (es. un'interfaccia a riga di comando, o un'interfaccia web),
scrivendo nuove classi nel package `gui` (o un package sorella) che
implementino `Navigatore` in modo diverso, senza toccare `model`,
`engine` né `persistence`. La classe `Main`, separata da
`HoleshotApplication`, esiste proprio per rendere esplicito questo punto
di estensione: oggi delega a JavaFX, in futuro potrebbe scegliere quale
front-end avviare.
