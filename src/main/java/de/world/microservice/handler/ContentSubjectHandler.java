package de.world.microservice.handler;

import org.springframework.stereotype.Service;

/**
 * Nur ein Beispiel. Ich hab gerade nix im SInn, ob es nützlich ist.
 */
@Service
public class ContentSubjectHandler implements Handler<String, String> {

    @Override
    public String preProcessor(String o) {
        return o;
    }

    @Override
    public String postProcessor(String o) {
        if (o == null || o.isEmpty()) {
            return "Unbekannter Titel";
        }
        return o;
    }
}
