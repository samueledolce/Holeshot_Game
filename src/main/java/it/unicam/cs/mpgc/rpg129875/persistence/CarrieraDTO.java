package it.unicam.cs.mpgc.rpg129875.persistence;

import java.util.List;

/**
 * Rappresentazione "piatta" di una {@code Carriera} usata esclusivamente
 * per la serializzazione JSON. Contiene solo tipi semplici (stringhe,
 * numeri, altri DTO): Gson può quindi serializzarla e deserializzarla senza
 * bisogno di adattatori personalizzati per le interfacce del modello di
 * dominio (StileDiGuida, Abilita, CondizionePista).
 */
final class CarrieraDTO {

    PilotaDTO pilota;
    CampionatoDTO campionato;
    List<ObiettivoDTO> obiettivi;
    List<String> storicoRisultati;
}
