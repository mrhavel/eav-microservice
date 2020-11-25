package de.world.microservice;

import de.world.microservice.communications.EnableRabbitCommunications;
import de.world.microservice.communications.consumers.IConsumer;
import de.world.microservice.logic.meta.DomainAttribute;
import de.world.microservice.logic.meta.DomainAttributeRepository;
import de.world.microservice.communications.producers.IProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.util.AnnotatedTypeScanner;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * Microservice Main File
 *
 * - Prüfung der Anforderungen für den Microservice
 *
 *
 */
@Slf4j
@SpringBootApplication
// @EnableRabbitCommunications // Currently not necessary
public class MicroserviceApplication {

    @Value("${spring.basePackage:de.world.microservice}")
    private String microserviceBasePackage;

    @Value("${domainAttributeDefinition:attributes.json}")
    private String attributesFile;

    @Autowired
    private DomainAttributeRepository attributeRepository;

    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceApplication.class, args);
    }

    @PostConstruct
    public void initialize() throws URISyntaxException, IOException {
        if (attributeRepository.count() == 0) {
            importAttributesIfNeeded();
        }

        log.info("Attributes for Domain Object: {}", attributeRepository.count());

        // Springs TypeScanner sagt uns, wenn wir den Rabbitlayer zu erwarten haben
        AnnotatedTypeScanner scanner =
                new AnnotatedTypeScanner(true, EnableRabbitCommunications.class);
        Set annotatedTypes = scanner.findTypes(microserviceBasePackage);
        if (!annotatedTypes.isEmpty()) {
            checkIfRabbitAnnotationIsSet();
        }
    }


    /**
     * Task 1 -     Prüfung, ob wir die Definitionen für die DomainAttributes haben
     *              Ohne die Attribute kennen wir das Objekt nicht wirklich
     *
     * @throws IOException          - attributes.json konnte nicht gelesen werden
     * @throws URISyntaxException   - Wir finden die Json File nicht
     */
    public void importAttributesIfNeeded() throws IOException, URISyntaxException {
        log.info("Creating Attribute Entities for Domain Object");

        File fil = new File(attributesFile);        // Für richtige Pfade
        if (!fil.exists())                          // Falls die Pfade nicht passen, suchen wir die Datei
            fil = new File(this.getClass().getClassLoader().getResource(attributesFile).toURI());

        if (!fil.exists()) {
            log.error("File not found: {}", attributesFile);
            System.exit(-1);
        }

        String jsonContents =
                Files.readString(fil.toPath());

        JsonParser parser =
                JsonParserFactory.getJsonParser();

        List<Object> domainAttributeListe =
                parser.parseList(jsonContents);

        List<DomainAttribute> liste =
                new ArrayList<>();

        for (Object attrib : domainAttributeListe) {
            Map<String, String> cont =
                    (Map<String, String>) attrib;

            DomainAttribute attribute = new DomainAttribute();
            attribute.setType(cont.get("type"));
            attribute.setName(cont.get("name"));

            attribute.setOptional(Boolean.parseBoolean(cont.get("optional")));
            attribute.setHandlerClass(cont.get("handlerClass"));
            liste.add(attribute);
        }

        attributeRepository.saveAll(liste);
    }

    /**
     * Wir prüfen hier die RabbitMQ Einstellungen. Wenn der Microservice als RabbitMQ
     * Service annn
     */
    public void checkIfRabbitAnnotationIsSet() {


        // Der Microservice ist mit der o.g. Annotation ein Service, der mit einer
        // MessageQueue arbeiten soll (nicht darf).

        log.info("***********************************************");
        log.info("* MessageQueue Annotation loaded. Checking");
        log.info("***********************************************");

        // Checking, if Producer and/or Consumer exists
        Map consumers =
                context.getBeansOfType(IConsumer.class);

        Map producers =
                context.getBeansOfType(IProducer.class);

        if (consumers.isEmpty() && producers.isEmpty()) {
            log.error("Neither a consumer nor producer is defined.");
            log.error("You should add a producer, a consumer or remove the Annotation");
            System.exit(-1); // Die Konfiguration des Microservices ist widersprüchlich. Wir stoppen hard
        }
    }
}
