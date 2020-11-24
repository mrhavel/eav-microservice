package de.world.microservice.handler;

/**
 * Gibt die Möglichkeit Daten vor Speicherung zu verändern
 * oder nach dem Laden zu ergänzen.
 *
 * Motivation waren Passwörter.
 */
public interface Handler<IN, OUT> {

    /**
     * Verarbeitung VOR Speicherung
     * @param o
     * @return
     */
    OUT preProcessor(IN o);

    /**
     * Verarbeitung NACH dem Laden des Objektes
     * @param o
     * @return
     */
    OUT postProcessor(IN o);
}
