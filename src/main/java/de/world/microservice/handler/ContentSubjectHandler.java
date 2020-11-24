package de.world.microservice.handler;

import org.springframework.stereotype.Service;

/**
 * Dies hier ist nur ein fast nutzloses Beispiel
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
