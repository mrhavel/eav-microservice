package de.world.microservice.controllers;

import de.world.microservice.handler.Handler;
import de.world.microservice.logic.meta.DomainAttribute;
import de.world.microservice.logic.meta.DomainAttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Die Idee ist, dass wir das Domänenobjekt hier beschreiben
 * um mehr dynamik zu erlauben
 */
@RestController
@RequestMapping("/meta")
public class MetaController {

    @Autowired
    private DomainAttributeRepository repository;

    @Autowired
    private ApplicationContext context;

    /**
     * Attribute, die wir in der Datenbank haben
     * @return Eine Liste von DomänenAttributen
     */
    @GetMapping("attributes")
    public  ResponseEntity<DomainAttribute> attributes() {
        if (repository.count() == 0)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity(repository.findAll(), HttpStatus.OK);
    }

    /**
     * Wir fragen den Kontext und geben die Ergebnisse zurück
     * @return Eine Liste von Handler-Objekten, die im Kontext gefunden werden
     */
    @GetMapping("handlers")
    public ResponseEntity handlers() {
         Map<String, Handler> data = context.getBeansOfType(Handler.class);
         return new ResponseEntity(data.keySet(), HttpStatus.OK);
    }

}
