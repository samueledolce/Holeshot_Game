package it.unicam.cs.mpgc.rpg129875.persistence;

/** Eccezione controllata sollevata quando un'operazione di persistenza non va a buon fine. */
public class PersistenzaException extends Exception {

    public PersistenzaException(String messaggio, Throwable causa) {
        super(messaggio, causa);
    }

    public PersistenzaException(String messaggio) {
        super(messaggio);
    }
}
