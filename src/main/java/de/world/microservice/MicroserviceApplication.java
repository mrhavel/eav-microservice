package de.world.microservice;

import de.world.microservice.communications.EnableRabbitCommunications;
import de.world.microservice.communications.consumers.IConsumer;
import de.world.microservice.logic.meta.DomainAttribute;
import de.world.microservice.logic.meta.DomainAttributeRepository;
import de.world.microservice.communications.producers.IProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootApplication
//@EnableRabbitCommunications Currently not necessary
public class MicroserviceApplication {

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
			log.info("Creating Attribute Contents for Domain Object");
			// @TODO Funktioniert nicht, wenn die DomänenAttribut Definition in der Jar liegt
			File fil = new File(this.getClass().getClassLoader().getResource("attributes.json").toURI());
			if (!fil.exists()) {
				log.error("File not found: attributes.json");
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
				Map<String,String> cont =
						(Map<String,String>) attrib;

				DomainAttribute attribute = new DomainAttribute();
				attribute.setType(cont.get("type"));
				attribute.setName(cont.get("name"));

				attribute.setOptional(Boolean.parseBoolean(cont.get("optional")));
				attribute.setHandlerClass(cont.get("handlerClass"));
				liste.add(attribute);
			}

			attributeRepository.saveAll(liste);
		}

		log.info("Attributes for Domain Object: {}", attributeRepository.count());
		// Vorsicht bei this.getClass - da kann ein Spring Proxy vorkommen, der
		// die Annotation nicht hat
		checkIfRabbitAnnotationIsSet(MicroserviceApplication.class);
	}

	/**
	 * F+E Methode
	 *
	 * @param clazz - Vorsicht bei der Klasse. Spring setzt eigene Proxies ein.
	 */
	public void checkIfRabbitAnnotationIsSet(Class clazz) {

		if (clazz.isAnnotationPresent(EnableRabbitCommunications.class)) {

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
}
