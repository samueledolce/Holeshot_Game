package it.unicam.cs.mpgc.rpg129875.persistence;

/**
 * Segnala che i file dei contenuti di gioco mancano o contengono dati non
 * validi.
 *
 * <p>È un'eccezione non controllata perché non è un errore da cui il
 * programma possa riprendersi durante l'uso: i contenuti fanno parte
 * dell'applicazione, quindi un file sbagliato va corretto da chi sviluppa.
 * Meglio fermarsi subito all'avvio con un messaggio chiaro che proseguire
 * con un gioco senza moto o senza gare.</p>
 */
public class CatalogoNonValidoException extends RuntimeException {

    public CatalogoNonValidoException(String messaggio) {
        super(messaggio);
    }

    public CatalogoNonValidoException(String messaggio, Throwable causa) {
        super(messaggio, causa);
    }
}
