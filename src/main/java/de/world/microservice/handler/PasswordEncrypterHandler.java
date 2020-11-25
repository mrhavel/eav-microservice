package de.world.microservice.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Ist ein Attribut
 */
@Service
public class PasswordEncrypterHandler implements Handler<String, String> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${world.passwordIdentifier:PASSWORD%%}")
    private String preIdentifier;

    @Override
    public String preProcessor(String o) {
        if (o.startsWith(preIdentifier)) {
            return o;
        }
        return preIdentifier + passwordEncoder.encode(o);
    }

    @Override
    public String postProcessor(String o) {
        return o.replaceAll(preIdentifier, "");
    }
}
